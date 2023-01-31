package ch.admin.bag.covidcertificate.web.controller;

import ch.admin.bag.covidcertificate.api.request.CreateNotificationDto;
import ch.admin.bag.covidcertificate.api.request.NotificationContentDto;
import ch.admin.bag.covidcertificate.api.request.NotificationDto;
import ch.admin.bag.covidcertificate.api.request.NotificationType;
import ch.admin.bag.covidcertificate.authorization.AuthorizationService;
import ch.admin.bag.covidcertificate.authorization.config.ServiceData;
import ch.admin.bag.covidcertificate.config.security.OAuth2SecuredWebConfiguration;
import ch.admin.bag.covidcertificate.service.NotificationService;
import ch.admin.bag.covidcertificate.testutil.JwtTestUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.startsWith;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = {NotificationsController.class, OAuth2SecuredWebConfiguration.class})
@ActiveProfiles("test")
@DisplayName("Tests for NotificationControllerSecurity")
class NotificationControllerSecurityTest extends AbstractSecurityTest {
    public static final String COVID_APP_MANAGER = "bag-cc-covid_app_manager";
    private static final String URL = "/api/v1/notifications";
    private final NotificationDto notification = CreateNotificationDto.builder().type(NotificationType.INFO).content(new NotificationContentDto("de", "fr", "it", "en")).isClosable(true).startTime(LocalDateTime.now().minusHours(1)).endTime(LocalDateTime.now().plusHours(1)).build();


    @MockBean
    private AuthorizationService authorizationService;
    @MockBean
    private NotificationService notificationService;

    private ResultMatcher getResultMatcher(HttpStatus status) {
        switch (status) {
            case OK:
                return status().isOk();
            case FORBIDDEN:
                return status().isForbidden();
            case UNAUTHORIZED:
                return status().isUnauthorized();
            case CREATED:
                return status().isCreated();
            case NO_CONTENT:
                return status().isNoContent();
            default:
                throw new IllegalArgumentException("HttpStatus not found!");
        }
    }

    @BeforeEach
    void setupMocks() {
        doNothing().when(notificationService).editNotification(any());

        ServiceData.Function deleteNotifications = createFunction("deleteNotifications", "COVID_APP_MANAGER", List.of(HttpMethod.DELETE));
        deleteNotifications.setUri("/api/v1/notifications/{notificationId}");
        when(authorizationService.identifyFunction(
                eq(AuthorizationService.SERVICE_NOTIFICATIONS),
                startsWith(URL), eq(HttpMethod.DELETE.name()))).thenReturn(List.of(deleteNotifications));
        when(authorizationService.isGranted(Set.of(COVID_APP_MANAGER), deleteNotifications)).thenReturn(true);

        ServiceData.Function getAllNotifications = createFunction("getAllNotifications", "ANY_ROLE", List.of(HttpMethod.GET));
        when(authorizationService.identifyFunction(
                eq(AuthorizationService.SERVICE_NOTIFICATIONS),
                startsWith(URL), eq(HttpMethod.GET.name()))).thenReturn(List.of(getAllNotifications));
        when(authorizationService.isGranted(Set.of(VALID_USER_ROLE), getAllNotifications)).thenReturn(true);

        ServiceData.Function writeNotifications = createFunction("writeNotifications", COVID_APP_MANAGER, List.of(HttpMethod.POST));
        when(authorizationService.identifyFunction(
                eq(AuthorizationService.SERVICE_NOTIFICATIONS),
                startsWith(URL), eq(HttpMethod.POST.name()))).thenReturn(List.of(writeNotifications));
        when(authorizationService.isGranted(Set.of(COVID_APP_MANAGER), writeNotifications)).thenReturn(true);

        when(authorizationService.isUserPermitted(Mockito.anyCollection())).thenReturn(true);
    }

    private ServiceData.Function createFunction(String identifier, String role, List<HttpMethod> methods) {
        ServiceData.Function function = new ServiceData.Function();
        function.setIdentifier(identifier);
        function.setFrom(LocalDateTime.MIN);
        function.setUntil(LocalDateTime.MAX);
        function.setOneOf(List.of(role));
        function.setUri("/api/v1/notifications");
        function.setHttp(methods);
        return function;
    }

    @Nested
    @DisplayName("Tests for readNotification")
    class ReadNotificationsTest {

        @Test
        @DisplayName("Given user is unauthenticated, when called, it should return 401 Unauthorized")
        void readNotificationTest1() throws Exception {
            mockMvc.perform(get(URL)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .header("Authorization", "Bearer"))
                    .andExpect(getResultMatcher(HttpStatus.UNAUTHORIZED));
            Mockito.verify(notificationService, times(0)).readNotifications();
        }

        @Test
        @DisplayName("Given user has invalid role, when called, it should return 403 Forbidden")
        void readNotificationTest2() throws Exception {
            performGet(EXPIRED_IN_FUTURE, INVALID_USER_ROLE, HttpStatus.FORBIDDEN);
            Mockito.verify(notificationService, times(0)).readNotifications();
        }

        @Test
        @DisplayName("Given auth token is expired, when called, it should return 401 Unauthorized")
        void readNotificationTest3() throws Exception {
            performGet(EXPIRED_IN_PAST, VALID_USER_ROLE, HttpStatus.UNAUTHORIZED);
            Mockito.verify(notificationService, times(0)).readNotifications();
        }

        @Test
        @DisplayName("Given user authorized, when called, it should return the notifications")
        void readNotificationTest4() throws Exception {
            performGet(EXPIRED_IN_FUTURE, VALID_USER_ROLE, HttpStatus.OK);
            Mockito.verify(notificationService, times(1)).readNotifications();
        }

        private void performGet(LocalDateTime tokenExpiration, String userRole, HttpStatus status) throws Exception {
            String token = JwtTestUtil.getJwtTestToken(PRIVATE_KEY, tokenExpiration, userRole);
            mockMvc.perform(get(URL)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .header("Authorization", "Bearer " + token))
                    .andExpect(getResultMatcher(status));
        }


    }

    @Nested
    @DisplayName("Tests for writeNotification")
    class WriteNotificationsTest {


        @Test
        @DisplayName("Given user is unauthenticated, when called, it should return 403 Forbidden")
        void writeNotificationTest1() throws Exception {

            mockMvc.perform(MockMvcRequestBuilders.post(URL)
                            .accept(MediaType.ALL_VALUE)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .header("Authorization", "Bearer")
                            .content(mapper.writeValueAsString(notification)))
                    .andExpect(status().isForbidden());
            Mockito.verify(notificationService, times(0)).createNotification(any());
        }

        @Test
        @DisplayName("Given user has invalid role, when called, it should return 403 Forbidden")
        void writeNotificationTest2() throws Exception {
            performPost(EXPIRED_IN_FUTURE, INVALID_USER_ROLE, HttpStatus.FORBIDDEN);
            Mockito.verify(notificationService, times(0)).createNotification(any());
        }

        @Test
        @DisplayName("Given auth token is expired, when called, it should return 401 Unauthorized")
        void writeNotificationTest3() throws Exception {
            performPost(EXPIRED_IN_PAST, VALID_USER_ROLE, HttpStatus.UNAUTHORIZED);
            Mockito.verify(notificationService, times(0)).createNotification(any());
        }

        @Test
        @DisplayName("Given user is authorized, when called, it should return 201 Created")
        void writeNotificationTest4() throws Exception {
            performPost(EXPIRED_IN_FUTURE, COVID_APP_MANAGER, HttpStatus.CREATED);
            Mockito.verify(notificationService, times(1)).createNotification(any());
        }

        private void performPost(LocalDateTime tokenExpiration, String userRole, HttpStatus status) throws Exception {
            String token = JwtTestUtil.getJwtTestToken(PRIVATE_KEY, tokenExpiration, userRole);
            mockMvc.perform(MockMvcRequestBuilders.post(URL)
                            .accept(MediaType.ALL_VALUE)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .header("Authorization", "Bearer " + token)
                            .content(mapper.writeValueAsString(notification)))
                    .andExpect(getResultMatcher(status));
        }

        private void performPut(LocalDateTime tokenExpiration, String userRole, HttpStatus status) throws Exception {
            String token = JwtTestUtil.getJwtTestToken(PRIVATE_KEY, tokenExpiration, userRole);
            mockMvc.perform(MockMvcRequestBuilders.put(URL)
                            .accept(MediaType.ALL_VALUE)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .header("Authorization", "Bearer " + token)
                            .content(mapper.writeValueAsString(notification)))
                    .andExpect(getResultMatcher(status));
        }

    }

    @Nested
    @DisplayName("Tests for removeNotification")
    class RemoveNotificationsTest {

        @Test
        @DisplayName("Given user is unauthenticated, when called, it should return 403 Forbidden")
        void removeNotificationTest1() throws Exception {
            mockMvc.perform(delete(URL)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .header("Authorization", "Bearer"))
                    .andExpect(getResultMatcher(HttpStatus.FORBIDDEN));
            Mockito.verify(notificationService, times(0)).removeNotifications(any());
        }

        @Test
        @DisplayName("Given user has invalid role, when called, it should return 403 Forbidden")
        void removeNotificationTest2() throws Exception {
            performDelete(EXPIRED_IN_FUTURE, INVALID_USER_ROLE, HttpStatus.FORBIDDEN);
            Mockito.verify(notificationService, times(0)).removeNotifications(any());
        }

        @Test
        @DisplayName("Given auth token is expired, when called, it should return 401 Unauthorized")
        void removeNotificationTest3() throws Exception {
            performDelete(EXPIRED_IN_PAST, VALID_USER_ROLE, HttpStatus.UNAUTHORIZED);
            Mockito.verify(notificationService, times(0)).removeNotifications(any());
        }

        @Test
        @DisplayName("Given user is authorized, when called, it should return 200 OK")
        void removeNotificationTest4() throws Exception {
            performDelete(EXPIRED_IN_FUTURE, COVID_APP_MANAGER, HttpStatus.OK);
            Mockito.verify(notificationService, times(1)).removeNotifications(any());
        }

        private void performDelete(LocalDateTime tokenExpiration, String userRole, HttpStatus status) throws Exception {
            String token = JwtTestUtil.getJwtTestToken(PRIVATE_KEY, tokenExpiration, userRole);
            mockMvc.perform(delete(URL + "/" + UUID.randomUUID())
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .header("Authorization", "Bearer " + token))
                    .andExpect(getResultMatcher(status));
        }
    }
}

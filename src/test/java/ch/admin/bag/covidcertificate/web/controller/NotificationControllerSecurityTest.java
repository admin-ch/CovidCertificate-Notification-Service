package ch.admin.bag.covidcertificate.web.controller;

import ch.admin.bag.covidcertificate.api.request.MessageDto;
import ch.admin.bag.covidcertificate.api.request.MessageType;
import ch.admin.bag.covidcertificate.api.request.NotificationDto;
import ch.admin.bag.covidcertificate.api.security.ServiceDataDto;
import ch.admin.bag.covidcertificate.config.security.OAuth2SecuredWebConfiguration;
import ch.admin.bag.covidcertificate.service.NotificationService;
import ch.admin.bag.covidcertificate.testutil.JwtTestUtil;
import ch.admin.bag.covidcertificate.web.security.AuthorizationClient;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
    private final List<NotificationDto> notifications = List.of(new NotificationDto(MessageType.INFO, new MessageDto("de", "fr", "it", "en"), LocalDateTime.now().minusHours(1), LocalDateTime.now().plusHours(1)));
    @MockBean
    private AuthorizationClient authorizationClient;
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
        doNothing().when(notificationService).writeNotifications(any());
        Map<String, String> roleMap = Map.of(VALID_USER_ROLE, "CERTIFICATE_CREATOR", COVID_APP_MANAGER, "COVID_APP_MANAGER");
        when(authorizationClient.requestRoleMap()).thenReturn(roleMap);

        ServiceDataDto.Function getAllNotifications = createFunction("getAllNotifications", roleMap.get(VALID_USER_ROLE), List.of(HttpMethod.GET));
        ServiceDataDto.Function writeOrDeleteNotifications = createFunction("writeOrDeleteNotifications", roleMap.get(COVID_APP_MANAGER), List.of(HttpMethod.POST, HttpMethod.DELETE));

        HashMap<String, ServiceDataDto.Function> functions = new HashMap<>();
        functions.put(getAllNotifications.getIdentifier(), getAllNotifications);
        functions.put(writeOrDeleteNotifications.getIdentifier(), writeOrDeleteNotifications);

        ServiceDataDto serviceDataDto = new ServiceDataDto();
        serviceDataDto.setFunctions(functions);
        when(authorizationClient.requestServiceDefinition()).thenReturn(Optional.of(
                serviceDataDto
        ));
    }

    private ServiceDataDto.Function createFunction(String identifier, String role, List<HttpMethod> methods) {
        ServiceDataDto.Function function = new ServiceDataDto.Function();
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
            performGet(EXPIRED_IN_FUTURE, VALID_USER_ROLE, HttpStatus.NO_CONTENT);
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
                            .content(mapper.writeValueAsString(notifications)))
                    .andExpect(status().isForbidden());
            Mockito.verify(notificationService, times(0)).writeNotifications(any());
        }

        @Test
        @DisplayName("Given user has invalid role, when called, it should return 403 Forbidden")
        void writeNotificationTest2() throws Exception {
            performPost(EXPIRED_IN_FUTURE, INVALID_USER_ROLE, HttpStatus.FORBIDDEN);
            Mockito.verify(notificationService, times(0)).writeNotifications(any());
        }

        @Test
        @DisplayName("Given auth token is expired, when called, it should return 401 Unauthorized")
        void writeNotificationTest3() throws Exception {
            performPost(EXPIRED_IN_PAST, VALID_USER_ROLE, HttpStatus.UNAUTHORIZED);
            Mockito.verify(notificationService, times(0)).writeNotifications(any());
        }

        @Test
        @DisplayName("Given user is authorized, when called, it should return 201 Created")
        void writeNotificationTest4() throws Exception {
            performPost(EXPIRED_IN_FUTURE, COVID_APP_MANAGER, HttpStatus.CREATED);
            Mockito.verify(notificationService, times(1)).writeNotifications(any());
        }

        private void performPost(LocalDateTime tokenExpiration, String userRole, HttpStatus status) throws Exception {
            String token = JwtTestUtil.getJwtTestToken(PRIVATE_KEY, tokenExpiration, userRole);
            mockMvc.perform(MockMvcRequestBuilders.post(URL)
                            .accept(MediaType.ALL_VALUE)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .header("Authorization", "Bearer " + token)
                            .content(mapper.writeValueAsString(notifications)))
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
            Mockito.verify(notificationService, times(0)).removeNotifications();
        }

        @Test
        @DisplayName("Given user has invalid role, when called, it should return 403 Forbidden")
        void removeNotificationTest2() throws Exception {
            performDelete(EXPIRED_IN_FUTURE, INVALID_USER_ROLE, HttpStatus.FORBIDDEN);
            Mockito.verify(notificationService, times(0)).removeNotifications();
        }

        @Test
        @DisplayName("Given auth token is expired, when called, it should return 401 Unauthorized")
        void removeNotificationTest3() throws Exception {
            performDelete(EXPIRED_IN_PAST, VALID_USER_ROLE, HttpStatus.UNAUTHORIZED);
            Mockito.verify(notificationService, times(0)).removeNotifications();
        }

        @Test
        @DisplayName("Given user is authorized, when called, it should return 204 No Content")
        void removeNotificationTest4() throws Exception {
            performDelete(EXPIRED_IN_FUTURE, COVID_APP_MANAGER, HttpStatus.NO_CONTENT);
            Mockito.verify(notificationService, times(1)).removeNotifications();
        }

        private void performDelete(LocalDateTime tokenExpiration, String userRole, HttpStatus status) throws Exception {
            String token = JwtTestUtil.getJwtTestToken(PRIVATE_KEY, tokenExpiration, userRole);
            mockMvc.perform(delete(URL)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .header("Authorization", "Bearer " + token))
                    .andExpect(getResultMatcher(status));
        }


    }
}

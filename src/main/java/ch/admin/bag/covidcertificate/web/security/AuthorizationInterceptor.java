package ch.admin.bag.covidcertificate.web.security;

import ch.admin.bag.covidcertificate.api.Constants;
import ch.admin.bag.covidcertificate.api.exception.AuthorizationException;
import ch.admin.bag.covidcertificate.authorization.AuthorizationService;
import ch.admin.bag.covidcertificate.authorization.config.ServiceData;
import ch.admin.bag.covidcertificate.config.security.authentication.JeapAuthenticationToken;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Set;

import static net.logstash.logback.argument.StructuredArguments.kv;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class AuthorizationInterceptor implements HandlerInterceptor {
    private final AuthorizationService authService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String uri = request.getRequestURI();
        String httpMethod = request.getMethod();
        log.info("Call of preHandle with URI: {} and Method: {}", uri, httpMethod);

        try {
            JeapAuthenticationToken authentication = ((JeapAuthenticationToken) SecurityContextHolder
                    .getContext()
                    .getAuthentication());
            Set<String> rawRoles = authentication.getUserRoles();
            if (!authService.isUserPermitted(rawRoles)) {
                throw new AuthorizationException(Constants.ACCESS_DENIED_FOR_HIN_WITH_CH_LOGIN);
            }

            ServiceData.Function function = authService.identifyFunction(AuthorizationService.SERVICE_NOTIFICATIONS, uri, httpMethod)
                    .stream()
                    .findAny()
                    .orElseThrow(() -> new AuthorizationException(Constants.NO_FUNCTION_CONFIGURED, uri));

            boolean isGranted = authService.isGranted(rawRoles, function);
            log.info("Function authorization {}: {}, {}, {}", (isGranted ? "granted" : "not granted"),
                    kv("clientId", authentication.getClientId()),
                    kv("roles", authService.mapRawRoles(rawRoles)),
                    kv("function", function.getIdentifier()));

            if (!isGranted) {
                throw new AuthorizationException(Constants.FORBIDDEN, uri);
            }

        } catch (ClassCastException e) {
            log.error(e.getMessage());
            throw new AuthorizationException(Constants.FORBIDDEN, uri);
        }

        return true;
    }
}

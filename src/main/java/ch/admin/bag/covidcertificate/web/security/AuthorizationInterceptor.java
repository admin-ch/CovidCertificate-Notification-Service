package ch.admin.bag.covidcertificate.web.security;

import ch.admin.bag.covidcertificate.api.Constants;
import ch.admin.bag.covidcertificate.api.exception.AuthorizationException;
import ch.admin.bag.covidcertificate.api.security.ServiceDataDto;
import ch.admin.bag.covidcertificate.config.security.authentication.JeapAuthenticationToken;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.flywaydb.core.internal.util.StringUtils;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static net.logstash.logback.argument.StructuredArguments.kv;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class AuthorizationInterceptor implements HandlerInterceptor {
    private final AuthorizationClient authClient;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String uri = request.getRequestURI();
        String httpMethod = request.getMethod();

        JeapAuthenticationToken authentication = ((JeapAuthenticationToken) SecurityContextHolder
                .getContext()
                .getAuthentication());
        Set<String> rawRoles = authentication.getUserRoles();
        boolean isHinUser = rawRoles.contains("bag-cc-hin-epr") || rawRoles.contains("bag-cc-hin");
        boolean isHinCodeOrPersonal = rawRoles.contains("bag-cc-hincode") || rawRoles.contains("bag-cc-personal");
        if (isHinUser && !isHinCodeOrPersonal) {
            log.warn("HIN-User not allowed to use the application...");
            log.warn("userroles: {}", rawRoles);
            throw new AuthorizationException(Constants.ACCESS_DENIED_FOR_HIN_WITH_CH_LOGIN);
        }

        Set<String> roles = mapRawRoles(rawRoles);
        ServiceDataDto.Function function = authClient.requestServiceDefinition()
                .stream()
                .map(ServiceDataDto::getFunctions)
                .map(Map::values)
                .flatMap(Collection::stream)
                .filter(f -> StringUtils.hasText(f.getUri()))
                .filter(f -> f.matchesUri(uri))
                .filter(f -> f.matchesHttpMethod(httpMethod))
                .filter(f -> f.isBetween(LocalDateTime.now()))
                .findAny()
                .orElseThrow(() -> new AuthorizationException(Constants.NO_FUNCTION_CONFIGURED, uri));

        log.info("Verify function authorization: {}, {}, {}",
                kv("clientId", authentication.getClientId()),
                kv("roles", roles),
                kv("function", function.getIdentifier()));

        boolean isGranted = isGranted(roles, function);
        if (!isGranted) {
            throw new AuthorizationException(Constants.FORBIDDEN, uri);
        }

        return true;
    }


    private Set<String> mapRawRoles(Collection<String> rawRoles) {
        return rawRoles.stream()
                .map(role -> authClient.requestRoleMap().get(role))
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    private boolean isGranted(Set<String> roles, ServiceDataDto.Function function) {
        boolean isActive = function.isBetween(LocalDateTime.now());
        if (!isActive) {
            return false;
        }

        boolean allAdditionalValid = true;
        if (CollectionUtils.isNotEmpty(function.getAdditional())) {
            // check additional functions which are currently valid

            allAdditionalValid = function.getAdditional()
                    .stream()
                    .filter(func -> func.isBetween(LocalDateTime.now()))
                    .allMatch(func -> isGranted(roles, func));
        }
        List<String> oneOf = function.getOneOf();
        if (CollectionUtils.isEmpty(oneOf)) {
            return allAdditionalValid;
        }
        boolean oneOfValid = oneOf.stream().anyMatch(roles::contains);
        return (allAdditionalValid && oneOfValid);
    }


}

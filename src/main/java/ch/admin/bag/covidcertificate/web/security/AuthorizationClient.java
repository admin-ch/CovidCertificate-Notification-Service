package ch.admin.bag.covidcertificate.web.security;

import ch.admin.bag.covidcertificate.api.security.RoleDataDto;
import ch.admin.bag.covidcertificate.api.security.ServiceDataDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthorizationClient {
    private static final String ROLE_MAP_CACHE_NAME = "ROLE_MAP_CACHE_NAME";
    private static final String SERVICE_DEFINITION_CACHE_NAME = "SERVICE_DEFINITION_CACHE_NAME";
    private static final String ROLE_MAPPING_RESOURCE_PATH = "role-mapping";
    private static final String SERVICE_DEFINITION_RESOURCE_PATH = "definition/notifications";

    private final WebClient defaultWebClient;

    @Value("${cc-management-service.uri}")
    private String managementServiceURL;
    @Value("${cc-management-service.authorization.api.v1-path}")
    private String authorizationApiV1Path;

    @Cacheable(ROLE_MAP_CACHE_NAME)
    public Map<String, String> requestRoleMap() {
        log.info("Request role map cache.");
        final var uri = UriComponentsBuilder.fromHttpUrl(managementServiceURL + authorizationApiV1Path + ROLE_MAPPING_RESOURCE_PATH).toUriString();

        return fetch(uri, RoleDataDto[].class)
                .stream()
                .flatMap(Arrays::stream)
                .flatMap(role -> Stream.of(
                        Pair.of(role.getClaim(), role.getIntern()),
                        Pair.of(role.getEiam(), role.getIntern())))
                .collect(Collectors.toMap(Pair::getKey, Pair::getValue));
    }

    @Cacheable(SERVICE_DEFINITION_CACHE_NAME)
    public Optional<ServiceDataDto> requestServiceDefinition() {
        log.info("Request role map cache.");
        final var uri = UriComponentsBuilder.fromHttpUrl(managementServiceURL + authorizationApiV1Path + SERVICE_DEFINITION_RESOURCE_PATH).toUriString();
        return fetch(uri, ServiceDataDto.class);
    }

    @Scheduled(cron = "${cc-management-service.authorization.data-sync.cron}")
    @CacheEvict(value = {SERVICE_DEFINITION_CACHE_NAME}, allEntries = true)
    public void resetCaches() {
        log.info("Reset caches.");
    }

    private <T> Optional<T> fetch(String uri, Class<T> classType) {
        return defaultWebClient.get()
                .uri(uri)
                .retrieve()
                .bodyToMono(classType)
                .switchIfEmpty(Mono.error(new IllegalStateException("Response Body is null for request " + uri)))
                .blockOptional();
    }

}

package ch.admin.bag.covidcertificate.web.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class AuthorizationInterceptorConfig implements WebMvcConfigurer {
    private final AuthorizationInterceptor interceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(interceptor)
                .excludePathPatterns(
                        "/error",
                        "/actuator/.*",
                        "/swagger-ui.html",
                        "/swagger-ui/.*",
                        "/v3/api-docs/.*",
                        "/api/v1/ping"
                );
    }
}

package ch.admin.bag.covidcertificate.service.config;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;


@Slf4j
@Configuration
public class RestClientConfig {

    @Value("${spring.application.name}")
    private String applicationName;

    @Value("${cc-notification-service.rest.connectTimeoutSeconds}")
    private int connectTimeout;

    @Value("${cc-notification-service.rest.readTimeoutSeconds}")
    private int readTimeout;

    @Bean
    public WebClient defaultWebClient(JeapOAuth2WebclientBuilderFactory jeapOAuth2WebclientBuilderFactory) {
        // Config Timeout
        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectTimeout * 1000)
                .doOnConnected(conn -> conn
                        .addHandlerLast(new ReadTimeoutHandler(readTimeout)));

        ClientHttpConnector connector = new ReactorClientHttpConnector(httpClient.wiretap(false));

        return jeapOAuth2WebclientBuilderFactory
                .createForClientId(applicationName)
                .clientConnector(connector)
                .build();
    }
}

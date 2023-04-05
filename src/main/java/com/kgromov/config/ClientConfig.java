package com.kgromov.config;

import com.netflix.graphql.dgs.client.GraphQLClient;
import com.netflix.graphql.dgs.client.HttpResponse;
import com.netflix.graphql.dgs.client.MonoGraphQLClient;
import com.netflix.graphql.dgs.client.WebClientGraphQLClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;


@Configuration
public class ClientConfig {
    @Value("${base.url}")
    private String baseUrl;

    @Bean
    public GraphQLClient blockingClient() {
        return GraphQLClient.createCustom(baseUrl, (url, headers, body) -> {
            HttpHeaders httpHeaders = new HttpHeaders();
            headers.forEach(httpHeaders::addAll);
            ResponseEntity<String> exchange = new RestTemplate().exchange(url, HttpMethod.POST, new HttpEntity<>(body, httpHeaders), String.class);
            return new HttpResponse(exchange.getStatusCode().value(), exchange.getBody());
        });
    }

   /* @Bean
    public MonoGraphQLClient nonBlockingClient() {
        WebClient webClient = WebClient.create(baseUrl);
        return MonoGraphQLClient.createWithWebClient(webClient);
    }*/
}

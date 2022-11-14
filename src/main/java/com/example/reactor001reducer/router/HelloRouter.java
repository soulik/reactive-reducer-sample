package com.example.reactor001reducer.router;

import com.example.reactor001reducer.service.HelloService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;

@Configuration(proxyBeanMethods = false)
public class HelloRouter {
    @Bean
    public RouterFunction<ServerResponse> route(HelloService handler) {
        return RouterFunctions
                .route(GET("/for-loop/{count}").and(accept(MediaType.APPLICATION_JSON)), handler::forLoop)
                .andRoute(GET("/reducer/{count}").and(accept(MediaType.APPLICATION_JSON)), handler::reducer);
    }
}



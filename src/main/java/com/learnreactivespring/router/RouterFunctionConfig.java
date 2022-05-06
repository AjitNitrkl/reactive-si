package com.learnreactivespring.router;

import com.learnreactivespring.document.Item;
import com.learnreactivespring.handler.SampleHandlerFunction;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;

@Configuration
public class RouterFunctionConfig {

    @Bean
    public RouterFunction<ServerResponse> route(SampleHandlerFunction handler){
        return RouterFunctions
                .route(GET("/functional/mono1")
                        .and(accept(MediaType.APPLICATION_JSON)),handler::mono1)
                .andRoute(GET("/functional/mono2")
                        .and(accept(MediaType.APPLICATION_JSON)),handler::mono2)
                .andRoute(POST("/functional/mono3"),handler::mono3);
    }
}

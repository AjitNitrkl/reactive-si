package com.learnreactivespring.handler;


import com.learnreactivespring.document.Item;
import com.learnreactivespring.repository.ItemReactiveRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class SampleHandlerFunction {

    @Autowired
    ItemReactiveRepository itemReactiveRepository;

    public Mono<ServerResponse> mono1(ServerRequest request){
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(Flux.just(1,2,3,4).log(), Integer.class);
    }

    public Mono<ServerResponse> mono2(ServerRequest request){
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(Flux.just(1).log(), Integer.class);
    }


    public Mono<ServerResponse> mono3(ServerRequest request){
        request.bodyToMono(Item.class)
                .map(item-> itemReactiveRepository.save(item));
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON).build();
    }

}

package com.learnreactivespring.legacyapi.todo;

import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

public class TodoRestClient {

    private WebClient webClient;
    public TodoRestClient(WebClient webClient){
        this.webClient = webClient;
    }

    public List<Todo> getAllTodos(){
       return webClient.get().uri("/todos")
                .retrieve()
                .bodyToFlux(Todo.class)
                .collectList()
                .block();
    }

    public Mono<List<Todo>> getAllTodosUsingReactive(){
        return webClient.get().uri("/todos")
                .retrieve()
                .bodyToFlux(Todo.class)
                .collectList();
    }

   /* public Flux<Integer> getAllIntegers(){
        return webClient.get().uri("/fluxStream")
                .retrieve()
                .bodyToFlux(Integer.class);
    }*/



}

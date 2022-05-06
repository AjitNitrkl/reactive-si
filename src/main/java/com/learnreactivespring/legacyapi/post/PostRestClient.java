package com.learnreactivespring.legacyapi.post;

import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

public class PostRestClient {

    private WebClient webClient;
    public PostRestClient(WebClient webClient){
        this.webClient = webClient;
    }

    public List<Post> getAllPosts(){
        return webClient.get().uri("/posts")
                .retrieve()
                .bodyToFlux(Post.class)
                .collectList()
                .block();
    }

    public Mono<List<Post>> getAllPostsUsingReactive(){
        return webClient.get().uri("/posts")
                .retrieve()
                .bodyToFlux(Post.class)
                .collectList();
    }
}

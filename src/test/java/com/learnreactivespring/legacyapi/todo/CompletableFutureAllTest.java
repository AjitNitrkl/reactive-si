package com.learnreactivespring.legacyapi.todo;

import com.learnreactivespring.legacyapi.post.Post;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.retry.Retry;

import java.net.UnknownHostException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.stream.Collectors;

@RunWith(SpringRunner.class)
@DirtiesContext
public class CompletableFutureAllTest {

    private static final String baseurl = "https://jsonplaceholder.typicode.com";
    WebClient webClient = WebClient.create(baseurl);
    public Retry<?> retryOnExc = Retry.anyOf(UnknownHostException.class)
                                    .fixedBackoff(Duration.ofSeconds(3))
                                    .retryMax(2);

    @Test
    public void testParallelCallsForApi() throws InterruptedException {
        List<String> urlLists =  Arrays.asList("/posts/1","/posts/2");
        IntFunction<CompletableFuture<Post>[]> futureArrayGenerator = CompletableFuture[]::new;
        CompletableFuture<Post>[] stages = urlLists.stream()
                .map(url -> executeApiCall(url).toFuture())
                .toArray(futureArrayGenerator);
        allOf(stages);
    }

    @Test
    public void testParallelCallsForApi_1() throws InterruptedException {
        List<String> urlLists =  Arrays.asList("posts/1", "posts/2");
        List<CompletableFuture<Post>> cfPostList = urlLists.stream()
                .map(url -> executeApiCall(url).toFuture())
                .collect(Collectors.toList());
        /*CompletableFuture cf = CompletableFuture.completedFuture(new ArrayList<>());
        CompletableFuture cf1 = CompletableFuture.completedFuture(new ArrayList<>());
        CompletableFuture.allOf(cf, cf1);*/
        //converting list of cf to Array of cf as below all of takes any no of args of cf
        CompletableFuture<Void> allFutures = CompletableFuture
                .allOf(cfPostList.toArray(new CompletableFuture[cfPostList.size()]));
        CompletableFuture<List<Post>> allCompletableFuture = allFutures.thenApply(future ->
                cfPostList.stream()
                        .map(completableFuture -> completableFuture.join())
                        .collect(Collectors.toList())
        );
        CompletableFuture completableFuture = allCompletableFuture
                .thenApply(post -> post.stream().collect(Collectors.toList()));

        System.out.println(completableFuture.join());
    }

    //@Test
    public void testParallelCallsForApiWhenExc() throws InterruptedException {
        List<String> urlLists =  Arrays.asList("https://jsonplaceholder.typicode.com/posts/1",
                "https://jsonplaceholder.typicode.com1/posts/2");
        List<CompletableFuture<Post>> cfPostList = urlLists.stream()
                .map(url -> executeApiCallWhenExc_1(url).toFuture())
                .collect(Collectors.toList());
        CompletableFuture<Void> allFutures = CompletableFuture
                .allOf(cfPostList.toArray(new CompletableFuture[cfPostList.size()]));
        CompletableFuture<List<Post>> allCompletableFuture = allFutures.thenApply(future ->
                cfPostList.stream()
                        .map(completableFuture -> completableFuture.join())
                        .collect(Collectors.toList())
        );
        CompletableFuture completableFuture = allCompletableFuture
                .thenApply(post -> post.stream().collect(Collectors.toList()))
                .exceptionally(ex ->{
                   // System.out.println("Exception Occurred "+ex);
                    throw new RuntimeException("Run time Exception Occurred");
                });

        System.out.println(completableFuture.join());
    }

    private Mono<Post> executeApiCallWhenExc(String url) {
        return  WebClient.create(url)
                .get()
                .exchange()
                .flatMap(this::extractResponse)
                .retryWhen(retryOnExc);
    }

    private Mono<Post> executeApiCallWhenExc_1(String url) {
        return  WebClient.create(url)
                .get()
                .exchange()
                .flatMap(this::extractResponse)
                .onErrorMap((e) -> new RuntimeException("Error Occurred..."))
                .retryBackoff(2, Duration.ofSeconds(2));

    }

    private Mono<Post> executeApiCall(String url) {
        return  webClient
                .get()
                .uri("url")
                .exchange()
                .flatMap(this::extractResponse);
    }

    private  Mono<Post> extractResponse(ClientResponse clientResponse) {
        return clientResponse.bodyToMono(Post.class);
    }

    private void allOf(CompletableFuture<Post>[] promises){
        Function<Void, List<Post>> func =
                v -> Arrays.stream(promises)
                        .map(CompletableFuture::join)
                        .collect(Collectors.toList());

        CompletableFuture<List<Post>> listObject = Optional.of(promises)
                .filter(l -> l.length>0)
                .map(post ->  CompletableFuture.allOf(post)
                            .thenApplyAsync(func))
                .orElseGet(() -> CompletableFuture.completedFuture(new ArrayList<>()));


        System.out.println(" Result "+listObject.join());
    }

}

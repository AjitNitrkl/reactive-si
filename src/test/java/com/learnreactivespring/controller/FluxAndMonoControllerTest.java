package com.learnreactivespring.controller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
//@WebFluxTest //inroduced partof spring 5 - scan class with @RestController but not service,repo,component
@DirtiesContext
@AutoConfigureWebTestClient //not required if using @webfluxTest
@SpringBootTest //Added this and @AutoConfigureWebTestClient after ItemRepository was autowired in item controller since webflux does not scan @Autowired
public class FluxAndMonoControllerTest {

    @Autowired
    WebTestClient webTestClient;  // actualcall- equivalent in Spring MVC is TestRestTemplate,
                                // with mockcall- @RestClientTest on class level with MockRestServiceServer

    @Test
    public void nonfluxWithWebclientTest(){
        Flux<Integer> integerFlux = webTestClient.get().uri("/flux")
                .accept(MediaType.APPLICATION_STREAM_JSON)
                .exchange()
                .expectStatus().isOk()
                .returnResult(Integer.class)
                .getResponseBody();
        System.out.println("Result from webclient for nonflux"+integerFlux.blockFirst());
    }

    @Test
    public void flux_approach1(){
        Flux<Integer> integerFlux = webTestClient.get().uri("/flux")
                .accept(MediaType.APPLICATION_STREAM_JSON)
                .exchange().expectStatus().isOk()
                .returnResult(Integer.class)
                .getResponseBody();
        StepVerifier.create(integerFlux)
                .expectSubscription()
                .expectNext(1,2,3,4)
                .verifyComplete();
    }


    @Test
    public void flux_approach2(){
        List<Integer> expectedList = Arrays.asList(1,2,3,4);
        EntityExchangeResult<List<Integer>> integerFlux = webTestClient.get().uri("/flux")
                .accept(MediaType.APPLICATION_STREAM_JSON)
                .exchange().expectStatus().isOk()
                .expectBodyList(Integer.class)
                .returnResult();

        assertEquals(expectedList, integerFlux.getResponseBody());
    }


    @Test
    public void flux_approach3(){
        List<Integer> expectedList = Arrays.asList(1,2,3,4);
         webTestClient.get().uri("/flux")
                .accept(MediaType.APPLICATION_STREAM_JSON)
                .exchange().expectStatus().isOk()
                .expectBodyList(Integer.class)
                .consumeWith((response) ->{
                    assertEquals(expectedList, response.getResponseBody());
                });
    }

    //@Test
    public void fluxStream_approach(){

        Flux<Long> longStreamFlux = webTestClient.get().uri("/fluxStream")
                .accept(MediaType.APPLICATION_STREAM_JSON)
                .exchange().expectStatus().isOk()
                .returnResult(Long.class)
                .getResponseBody();
        longStreamFlux.subscribe((l) ->{
            System.out.println("val.."+l);
        });
       /* Flux.range(1,10).(
                (elm) -> System.out.println("Element is "+elm),
                (e) -> System.err.println("Exception Occured "+e),
                ()-> System.out.println("Completed"), //only executed at the end - here we r request only 2 so this will not present
                (subscription -> subscription.request(3)); */

        StepVerifier.create(longStreamFlux)
                //.expectSubscription()
                .expectNext(0l)
                .expectNext(1l)
                .expectNext(2l)
                .thenCancel()
                .verify();
    }

    @Test
    public void testMono(){
        webTestClient.get().uri("/mono")
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Integer.class)
                .consumeWith((response) ->{
                    assertEquals(new Integer(1), response.getResponseBody());
                });


    }

    @Test
    public void testFluxBackPressure(){
        Flux.range(1,5)
                .subscribe(new Subscriber<Integer>() {
                    private Subscription s;
                    int counter;

                    @Override//back pressure requesting 2
                    public void onSubscribe(Subscription s) {
                        System.out.println("onSubscribe");
                        this.s = s;
                        System.out.println("Requesting 2 emissions");
                        s.request(2);
                    }

                    @Override
                    public void onNext(Integer i) {
                        System.out.println("onNext " + i);
                        counter++;
                        if (counter % 2 == 0) {
                            System.out.println("Requesting 2 emissions");
                            s.request(2);
                        }
                    }

                    @Override
                    public void onError(Throwable t) {
                        System.err.println("onError");
                    }

                    @Override
                    public void onComplete() {
                        System.out.println("onComplete");
                    }
                });
    }

}

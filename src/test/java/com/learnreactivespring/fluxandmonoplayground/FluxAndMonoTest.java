package com.learnreactivespring.fluxandmonoplayground;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@RunWith(SpringRunner.class)
@DirtiesContext
public class FluxAndMonoTest {

    Flux<String> stringFlux;

    @Before
    public void setUp(){
        stringFlux = Flux.just("Spring", "Spring Boot", "Reactive Spring")
                /*.concatWith(Flux.error(new RuntimeException("Exception Occurred")))*/
                //.concatWith(Flux.just("After Error"))
                .log();
    }

    @Test
    public void fluxTest() {
        stringFlux
                .subscribe(System.out::println,
                        (e) -> System.err.println("Exception is " + e)
                        , () -> System.out.println("Completed"));

        //to get hold of subscribtion and apply back pressure
        stringFlux
                .subscribe(System.out::println,
                        (e) -> System.err.println("Exception is " + e)
                        , () -> System.out.println("Completed"),
                        sub ->sub.request(3));
    }

    //stepverifier & verifycomplete does subscribe and does assert as well
    @Test
    public void fluxTest1(){
        StepVerifier.create(stringFlux)
                .expectNext("Spring")
                .expectNext("Spring Boot")
                .expectNext("Reactive Spring")
                .verifyComplete();
    }

   // @Test //this will fail if order is changed
    public void fluxTest2(){
        StepVerifier.create(stringFlux)
                .expectNext("Spring Boot")
                .expectNext("Spring")
                .expectNext("Reactive Spring")
                .verifyComplete();
    }

    @Test
    public void fluxTest3(){
        StepVerifier.create(stringFlux)
                .expectNext("Spring","Spring Boot","Reactive Spring")
                .verifyComplete();
    }

    @Test
    public void fluxTestWithError(){

        stringFlux = Flux.just("Spring", "Spring Boot", "Reactive Spring")
                .concatWith(Flux.error(new RuntimeException("Exception Occurred")))
                .log();
        StepVerifier.create(stringFlux)
                .expectNext("Spring")
                .expectNext("Spring Boot")
                .expectNext("Reactive Spring")
                //.expectError(RuntimeException.class) or below
                .expectErrorMessage("Exception Occurred")
                .verify();
    }

    @Test
    public void fluxTestWithCount(){

        stringFlux = Flux.just("Spring", "Spring Boot", "Reactive Spring")
                .concatWith(Flux.error(new RuntimeException("Exception Occurred")))
                .log();
        StepVerifier.create(stringFlux)
                .expectNextCount(3)
                .expectErrorMessage("Exception Occurred")
                .verify();
    }



    @Test
    public void monotest(){
        Mono<String> mono = Mono.just("Spring"); //same like flux for error as well
        StepVerifier.create(mono.log())
                .expectNext("Spring")
                .verifyComplete();
    }
}

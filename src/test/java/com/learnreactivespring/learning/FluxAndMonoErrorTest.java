package com.learnreactivespring.learning;

import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;

public class FluxAndMonoErrorTest {

    Flux<String> fluxString = Flux.just("Spring", "Spring Boot")
                                    .concatWith(Flux.error(new RuntimeException("Error Occurred")))
                                    .concatWith(Flux.just("Spring Data"));

    @Test
    public void testError(){
        StepVerifier.create(fluxString)
                .expectSubscription()
                .expectNext("Spring", "Spring Boot")
                .expectError(RuntimeException.class)
                .verify();
    }

    @Test
    public void testErrorHandler(){
        Flux<String> fluxString = Flux.just("Spring", "Spring Boot")
                .concatWith(Flux.error(new RuntimeException("Error Occurred")))
                .concatWith(Flux.just("Spring Data"))
                .onErrorResume((e)->{
            System.out.println("Exception "+e);
            return Flux.just("default msg");
        });
        StepVerifier.create(fluxString.log())
                .expectSubscription()
                .expectNext("Spring", "Spring Boot")
                .expectNext("default msg")
                .verifyComplete();
    }


    @Test
    public void testErrorHandlerException(){ //error map is used to map one exception to other type
        Flux<String> fluxString = Flux.just("Spring", "Spring Boot")
                .concatWith(Flux.error(new RuntimeException("Error Occurred")))
                .concatWith(Flux.just("Spring Data"))
                .onErrorMap((e) -> new CustomErrorException("Exception from Flux"));
        StepVerifier.create(fluxString.log())
                .expectSubscription()
                .expectNext("Spring", "Spring Boot")
                .expectError(CustomErrorException.class)
                .verify();
    }


    @Test
    public void testErrorHandlerWithRetry(){ //error map is used to map one exception to other type
        Flux<String> fluxString = Flux.just("Spring", "Spring Boot")
                .concatWith(Flux.error(new RuntimeException("Error Occurred")))
                .concatWith(Flux.just("Spring Data"))
                .onErrorMap((e) -> new CustomErrorException("Exception from Flux"))
                .retry(2);
        StepVerifier.create(fluxString.log())
                .expectSubscription()
                .expectNext("Spring", "Spring Boot") //with retry we get same flux again
                .expectNext("Spring", "Spring Boot") //this is useful if we have a db/remote call ther are chance of outage
                .expectNext("Spring", "Spring Boot")
                .expectError(CustomErrorException.class)
                .verify();
    }


    @Test
    public void testErrorHandlerWithRetryWithBackOff(){ //error map is used to map one exception to other type
        Flux<String> fluxString = Flux.just("Spring", "Spring Boot")
                .concatWith(Flux.error(new RuntimeException("Error Occurred")))
                .concatWith(Flux.just("Spring Data"))
                .onErrorMap((e) -> new CustomErrorException("Exception from Flux"))
                .retryBackoff(2, Duration.ofSeconds(5));
        StepVerifier.create(fluxString.log())
                .expectSubscription()
                .expectNext("Spring", "Spring Boot") //with retry we get same flux again
                .expectNext("Spring", "Spring Boot") //this is useful if we have a db/remote call ther are chance of outage
                .expectNext("Spring", "Spring Boot")
                .expectError(IllegalStateException.class)
                .verify();
    }
}

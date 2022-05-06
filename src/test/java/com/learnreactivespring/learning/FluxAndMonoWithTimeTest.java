package com.learnreactivespring.learning;

import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;

public class FluxAndMonoWithTimeTest {

    @Test
    public void testInfiniteSequence() throws InterruptedException {
        Flux<Long> infiniteFlux = Flux.interval(Duration.ofMillis(200)).log();
        infiniteFlux.subscribe((elm) -> System.out.println(elm));
        Thread.sleep(3000);
    }

    @Test
    public void testFiniteSequence() {
        Flux<Long> infiniteFlux = Flux.interval(Duration.ofMillis(200)).take(5).log();
        StepVerifier.create(infiniteFlux)
                .expectSubscription()
                .expectNextCount(5)
                .verifyComplete();
    }

    @Test
    public void testFiniteSequenceMap() {
        Flux<Long> infiniteFlux = Flux.interval(Duration.ofMillis(200))
                .take(5)
                .map(l -> l*5)
                .log();
        StepVerifier.create(infiniteFlux)
                .expectSubscription()
                .expectNextCount(5)
                .verifyComplete();
    }
}

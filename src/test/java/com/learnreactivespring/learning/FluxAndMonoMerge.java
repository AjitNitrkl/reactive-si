package com.learnreactivespring.learning;

import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import reactor.test.scheduler.VirtualTimeScheduler;

import java.time.Duration;

public class FluxAndMonoMerge {

    Flux<String> flux1 = Flux.just("Spring", "Spring MVC", "Spring Data");
    Flux<String> flux2 = Flux.just("REST", "JAX RS", "Spring REST");

    @Test
    public void testFluxMerge(){
        StepVerifier.create(Flux.merge(flux1,flux2).log())
                .expectSubscription()
                .expectNextCount(6)
                .verifyComplete();
    }

    @Test
    public void testFluxMergeWithDelay(){
        Flux<String> mergedFlux = Flux.merge(flux1.delayElements(Duration.ofSeconds(1)),
                flux2.delayElements(Duration.ofSeconds(1)));
        StepVerifier.create(mergedFlux.log())
                .expectSubscription()
                .expectNextCount(6)
                .verifyComplete();
    }

    @Test
    public void testFluxMergeWithDelayUsingVirtualTime(){
        VirtualTimeScheduler.getOrSet();
        Flux<String> mergedFlux = Flux.merge(flux1.delayElements(Duration.ofSeconds(1)),
                flux2.delayElements(Duration.ofSeconds(1)));
        StepVerifier.withVirtualTime(()-> mergedFlux.log())
                .expectSubscription()
                .thenAwait(Duration.ofSeconds(6))
                .expectNextCount(6)
                .verifyComplete();
    }

    @Test
    public void testFluxConcat(){  //here the order is maintained but takes more time
        Flux<String> mergedFlux = Flux.concat(flux1.delayElements(Duration.ofSeconds(1)),
                flux2.delayElements(Duration.ofSeconds(1)));
        StepVerifier.create(mergedFlux.log())
                .expectSubscription()
                .expectNextCount(6)
                .verifyComplete();
    }

    @Test
    public void testFluxUsingZip(){
        Flux<String> mergedFlux = Flux.zip(flux1,flux2,(t1,t2)->{
            return t1.concat(t2);
        });
        StepVerifier.create(mergedFlux.log())
                .expectSubscription()
                .expectNextCount(3)
                .verifyComplete();
    }

}

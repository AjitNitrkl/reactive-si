package com.learnreactivespring.learning;

import org.junit.Test;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

public class FluxAndMonoBackPressureTest {

    @Test
    public void testBackPressure(){
        StepVerifier.create(Flux.range(1,10))
                .expectSubscription()
                .thenRequest(1)
                .expectNext(1)
                .thenRequest(1)
                .expectNext(2)
                .thenCancel()
                .verify();
    }

    @Test
    public void testBackPressureSubscribe(){
        Flux<Integer> fluxInfinite = Flux.range(1,10).log();
        fluxInfinite.subscribe((elm) -> System.out.println("Element is "+elm),
            (e) -> System.err.println("Exception Occured "+e),
                ()-> System.out.println("Completed"), //only executed at the end - here we r request only 2 so this will not present
                (subscription -> subscription.request(3)));

    }

    @Test
    public void testBackPressureCancel(){
        Flux<Integer> fluxInfinite = Flux.range(1,10).log();
        fluxInfinite.subscribe((elm) -> System.out.println("Element is "+elm),
                (e) -> System.err.println("Exception Occured "+e),
                ()-> System.out.println("Completed"), //only executed at the end - here we r request only 2 so this will not present
                (subscription -> subscription.cancel()));
    }

    @Test
    public void testBackPressureCustomized(){

        Flux<Integer> fluxInfinite = Flux.range(1,10).log();
        fluxInfinite.subscribe(new BaseSubscriber<Integer>() {
            @Override
            protected void hookOnNext(Integer value) {
                request(1);
                System.out.println("Value of elm "+value);
                if(value == 4)
                    cancel();
            }
        });

    }

}

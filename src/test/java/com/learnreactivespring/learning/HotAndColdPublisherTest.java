package com.learnreactivespring.learning;

import org.junit.Test;
import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;

import java.time.Duration;

public class HotAndColdPublisherTest {

    @Test
    public void testColdPublisher() throws InterruptedException{

        Flux<String> stringFlux = Flux.just("A","B","C","D","E")
                .delayElements(Duration.ofSeconds(1));
        stringFlux
                .subscribe(s -> System.out.println("Subsriber1: "+s));
        Thread.sleep(2000);
        stringFlux
                .subscribe(s -> System.out.println("Subsriber2: "+s));
        Thread.sleep(5000);
    }


    @Test
    public void testHotPublisher() throws InterruptedException{

        Flux<String> stringFlux = Flux.just("A","B","C","D","E")
                .delayElements(Duration.ofSeconds(1));
        ConnectableFlux<String> connectableFlux = stringFlux.publish();
        connectableFlux.connect();

        connectableFlux
                .subscribe(s -> System.out.println("Subsriber1: "+s));
        Thread.sleep(2000);
        //does not emit the value from beginning, emits from where it joins
        connectableFlux
                .subscribe(s -> System.out.println("Subsriber2: "+s));
        Thread.sleep(5000);

    }
}

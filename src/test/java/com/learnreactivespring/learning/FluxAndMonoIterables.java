package com.learnreactivespring.learning;

import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

public class FluxAndMonoIterables {

    List<String> fluxList = Arrays.asList("Spring", "Spring Boot", "Spring Boot Test");

    @Test
    public void testFluxList(){
        Flux<String> stringFlux =
                Flux.fromIterable(fluxList)  //can also use Array and stream as well.
                .log();
        StepVerifier.create(stringFlux)
                .expectNextCount(3)
                .verifyComplete();

        Flux<String> streamFlux = Flux.fromStream(fluxList.stream()).log();
        StepVerifier.create(streamFlux)
                .expectNext("Spring", "Spring Boot", "Spring Boot Test")
                .verifyComplete();
    }

    @Test
    public void testMono(){
       // Mono<String> mono = Mono.empty();
        Mono<String> mono = Mono.justOrEmpty(null);
        StepVerifier.create(mono)
                .expectNext()
                .verifyComplete();

    }
    @Test
    public void testMonoWithSupplier(){
        //supplier.get is taken care by fromSupplier() behind the scene
        Mono<String> supplier = Mono.fromSupplier(() -> "Ajit").log();
        StepVerifier.create(supplier)
                .expectNext("Ajit")
                .verifyComplete();
    }

    @Test
    public void testFluxinRange(){
        Flux<Integer> rangeFlux = Flux.range(1,10).log();
        StepVerifier.create(rangeFlux)
                .expectNextCount(10)
                .verifyComplete();
    }
}

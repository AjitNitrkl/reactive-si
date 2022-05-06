package com.learnreactivespring.learning;

import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

public class FluxAndMonoFilterTest {

    List<String> fluxList = Arrays.asList("Spring", "Spring Boot", "Spring Boot Test");

    @Test
    public void testFluxFilter(){
        StepVerifier.create(Flux.fromIterable(fluxList)
                .filter(s -> s.contains("Boot"))
                .log()).expectNext("Spring Boot", "Spring Boot Test").verifyComplete();

    }
}

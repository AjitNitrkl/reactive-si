package com.learnreactivespring.learning;

import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

import static reactor.core.scheduler.Schedulers.parallel;

public class FluxAndMonoTransform {

    List<String> fluxList = Arrays.asList("Spring", "Spring Boot", "Spring Boot Test");

    @Test
    public void testTransformMap(){
        StepVerifier.create(Flux.fromIterable(fluxList)
                .filter(s -> s.contains("Boot"))
                .map(String::toUpperCase)
                .log()).expectNext("SPRING BOOT", "SPRING BOOT TEST").verifyComplete();

        StepVerifier.create(Flux.fromIterable(fluxList)
                .filter(s -> !s.contains("Boot"))
                .map(String::length)
                .log()).expectNext(6).verifyComplete();

        StepVerifier.create(Flux.fromIterable(fluxList)
                .filter(s -> !s.contains("Boot"))
                .map(String::length)
                .repeat() //repeating the flux
                .log()).expectNext(6,6).verifyComplete();
    }

    @Test
    public void testTransformUsingFlatMap(){
        StepVerifier.create(Flux.fromIterable(fluxList)
                .flatMap(s -> {
                    return Flux.fromIterable(convertToList(s));
                }).log()).expectNextCount(6).verifyComplete();
    }

    @Test
    public void testTransformUsingFlatMapParallel(){
        StepVerifier.create(Flux.fromIterable(fluxList)
                .window(2)
                .flatMap(s -> s.map(this::convertToList).subscribeOn(parallel()))
                .flatMap(s -> Flux.fromIterable(s)).log()).expectNextCount(6).verifyComplete();
    }

    @Test
    public void testTransformUsingFlatMapParallelAndSequential(){
        StepVerifier.create(Flux.fromIterable(fluxList)
                .window(2)
                .flatMapSequential(s -> s.map(this::convertToList).subscribeOn(parallel()))
                .flatMap(s -> Flux.fromIterable(s)).log()).expectNextCount(6).verifyComplete();
    }

    private List<String> convertToList(String s) {
        return Arrays.asList(s, "new value");
    }
}

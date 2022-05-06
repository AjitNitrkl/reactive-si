package com.learnreactivespring.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

@RestController
public class FluxAndMonoController {

   /* @Autowired
    ItemReactiveRepository itemReactiveRepository;*/

    @GetMapping(value = "/flux", produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
    public Flux<Integer> returnFlux(){
        return Flux.just(1,2,3,4)
               // .delayElements(Duration.ofSeconds(2))
                .log();
    }

    @GetMapping(value = "/nonflux", produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
    public Integer returnNonFlux(){
        return 1;
    }

    @GetMapping(value = "/fluxStream", produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
    public Flux<Long> returnFluxStream(){
        return Flux.interval(Duration.ofSeconds(1))
                .log();
    }

    @GetMapping(value="/mono")
    public Mono<Integer> returnMono(){
        return Mono.just(1).log();
    }

    /*@GetMapping(value = "/getAllFluxItems", produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
    public Flux<Item> getAllItems(){
        return itemReactiveRepository.findAll()
                .delayElements(Duration.ofSeconds(2))
                .log();
    }*/

}

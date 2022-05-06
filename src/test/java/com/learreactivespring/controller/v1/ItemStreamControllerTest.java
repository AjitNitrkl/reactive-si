package com.learreactivespring.controller.v1;

import com.learnreactivespring.LearnReactivespringApplication;
import com.learnreactivespring.document.Item;
import com.learnreactivespring.document.ItemCapped;
import com.learnreactivespring.repository.ItemCappedReactiveRepository;
import com.learnreactivespring.repository.ItemReactiveRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.CollectionOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

import static com.learnreactivespring.controller.constants.ItemConstants.ITEM_ENDPOINT_V1;
import static com.learnreactivespring.controller.constants.ItemConstants.ITEM_STREAM_ENDPOINT_V1;
import static org.junit.Assert.assertEquals;



@SpringBootTest(classes={LearnReactivespringApplication.class})
@RunWith(SpringRunner.class)
@DirtiesContext
@AutoConfigureWebTestClient   //needed for webtestclient
@ActiveProfiles("test")  //Activating test profile this is running since we are using springboottest to load context in case of Datamongotest context is not loaded
public class ItemStreamControllerTest {

    @Autowired
    WebTestClient webTestClient;

    @Autowired
    ItemCappedReactiveRepository itemCappedReactiveRepository;

    @Autowired
    MongoOperations mongoOperations;

    @Before
    public void setUp(){
        mongoOperations.dropCollection(ItemCapped.class);
        mongoOperations.createCollection(ItemCapped.class,
                CollectionOptions.empty().maxDocuments(20).size(5000).capped());
        Flux<ItemCapped> itemCappedFlux = Flux.interval(Duration.ofSeconds(1))
                .map(i -> new ItemCapped(null, "Random Item"+i, (100.00+i)))
                .take(5);
        itemCappedReactiveRepository.insert(itemCappedFlux)
                .doOnNext((itemCapped -> {
                    System.out.println("Inserted Item: "+itemCapped);
                }))
                .blockLast();
    }

    @Test
    public void testStreamAllItems(){
       Flux<ItemCapped> itemCappedFlux =  webTestClient.get()
                .uri(ITEM_STREAM_ENDPOINT_V1)
                .exchange()
                .expectStatus().isOk()
                .returnResult(ItemCapped.class)
                .getResponseBody()
                .take(5);
       StepVerifier.create(itemCappedFlux)
               .expectNextCount(5)
               .thenCancel()
               .verify();
    }



}

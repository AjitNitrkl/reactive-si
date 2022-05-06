package com.learnreactivespring.repository;

import com.learnreactivespring.document.Item;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

@DataMongoTest  //does not load whole context only loads specific to mongo class
@RunWith(SpringRunner.class)
@DirtiesContext //each class is treated as separate and get a brand new application context
public class ItemReactiveRepositoryTest {

    @Autowired
    ItemReactiveRepository itemReactiveRepository;

    List<Item> itemList = Arrays.asList(Item.builder().description("Iphone").price(1000.0).build(),
            Item.builder().description("samsung TV").price(500.0).build(),
            Item.builder().description("Mac").price(1200.0).build(),
            Item.builder().description("Apple Watch").price(300.0).build(),
            Item.builder().id("123").description("Bose Speaker").price(300.0).build());

    @Before
    public void setUp(){
        itemReactiveRepository.deleteAll()
                .thenMany(Flux.fromIterable(itemList))
                .flatMap(itemReactiveRepository::save)
                .doOnNext((item ->{
                    System.out.println(" Inserted Item "+item);
                })).blockLast();
    }

    @Test
    public void testGetAllItems(){
        StepVerifier.create(itemReactiveRepository.findAll())
                .expectSubscription()
                .expectNextCount(5)
                .verifyComplete();
    }

    @Test
    public void testGetItemById(){
        StepVerifier.create(itemReactiveRepository.findById("123"))
                .expectSubscription()
                //.expectNext(Item.builder().id("123").description("Bose Speaker").price(300.0).build())
                //or
                .expectNextMatches(item-> item.getDescription().equals("Bose Speaker"))
                .verifyComplete();
    }

    @Test
    public void testGetItemByDesc(){
        StepVerifier.create(itemReactiveRepository.findByDescription("Mac"))
                .expectSubscription()
                .expectNextMatches(item-> item.getDescription().equals("Mac"))
                .verifyComplete();
    }

    @Test
    public void testSaveItem(){
        StepVerifier.create(itemReactiveRepository.save(Item.builder().description("Canon Printer").price(60.0).build()))
                .expectSubscription()
                .expectNextMatches(item-> item.getDescription().equals("Canon Printer"))
                .verifyComplete();
    }

    @Test
    public void testUpdateItem(){
        StepVerifier.create(itemReactiveRepository.findById("123")
                .map(item -> {
                     item.setPrice(350.0);
                     return item;
                }).flatMap(item ->
                     itemReactiveRepository.save(item) //this converts to a flux/mono as it is flatmap
                ))
        .expectSubscription()
        .expectNextMatches(item-> item.getPrice() == 350.0)
        .verifyComplete();
    }

    @Test
    public void testDeleteItem(){

        StepVerifier.create(itemReactiveRepository.deleteById("123"))
                .expectSubscription()
                .verifyComplete();
        //or
       Mono<Void> monoItem = itemReactiveRepository.findById("123")
               .map(Item::getId)
               .flatMap(id -> itemReactiveRepository.deleteById(id));

       StepVerifier.create(monoItem)
               .expectSubscription()
               .verifyComplete();

    }

}

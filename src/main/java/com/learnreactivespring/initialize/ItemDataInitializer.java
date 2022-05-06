package com.learnreactivespring.initialize;

import com.learnreactivespring.document.Item;
import com.learnreactivespring.document.ItemCapped;
import com.learnreactivespring.repository.ItemCappedReactiveRepository;
import com.learnreactivespring.repository.ItemReactiveRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.CollectionOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

@Component
@Slf4j
@Profile("!test") //dont want to run this for test profile
public class ItemDataInitializer implements CommandLineRunner {

    @Autowired
    ItemReactiveRepository itemReactiveRepository;

    @Autowired
    ItemCappedReactiveRepository itemCappedReactiveRepository;

    @Autowired
    MongoOperations mongoOperations;

    @Override
    public void run(String... args) throws Exception {
        initialDatsetUp();
        createCappedCollection(); //Never use capped collection for permanent storage
        dataSetUpForCappedCollection();
    }

    private void createCappedCollection() {
        mongoOperations.dropCollection(ItemCapped.class);
        mongoOperations.createCollection(ItemCapped.class,
                CollectionOptions.empty().maxDocuments(20).size(5000).capped());

    }

    private void dataSetUpForCappedCollection(){
        Flux<ItemCapped> itemCappedFlux = Flux.interval(Duration.ofSeconds(1))
                .map(i -> new ItemCapped(null, "Random Item"+i, (100.00+i)));
        itemCappedReactiveRepository.insert(itemCappedFlux)
                .subscribe(itemCapped ->{
                    log.info("Inserted Item: "+itemCapped);
                });
    }

    private void initialDatsetUp() {
        itemReactiveRepository.deleteAll()
                .thenMany(Flux.fromIterable(getData()))
                .flatMap(itemReactiveRepository::save)
                .thenMany(itemReactiveRepository.findAll())
                .subscribe(item ->{
                    System.out.println("Inserted from CommandLine Runner " +item);
                });
    }


    private List<Item> getData() {
        return Arrays.asList(Item.builder().description("Iphone").price(1000.0).build(),
                Item.builder().description("samsung TV").price(500.0).build(),
                Item.builder().description("Mac").price(1200.0).build(),
                Item.builder().description("Apple Watch").price(300.0).build(),
                Item.builder().id("123").description("Bose Speaker").price(300.0).build());
    }
}

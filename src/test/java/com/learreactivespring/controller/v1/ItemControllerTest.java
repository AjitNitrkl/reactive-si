package com.learreactivespring.controller.v1;

import com.learnreactivespring.LearnReactivespringApplication;
import com.learnreactivespring.document.Item;
import com.learnreactivespring.repository.ItemReactiveRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

import static com.learnreactivespring.controller.constants.ItemConstants.ITEM_ENDPOINT_V1;
import static org.junit.Assert.assertEquals;

/**
 * Got 2 issues in this class
 * Issue1- Hand to change in prefrence-  http://testing340.blogspot.com/2020/01/no-tests-found-for-given-includes.html
 * 1. File > Setting (Ctrl+Alt+S)
 * 2. Build, Execution, Deployment > Build Tools > gradle
 * 3. Run Tests using: Intellij IDEA
 *
 * Issue 2- Unable to find a @SpringBootConfiguration, you need to use @ContextConfiguration or @SpringBootTest(classes=...) with your test
 *https://stackoverflow.com/questions/43515279/error-unable-to-find-springbootconfiguration-when-doing-webmvctest-for-spring/43517967
 * update SpringBootTest annotation with
 * @SpringBootTest(classes={LearnReactivespringApplication.class})
 *
 */

@SpringBootTest(classes={LearnReactivespringApplication.class})
@RunWith(SpringRunner.class)
@DirtiesContext
@AutoConfigureWebTestClient   //needed for webtestclient
@ActiveProfiles("test")  //Activating test profile this is running since we are using springboottest to load context in case of Datamongotest context is not loaded
public class ItemControllerTest {

    @Autowired
    WebTestClient webTestClient;

    @Autowired
    ItemReactiveRepository itemReactiveRepository;

    @Before
    public void setUp(){
        /*itemReactiveRepository.deleteAll()
                .thenMany(Flux.fromIterable(getData()))
                .flatMap(itemReactiveRepository::save)
                .thenMany(itemReactiveRepository.findAll())
                .subscribe(item ->{
                    System.out.println("Inserted from CommandLine Runner " +item);
                });*/

        //or
        itemReactiveRepository.deleteAll()
                .thenMany(Flux.fromIterable(getData()))
                .flatMap(itemReactiveRepository::save)
                .doOnNext(item ->{
                    System.out.println("Inserted Item is "+item);
                })
                .blockLast();

    }

    private List<Item> getData() {
        return Arrays.asList(Item.builder().description("Iphone").price(1000.0).build(),
                Item.builder().description("samsung TV").price(500.0).build(),
                Item.builder().description("Mac").price(1200.0).build(),
                Item.builder().description("Apple Watch").price(300.0).build(),
                Item.builder().id("123").description("Bose Speaker").price(300.0).build());
    }


    @Test
    public void getAllItems()  {
        webTestClient.get().uri(ITEM_ENDPOINT_V1)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Item.class)
                .hasSize(5);
    }

    @Test
    public void getAllItems_1()  {
        webTestClient.get().uri(ITEM_ENDPOINT_V1)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Item.class)
                .hasSize(5)
                .consumeWith((response) ->{
                    List<Item> itemList = response.getResponseBody();
                    assertEquals(itemList.size(),5);
                });
    }

    @Test
    public void getAllItems_2()  {
       Flux<Item> fluxItems =  webTestClient.get().uri(ITEM_ENDPOINT_V1)
                .exchange()
                .expectStatus().isOk()
                .returnResult(Item.class)
                .getResponseBody().log();
        StepVerifier.create(fluxItems.log("Value from network: "))
                .expectSubscription()
                .expectNextCount(5).verifyComplete();
    }

    @Test
    public void getAItemTest(){

        webTestClient.get().uri(ITEM_ENDPOINT_V1.concat("/{id}"), "123")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.price",300.0);
    }


    @Test
    public void getAItemNotFoundTest(){
        webTestClient.get().uri(ITEM_ENDPOINT_V1.concat("/{id}"), "1234")
                .exchange()
                .expectStatus().isNotFound();

    }

    @Test
    public void createItemTest(){
        Item item = Item.builder().description("Canon Printer").price(50.0).build();
        webTestClient.post().uri(ITEM_ENDPOINT_V1)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .body(Mono.just(item), Item.class)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.id").isNotEmpty();
    }

    @Test
    public void deleteItemTest(){
        webTestClient.delete().uri(ITEM_ENDPOINT_V1.concat("/{id}"),"123")
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Void.class);
    }

    @Test
    public void updateItemTest(){
        Item newItem = Item.builder().id("123").description("Bose Portal Speaker ").price(399.0).build();
        webTestClient.put().uri(ITEM_ENDPOINT_V1.concat("/{id}"),"123")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .body(Mono.just(newItem),Item.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.price",399.0);
    }

    @Test
    public void updateItemTestNotFound(){
        Item newItem = Item.builder().id("123").description("Bose Portal Speaker ").price(399.0).build();
        webTestClient.put().uri(ITEM_ENDPOINT_V1.concat("/{id}"),"345")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .body(Mono.just(newItem),Item.class)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    public void exceptionTest(){
        webTestClient.get().uri(ITEM_ENDPOINT_V1+"/runTimeException")
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody(String.class)
                .isEqualTo("Runtime Exception Occurred..");
    }

    @Test
    public void exceptionTest_1(){
        webTestClient.get().uri(ITEM_ENDPOINT_V1+"/runTimeException")
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody()
                .jsonPath("$.message","Runtime Exception Occurred..");
    }
}

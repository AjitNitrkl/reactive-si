package com.learnreactivespring.controller.v1;

import com.learnreactivespring.document.Item;
import com.learnreactivespring.repository.ItemReactiveRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


import static com.learnreactivespring.controller.constants.ItemConstants.ITEM_ENDPOINT_V1;

@RestController
@Slf4j
public class ItemController {

    @Autowired
    ItemReactiveRepository itemReactiveRepository;

    @GetMapping(value = ITEM_ENDPOINT_V1, produces = MediaType.APPLICATION_STREAM_JSON_VALUE )
    public Flux<Item> getAllItems(){
       return itemReactiveRepository
               .findAll();
               //.delayElements(Duration.ofSeconds(2)).log();
    }

    @GetMapping(value = ITEM_ENDPOINT_V1+"/{id}")
    public Mono<ResponseEntity<Item>> getAItem(@PathVariable String id){
        return itemReactiveRepository.findById(id)
                .map(item -> new ResponseEntity<>(item, HttpStatus.OK))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping(value = ITEM_ENDPOINT_V1)
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Item> createItem(@RequestBody Item item){
        return itemReactiveRepository.save(item);
    }

    @DeleteMapping(value = ITEM_ENDPOINT_V1+"/{id}")
    public Mono<Void> deleteItem(@PathVariable  String id){
        return itemReactiveRepository.deleteById(id);  //since the call is async so we need to return Mono of type Void, in case of failure to delete we get error
    }

    @PutMapping(value = ITEM_ENDPOINT_V1+"/{id}")
    public Mono<ResponseEntity<Item>> updateItem(@PathVariable String id, @RequestBody Item item){
        return itemReactiveRepository.findById(id)
                .flatMap(currentItem ->{
                    currentItem.setPrice(item.getPrice());
                    currentItem.setDescription(item.getDescription());
                    return itemReactiveRepository.save(currentItem);
                })
                .map(updatedItem -> new ResponseEntity<>(updatedItem, HttpStatus.OK))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping(value = ITEM_ENDPOINT_V1+"/runTimeException")
    public Flux<Item> runTimeException(){
        return itemReactiveRepository.findAll()
                    .concatWith(Mono.error(new RuntimeException("Runtime Exception Occurred..")));
    }
    //Moved to ControllerAdvice class

    /*@ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRunTimeExc(RuntimeException ex){
        log.error("Error occurred..."+ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(ex.getMessage());
    }*/
}

package com.learnreactivespring.handler;

import com.learnreactivespring.document.Item;
import com.learnreactivespring.document.ItemCapped;
import com.learnreactivespring.repository.ItemCappedReactiveRepository;
import com.learnreactivespring.repository.ItemReactiveRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import static org.springframework.web.reactive.function.BodyInserters.fromObject;

@Component
public class ItemsHandler {

    @Autowired
    ItemReactiveRepository itemReactiveRepository;

    @Autowired
    ItemCappedReactiveRepository itemCappedReactiveRepository;

    public Mono<ServerResponse> getAllItems(ServerRequest serverRequest){
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(itemReactiveRepository.findAll(), Item.class);
    }

    public Mono<ServerResponse> getAItem(ServerRequest serverRequest){
        String id = serverRequest.pathVariable("id");
        Mono<Item> itemMono = itemReactiveRepository.findById(id);

        return itemMono.flatMap(item -> ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(fromObject(item))) //converts object to a publisher
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> createItem(ServerRequest serverRequest){
        Mono<Item> itemMono = serverRequest.bodyToMono(Item.class);
        return itemMono.flatMap(item -> ServerResponse.accepted()
                .contentType(MediaType.APPLICATION_JSON)
                .body(itemReactiveRepository.save(item), Item.class));
    }

    public Mono<ServerResponse> deleteItem(ServerRequest serverRequest){
        String id = serverRequest.pathVariable("id");
        Mono<Void> deleteItem = itemReactiveRepository.deleteById(id);

        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(deleteItem, Void.class);
    }

    public Mono<ServerResponse> updateItem(ServerRequest serverRequest){
        String id = serverRequest.pathVariable("id");
         Mono<Item> updateItem = serverRequest.bodyToMono(Item.class)
                 .flatMap(item ->{
                   Mono<Item> itemMono =   itemReactiveRepository.findById(id)
                             .flatMap(currentItem ->{
                                 currentItem.setDescription(item.getDescription());
                                 currentItem.setPrice(item.getPrice());
                                 return itemReactiveRepository.save(currentItem);
                             });
                           return itemMono;
                 });

        return updateItem.flatMap(item ->
                ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(fromObject(item)))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> itemsEx(ServerRequest serverRequest){
        throw new RuntimeException("Runtime Exception Occurred");
    }

    public Mono<ServerResponse> itemsStream(ServerRequest serverRequest){
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_STREAM_JSON)
                .body(itemCappedReactiveRepository.findItemsBy(), ItemCapped.class);
    }

}

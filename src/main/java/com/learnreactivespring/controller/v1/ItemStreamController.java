package com.learnreactivespring.controller.v1;

import com.learnreactivespring.document.ItemCapped;
import com.learnreactivespring.repository.ItemCappedReactiveRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.awt.*;

import static com.learnreactivespring.controller.constants.ItemConstants.ITEM_STREAM_ENDPOINT_V1;

@RestController
public class ItemStreamController {

    @Autowired
    ItemCappedReactiveRepository itemCappedReactiveRepository;

    @GetMapping(value = ITEM_STREAM_ENDPOINT_V1, produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
    public Flux<ItemCapped> getItemsStream(){
        return itemCappedReactiveRepository.findItemsBy();
    }
}

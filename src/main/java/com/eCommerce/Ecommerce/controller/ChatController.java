package com.eCommerce.Ecommerce.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.eCommerce.Ecommerce.Services.SupportService;

import reactor.core.publisher.Flux;

@RestController
public class ChatController {

    @Autowired
    private SupportService supportService;

    @PostMapping(value = "/stream", produces = "text/event-stream")
    public Flux<String> streamResponse(
            @RequestParam(value = "inputText") String inputText,
            @RequestParam(value = "page", required = false) String page) {

        return supportService.getResponse(inputText, page);
    }

}
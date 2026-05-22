package com.eCommerce.Ecommerce.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.eCommerce.Ecommerce.Services.SupportService;

import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/support")
public class SupportAIController {

    @Autowired
    private SupportService supportService;

    @PostMapping(value = "/query", produces = "text/event-stream")
    public Flux<String> streamResponse(
            @RequestParam(value = "question") String question) {

        return supportService.getResponse(question, "");
    }

}


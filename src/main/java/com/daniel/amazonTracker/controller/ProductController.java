package com.daniel.amazonTracker.controller;

import com.daniel.amazonTracker.service.ProductService;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/track")
@AllArgsConstructor
public class ProductController {

    private final ProductService service;

    @Autowired
    public ProductController(ProductService service) {
        this.service = service;
    }

    @PostMapping(consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE})
    public void getMessage(@RequestParam("From") String From, @RequestParam("To") String To, @RequestParam("Body") String Body) {
        service.trackAndSendReply(From, To, Body);
    }

}

package com.hanghae.hackathon.Ant.controller;

import com.hanghae.hackathon.Ant.service.RecycleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ApiController {

    @Autowired
    RecycleService service;

    @GetMapping("/ask")
    public Object ask() {
        return service.getResult();
    }
}

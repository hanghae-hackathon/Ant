package com.hanghae.hackathon.Ant.controller;

import com.hanghae.hackathon.Ant.service.RecycleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class ApiController {

    @Autowired
    RecycleService service;

    @GetMapping("/ask")
    public void ask() {
        service.getResult();
    }

    @PostMapping("/question")
    public String question(
            @RequestPart(required = false) String reqInfo,
            @RequestPart(required = false) MultipartFile attachFile) {

        return service.getAnswer(reqInfo, attachFile);
    }
}

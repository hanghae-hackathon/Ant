package com.hanghae.hackathon.Ant.controller;

import com.hanghae.hackathon.Ant.service.RecycleService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.util.HashMap;
import java.util.Map;

@RestController
public class ApiController {

    @Autowired
    RecycleService service;

//    @GetMapping("/ask")
//    public void ask() {
//        service.getResult();
//    }
//
//    @PostMapping("/question")
//    public String question(
//            @RequestPart(required = false) String reqInfo,
//            @RequestPart(required = false) MultipartFile attachFile) {
//
//        return service.getAnswer(reqInfo, attachFile);
//    }

    @PostMapping("/ask")
    public ResponseEntity ask(MultipartHttpServletRequest request){
        Map<String,Object> resultMap = new HashMap<String,Object>();
        resultMap.put("result", service.getAnswer(null, request.getFile("imageFile")));
        return new ResponseEntity<>(resultMap, HttpStatus.OK);
    }

    @PostMapping("/askText")
    public ResponseEntity askText(HttpServletRequest request, @RequestBody Map<String, Object> param){
        Map<String,Object> resultMap = new HashMap<String,Object>();
        String at = param.getOrDefault("askText", "").toString();
        resultMap.put("result", service.getAnswer(param.getOrDefault("askText", "").toString(), null));
        return new ResponseEntity<>(resultMap, HttpStatus.OK);
    }
}

package com.hanghae.hackathon.Ant.controller;

import com.hanghae.hackathon.Ant.service.RecycleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class UIController {

    @Autowired
    RecycleService service;

    @Value("${file.path}")
    String filePath;

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/testview")
    public ModelAndView testview() {
        ModelAndView view = new ModelAndView("testview");
        view.addObject("path", service.imageB64(filePath + "hot.jpg"));
        return view;
    }
}

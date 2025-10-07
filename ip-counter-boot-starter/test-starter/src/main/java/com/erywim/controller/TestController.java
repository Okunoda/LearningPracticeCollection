package com.erywim.controller;

import com.erywim.service.IpCounterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    @Autowired
    private IpCounterService ipCounterService;

    @PostMapping("/test")
    public String test(){
        ipCounterService.record();
        return "hello world";
    }
}

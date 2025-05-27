package com.example.systeme_suivi_ticket.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/test") // Changed from "/" to "/test"
    public String home() {
        return "This is the test page.";
    }
}
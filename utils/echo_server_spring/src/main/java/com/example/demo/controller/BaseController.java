package com.example.demo.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("")
public class BaseController {

    @GetMapping("/")
    public ResponseEntity<String> auth() {
        String body = new String("tzewqt7632762-rtrttttree");

        return new ResponseEntity<>(body, HttpStatus.OK);
    }
}

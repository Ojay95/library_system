package com.Library.System.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

public class HomeController {

    @GetMapping("/")
    @ResponseBody
    public String home() {
        return "Welcome to Our Library System!";
}
}

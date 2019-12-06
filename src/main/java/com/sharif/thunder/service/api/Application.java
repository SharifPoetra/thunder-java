package com.sharif.thunder.service.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class Application {
  @GetMapping("/")
  public String home(@RequestParam(value = "name", defaultValue = "World") String name) {
    return "index";
  }
}
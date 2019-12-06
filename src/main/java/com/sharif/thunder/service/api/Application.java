package com.sharif.thunder.service.api;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class Application {

  @GetMapping("/")
  public String home() {
    return "index";
  }
}

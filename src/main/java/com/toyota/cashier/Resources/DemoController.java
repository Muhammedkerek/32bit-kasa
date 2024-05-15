package com.toyota.cashier.Resources;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DemoController {
    @GetMapping("/demo")
    public ResponseEntity<String> demo(){
        return ResponseEntity.ok("hello from secured url");
    }
}

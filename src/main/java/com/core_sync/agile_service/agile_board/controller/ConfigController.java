package com.core_sync.agile_service.agile_board.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ConfigController {

    @GetMapping("/health")
    public String health() {
        return "OK";
    }


}

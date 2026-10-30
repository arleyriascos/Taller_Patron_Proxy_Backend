package com.arley.arlynx;

import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/arlynx")
public class PulseController {

    @GetMapping("/pulse")
    public Map<String, String> pulse() {
        return Map.of("status", "UP", "service", "arlynx-api");
    }
}

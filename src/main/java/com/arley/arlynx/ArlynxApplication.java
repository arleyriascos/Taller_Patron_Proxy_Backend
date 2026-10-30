package com.arley.arlynx;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ArlynxApplication {

    public static void main(String[] args) {
        SpringApplication.run(ArlynxApplication.class, args);
    }
}

package com.example.prodolymp;

import de.codecentric.boot.admin.server.config.EnableAdminServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
@EnableAdminServer
@SpringBootApplication
public class ProdOlympApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProdOlympApplication.class, args);
    }

}

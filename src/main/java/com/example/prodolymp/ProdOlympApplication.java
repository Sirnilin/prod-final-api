package com.example.prodolymp;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import tech.ailef.snapadmin.external.SnapAdminAutoConfiguration;

@SpringBootApplication
@EnableWebMvc
@ImportAutoConfiguration(SnapAdminAutoConfiguration.class)
public class ProdOlympApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProdOlympApplication.class, args);
    }

}

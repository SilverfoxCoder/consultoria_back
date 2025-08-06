package com.codethics.consultoria;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ConsultoriaBackApplication {
    public static void main(String[] args) {
        SpringApplication.run(ConsultoriaBackApplication.class, args);
    }
}
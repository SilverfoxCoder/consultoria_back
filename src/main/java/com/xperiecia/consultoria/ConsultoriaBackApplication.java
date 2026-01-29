package com.xperiecia.consultoria;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ConsultoriaBackApplication {
    public static void main(String[] args) {
        System.out.println("🚀 [STARTUP] Application starting... Force logging check.");
        SpringApplication.run(ConsultoriaBackApplication.class, args);
    }
}

package com.moviego;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class MoviegoApplication {

    public static void main(String[] args) {
        SpringApplication.run(MoviegoApplication.class, args);
    }

}

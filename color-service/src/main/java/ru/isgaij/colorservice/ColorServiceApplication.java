package ru.isgaij.colorservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication
@EnableKafka
public class ColorServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ColorServiceApplication.class, args);
    }
}

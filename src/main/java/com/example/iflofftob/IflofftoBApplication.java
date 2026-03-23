package com.example.iflofftob;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * ifLotto 백엔드 애플리케이션 진입점.
 */
@SpringBootApplication
@EnableScheduling
public class IflofftoBApplication {

    public static void main(String[] args) {
        SpringApplication.run(IflofftoBApplication.class, args);
    }

}

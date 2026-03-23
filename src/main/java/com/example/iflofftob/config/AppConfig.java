package com.example.iflofftob.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * 애플리케이션 공통 Bean 설정 클래스.
 * HTTP 클라이언트 등 인프라 레벨 Bean을 등록한다.
 */
@Configuration
public class AppConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}

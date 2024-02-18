package com.baseball;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SpringBootApplication
public class BaseballSpringApplication {

    @Value("${cloudflare.ip-ranges}")
    private String[] ipRanges;

    public static void main(String[] args) {
        SpringApplication.run(BaseballSpringApplication.class, args);
    }

    @Bean
    public WebMvcConfigurer webMvcConfigurer() {

        return new WebMvcConfigurer() {

            @Override
            public void addCorsMappings(CorsRegistry registry) {
                List<String> origins = new ArrayList<>(Arrays.asList(ipRanges));
                origins.add("http://localhost");
                origins.add("http://localhost:3000");
                origins.add("https://bulls-and-cows.kr");
                origins.add("https://www.bulls-and-cows.kr");
                origins.add("https://bulls-and-cows.shop");
                origins.add("https://www.bulls-and-cows.shop");

                registry.addMapping("/**")
                    .allowedOrigins(origins.toArray(new String[0]))
                    .allowedHeaders("*")
                    .allowedMethods("*").allowCredentials(true).maxAge(3600);

            }
        };
    }

}

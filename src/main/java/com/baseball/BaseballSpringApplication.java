package com.baseball;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
public class BaseballSpringApplication {

	public static void main(String[] args) {
		SpringApplication.run(BaseballSpringApplication.class, args);
}

	@Bean
	public static WebMvcConfigurer webMvcConfigurer() {

		return new WebMvcConfigurer() {

			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/**")
					.allowedOrigins("http://localhost:3000/")
					.allowedHeaders("*")
					.allowedMethods("*").allowCredentials(true).maxAge(3600);

			}
		};
	}

}

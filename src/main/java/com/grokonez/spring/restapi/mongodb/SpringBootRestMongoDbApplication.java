package com.grokonez.spring.restapi.mongodb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class SpringBootRestMongoDbApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringBootRestMongoDbApplication.class, args);
	}
}

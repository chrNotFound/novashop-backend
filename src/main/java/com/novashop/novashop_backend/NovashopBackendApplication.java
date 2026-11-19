package com.novashop.novashop_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan("com.novashop.novashop_backend.model")
@EnableJpaRepositories("com.novashop.novashop_backend.repository")
public class NovashopBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(NovashopBackendApplication.class, args);
	}

}

package com.main;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class PaymentProcessingSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(PaymentProcessingSystemApplication.class, args);
	}

}

package com.onlinevotingsystem.voting_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class VotingBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(VotingBackendApplication.class, args);
	}

}

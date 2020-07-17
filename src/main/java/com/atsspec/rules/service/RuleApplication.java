package com.atsspec.rules.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan({"com.atsspec.rules"})
@SpringBootApplication
public class RuleApplication {


	public static void main(String[]args) {
		SpringApplication.run(RuleApplication.class, args);
	}
}

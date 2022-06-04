package com.upbit.application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan(basePackages={"com.upbit.controller"})
@SpringBootApplication
public class UpbitApplication {

	public static void main(String[] args) {
		SpringApplication.run(UpbitApplication.class, args);
	}

}

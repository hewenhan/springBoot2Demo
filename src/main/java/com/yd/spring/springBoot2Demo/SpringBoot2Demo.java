package com.yd.spring.springBoot2Demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class SpringBoot2Demo {

	public static void main(String[] args) {

		SpringApplication.run(SpringBoot2Demo.class, args);
	}

}


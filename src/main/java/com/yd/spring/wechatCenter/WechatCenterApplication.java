package com.yd.spring.wechatCenter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class WechatCenterApplication {

	public static void main(String[] args) {

		SpringApplication.run(WechatCenterApplication.class, args);
	}

}


package com.knugpt.knugpt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class KnugptApplication {

	public static void main(String[] args) {
		SpringApplication.run(KnugptApplication.class, args);
	}

}

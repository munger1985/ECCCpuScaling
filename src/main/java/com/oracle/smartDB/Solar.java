package com.oracle.smartDB;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class Solar {
	private static  final Logger log = LoggerFactory.getLogger(Solar.class);

	public static void main(String[] args) {
		log.info("Solar app is started=========");
		SpringApplication.run(Solar.class);
	}
}

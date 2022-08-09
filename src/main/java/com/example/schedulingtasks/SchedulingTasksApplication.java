package com.example.schedulingtasks;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SchedulingTasksApplication {
	private static  final Logger log = LoggerFactory.getLogger(SchedulingTasksApplication.class);

	public static void main(String[] args) {
		log.info("dbrun app is started=========");
		SpringApplication.run(SchedulingTasksApplication.class);
	}
}

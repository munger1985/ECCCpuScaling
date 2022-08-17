package jingsong.oracle.smart;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class DBRun {
	private static  final Logger log = LoggerFactory.getLogger(DBRun.class);

	public static void main(String[] args) {
		log.info("dbrun app is started=========");
		SpringApplication.run(DBRun.class);
	}
}

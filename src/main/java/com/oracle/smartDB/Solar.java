package com.oracle.smartDB;

import com.oracle.bmc.ConfigFileReader;
import com.oracle.bmc.auth.AuthenticationDetailsProvider;
import com.oracle.bmc.auth.ConfigFileAuthenticationDetailsProvider;
import com.oracle.bmc.database.DatabaseClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.io.IOException;

@SpringBootApplication
@EnableScheduling
public class Solar {
	private static  final Logger log = LoggerFactory.getLogger(Solar.class);

	@Bean
	public DatabaseClient databaseClient() {
		final ConfigFileReader.ConfigFile configFile;
		try {
			configFile = ConfigFileReader.parseDefault();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		final AuthenticationDetailsProvider provider = new ConfigFileAuthenticationDetailsProvider(configFile);


		/* Create a service client */
		DatabaseClient client = DatabaseClient.builder().build(provider);

		return client;
	}
	public static void main(String[] args) {
		log.info("Solar app is started=========v7");
		SpringApplication.run(Solar.class);
	}
}

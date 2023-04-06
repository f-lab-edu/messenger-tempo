package com.messenger;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.SpringVersion;

@Slf4j
@SpringBootApplication
public class MessengerTempoApplication {

	public static void main(String[] args) {
		SpringApplication.run(MessengerTempoApplication.class, args);

		String getVersion = SpringVersion.getVersion();
		log.info("Spring version = {}", getVersion);
	}

}

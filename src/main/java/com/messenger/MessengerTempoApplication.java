package com.messenger;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.SpringVersion;

@SpringBootApplication
public class MessengerTempoApplication {

	public static void main(String[] args) {
		SpringApplication.run(MessengerTempoApplication.class, args);

		String getVersion = SpringVersion.getVersion();
		System.out.println("Spring version = "+getVersion);
	}

	public void test() {

	}

}

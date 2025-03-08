package com.fastcampus.sns;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

//exclude = DataSourceAutoConfiguration.class
@SpringBootApplication()
public class SnsServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(SnsServiceApplication.class, args);
	}
}

package com.geng.hd.youzhan;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude={DataSourceAutoConfiguration.class})
public class YouzhanApplication {

	public static void main(String[] args) {
		SpringApplication.run(YouzhanApplication.class, args);
	}
}

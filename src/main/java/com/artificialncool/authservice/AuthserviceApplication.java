package com.artificialncool.authservice;

import com.artificialncool.authservice.service.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication(exclude={DataSourceAutoConfiguration.class})
@EnableMongoRepositories
public class AuthserviceApplication {

	@Bean
	public ApplicationRunner applicationStartupRunner() {
		return new ApplicationRunner();
	}
	public static void main(String[] args) {
		SpringApplication.run(AuthserviceApplication.class, args);
	}

}

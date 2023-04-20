package com.player.props;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;


@EnableScheduling
@EnableCaching
@Configuration
@SpringBootApplication
public class PlayerPropsApplication {

	public static void main(String[] args) {
		SpringApplication.run(PlayerPropsApplication.class, args);
	}

}

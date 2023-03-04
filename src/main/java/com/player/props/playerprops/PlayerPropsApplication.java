package com.player.props.playerprops;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.google.auto.value.AutoValue;

@SpringBootApplication
@AutoValue
public class PlayerPropsApplication {

	public static void main(String[] args) {
		SpringApplication.run(PlayerPropsApplication.class, args);
	}

}

package me.yattaw.snowingtoday;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SnowingTodayApplication {

	public static final String API_KEY = System.getenv("WEATHER_API_KEY");

	public static void main(String[] args) {
		SpringApplication.run(SnowingTodayApplication.class, args);
	}

}

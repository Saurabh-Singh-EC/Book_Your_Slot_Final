package com.codeWithSrb.BookYourSlot;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class BookYourSlotApplication {

	public static void main(String[] args) {
		SpringApplication.run(BookYourSlotApplication.class, args);
	}

	@Bean
	public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
		return args -> {

			System.out.println("command line runner is being instantiated");

		};
	}
}

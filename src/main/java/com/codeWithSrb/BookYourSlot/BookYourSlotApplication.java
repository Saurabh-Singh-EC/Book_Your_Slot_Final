package com.codeWithSrb.BookYourSlot;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import java.util.Arrays;

@SpringBootApplication
public class BookYourSlotApplication {

	public static void main(String[] args) {
		SpringApplication.run(BookYourSlotApplication.class, args);
	}

	@Bean
	public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
		return args -> {

			System.out.println("command line runner is being instantiated");
			System.out.println("let's inspect the beans provided by spring boot");

			String[] beanNames = ctx.getBeanDefinitionNames();
			Arrays.stream(beanNames).forEach(name -> System.out.println("bean name : " + name));

			System.out.println("command line runner bean has been instantiated");

		};
	}
}

package com.mini.ai_chatbot;

import org.springframework.boot.SpringApplication;

public class TestAichatbotApplication {

	public static void main(String[] args) {
		SpringApplication.from(AichatbotApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}

package com.sbproject.deokhugam.book.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.genai.Client;

@Configuration
public class GeminiConfig {

	@Value("${GEMINI_API_KEY}")
	String GEMINI_API_KEY;

	@Bean
	public Client geminiClient() {
		return Client.builder()
		             .apiKey(GEMINI_API_KEY)
		             .build();
	}
}

package com.example.demo.web;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.example.demo.Login.SessionInfo;
import com.example.demo.facade.ClientFacade;

import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig {
	
	
	
	
	
	@Bean(name = "sessions")
	@Primary
	public Map<String, SessionInfo> sessions() {
		return new Hashtable<String, SessionInfo>();
	}
	
	
	
	@Bean
	public Docket api() {
		return new Docket(DocumentationType.SWAGGER_2).select().apis(RequestHandlerSelectors.any())
				.paths(PathSelectors.any()).build();
	}
	
	
}

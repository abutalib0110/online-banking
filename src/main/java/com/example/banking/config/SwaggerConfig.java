package com.example.banking.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI publicApi() {
    	return new OpenAPI()
				.info(new Info().title("Abu Talib Online Banking"))				
				.addSecurityItem(new SecurityRequirement().addList("Online Banking Security"))
				.components(new Components().addSecuritySchemes("Online Banking Security", new SecurityScheme()
						.name("Online-Banking-Security").type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT")));
		
    }
}

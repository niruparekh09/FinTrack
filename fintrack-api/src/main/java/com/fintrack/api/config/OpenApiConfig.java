package com.fintrack.api.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI fintrackOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("FinTrack API")
                        .description("Backend API documentation for FinTrack")
                        .version("1.0.0"));
    }
}
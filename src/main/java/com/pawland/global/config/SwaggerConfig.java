package com.pawland.global.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI serverApiConfig() {
        return new OpenAPI()
            .info(new Info().title("PAWLAND API")
                .description("PAWLAND API SWAGGER UI입니다."));
    }
}

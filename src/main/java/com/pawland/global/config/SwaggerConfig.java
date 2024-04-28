package com.pawland.global.config;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@SecurityScheme(
    name = "jwt-cookie",
    type = SecuritySchemeType.APIKEY,
    in = SecuritySchemeIn.HEADER
)
public class SwaggerConfig {

    @Bean
    public OpenAPI serverApiConfig() {
        Server server = new Server();
        server.setUrl("https://www.midcon.store");
        server.description("백엔드 도메인");
        return new OpenAPI()
            .servers(List.of(server))
            .info(new Info().title("PAWLAND API")
                .description("PAWLAND API SWAGGER UI입니다."));
    }
}

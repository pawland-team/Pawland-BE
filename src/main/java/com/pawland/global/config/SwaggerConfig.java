package com.pawland.global.config;

import com.pawland.global.config.swagger.SecurityNotRequired;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.HandlerMethod;

import java.util.Collections;
import java.util.List;

@Configuration
@RequiredArgsConstructor
@SecurityScheme(
    name = "jwt-cookie",
    type = SecuritySchemeType.APIKEY,
    in = SecuritySchemeIn.HEADER
)
public class SwaggerConfig {

    private final AppConfig appConfig;

    @Bean
    public OpenAPI serverApiConfig() {
        Server server = new Server();
        server.setUrl(appConfig.getBackUrl());
        server.description("백엔드 도메인");
        return new OpenAPI()
            .servers(List.of(server))
            .info(new Info().title("PAWLAND API")
                .description("PAWLAND API SWAGGER UI입니다."));
    }

    @Bean
    public OperationCustomizer customize() {
        return (Operation operation, HandlerMethod handlerMethod) -> {
            SecurityNotRequired annotation = handlerMethod.getMethodAnnotation(SecurityNotRequired.class);
            // SecurityNotRequire 어노테이션있을시 스웨거 시큐리티 설정 삭제
            if (annotation != null) {
                operation.security(Collections.emptyList());
            }
            return operation;
        };
    }
}

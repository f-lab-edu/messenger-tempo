package com.messenger;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger UI : http://localhost:8080/swagger-ui/index.html
 * OpenAPI description : http://localhost:8080/v3/api-docs
 */
@Configuration
public class OpenApiConfig {

    private static final String VERSION = "v1";

    @Bean
    public OpenAPI openAPI(@Value(VERSION) String appVersion) {
        Info info = new Info()
                .title("메신저")
                .version(appVersion)
                .description("Spring 기반 채팅 메신저 API");

        return new OpenAPI().info(info).components(
                new Components()
                        .addSecuritySchemes(
                                "authorization",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                        )
        );
    }
}

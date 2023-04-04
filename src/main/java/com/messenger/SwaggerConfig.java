package com.messenger;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

/**
 * Swagger 접속 url : http://localhost:8080/swagger-ui/index.html
 */
@Configuration
public class SwaggerConfig implements WebMvcConfigurer {

    private final String version = "v1";

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.any())
                .build()
                .apiInfo(apiInfo());
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("메신저")
                .description("Spring 기반 채팅 메신저 API")
                .version(version)
                .contact(new Contact("안준혁", "https://github.com/f-lab-edu/messenger-tempo/", "e-mail"))
                .build();
    }
}

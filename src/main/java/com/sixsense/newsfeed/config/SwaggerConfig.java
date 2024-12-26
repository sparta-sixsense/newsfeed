package com.sixsense.newsfeed.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(
        info = @Info(
                title = "Newsfeed project api docs (made my '육성불패')",
                description = "Newsfeed project api docs",
                version = "v1"
        )
)
@Configuration
public class SwaggerConfig {

//    @Bean
//    public OpenAPI openAPI() {
//        String securityJwtName = "JWT";
//        SecurityRequirement securityRequirement = new SecurityRequirement().addList(securityJwtName);
//        Components components = new Components()
//                .addSecuritySchemes(securityJwtName, new SecurityScheme()
//                        .name(securityJwtName)
//                        .type(SecurityScheme.Type.HTTP)

    /// /                        .scheme(BEARER_TOKEN_PREFIX)
//                        .bearerFormat(securityJwtName));
//
//        return new OpenAPI()
//                .addSecurityItem(securityRequirement)
//                .components(components);
//    }
    @Bean
    public OpenAPI openAPI() {
        String securityJwtName = "JWT";
        SecurityRequirement securityRequirement = new SecurityRequirement().addList(securityJwtName);

        // Security Schema 설정 (Bearer 접두사 없이 우선 토큰만 받도록 설정)
        Components components = new Components()
                .addSecuritySchemes(securityJwtName, new SecurityScheme()
                        .name(securityJwtName)
                        .type(SecurityScheme.Type.APIKEY)
                        .in(SecurityScheme.In.HEADER)
                        .name("Authorization")
                );

        return new OpenAPI()
                .addSecurityItem(securityRequirement)
                .components(components);
    }

}
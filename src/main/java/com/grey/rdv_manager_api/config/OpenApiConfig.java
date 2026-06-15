package com.grey.rdv_manager_api.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    private static final String BEARER_AUTH = "bearerAuth";

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("RDV Manager API")
                .version("3.0")
                .description(
                    "Appointment booking REST API. " +
                    "Login via POST /api/auth/login, " +
                    "copy the token, click Authorize, paste it (without the Bearer prefix)."
                )
                .contact(new Contact()
                    .name("RDV Manager")
                    .email("admin@rdv.com")
                )
            )
            // Register the JWT bearer scheme once
            .components(new Components()
                .addSecuritySchemes(BEARER_AUTH,
                    new SecurityScheme()
                        .name(BEARER_AUTH)
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")
                )
            )
            // Apply it globally — every endpoint shows the padlock
            .addSecurityItem(new SecurityRequirement().addList(BEARER_AUTH));
    }
}
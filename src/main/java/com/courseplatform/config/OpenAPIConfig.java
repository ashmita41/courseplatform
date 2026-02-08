package com.courseplatform.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenAPIConfig {
    
    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "Bearer Authentication";
        
        return new OpenAPI()
                .servers(List.of(
                        new Server()
                                .url("https://courseplatform-production.up.railway.app")
                                .description("Production Server (HTTPS)"),
                        new Server()
                                .url("http://localhost:8080")
                                .description("Local Development Server")
                ))
                .info(new Info()
                        .title("Course Platform API")
                        .version("1.0")
                        .description("Backend API for learning platform with course browsing, enrollment, and progress tracking")
                        .contact(new Contact()
                                .name("Your Name")
                                .email("your.email@example.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("http://www.apache.org/licenses/LICENSE-2.0.html")))
                // Don't add global security requirement - let controllers specify it individually
                // This allows public endpoints to work without authentication in Swagger UI
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName,
                                new SecurityScheme()
                                        .name(securitySchemeName)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Enter JWT token obtained from /api/auth/login")));
    }
}

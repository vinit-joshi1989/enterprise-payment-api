package com.vinitjoshi.payment.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI enterprisePaymentApiOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Enterprise Payment API")
                        .version("1.0.0")
                        .description(
                                "REST API for creating, retrieving, updating, "
                                        + "and deleting payments."
                        )
                        .contact(new Contact()
                                .name("Vinit Joshi")
                                .email("vinitjoshi1989@gmail.com")));
    }
}
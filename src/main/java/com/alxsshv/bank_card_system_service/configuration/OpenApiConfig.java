package com.alxsshv.bank_card_system_service.configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;

@OpenAPIDefinition(
        info = @Info(
                title = "Bank cards system Api",
                description = "API системы управления банковскими картами",
                version = "1.0.0",
                contact = @Contact(
                        name = "Shvariov Aleksey",
                        email = "alxsshv@gmail.com"
                )
        )
)
public class OpenApiConfig {

}
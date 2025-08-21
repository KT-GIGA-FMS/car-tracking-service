package com.kt_giga_fms.car_tracking.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("KT Giga FMS Car Tracking Service API")
                        .description("차량 추적 및 위치 모니터링 서비스 API 문서")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("KT Giga FMS Team")
                                .email("support@kt-giga-fms.com")
                                .url("https://kt-giga-fms.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("Local Development Server"),
                        new Server()
                                .url("https://api.kt-giga-fms.com")
                                .description("Production Server")
                ));
    }
}

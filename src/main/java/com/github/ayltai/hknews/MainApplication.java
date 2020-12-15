package com.github.ayltai.hknews;

import java.util.Collections;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.lang.Nullable;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;

@SpringBootApplication
@EnableConfigurationProperties(MainConfiguration.class)
public class MainApplication {
    public static void main(@Nullable final String[] args) {
        SpringApplication.run(MainApplication.class, args);
    }

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("HK News API")
                .description("Serves aggregated news from 13 local news publishers in Hong Kong")
                .version("2.0.0")
                .contact(new Contact()
                    .name("GitHub")
                    .url("https://github.com/ayltai/hknews"))
                .license(new License()
                    .name("MIT")
                    .url("https://github.com/ayltai/hknews/blob/master/LICENSE")))
            .servers(Collections.singletonList(new Server().url("https://hknews.dev")));
    }
}

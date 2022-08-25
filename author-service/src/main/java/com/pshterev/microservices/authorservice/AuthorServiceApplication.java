package com.pshterev.microservices.authorservice;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.net.MalformedURLException;
import java.net.URL;

@SpringBootApplication
public class AuthorServiceApplication {

   @Value("${serverAddress:https://default}")
   private String serverAddress; // base server address for example 'example.com'
   @Value("${version:0.0}")
   private String version;

   private final String SERVICE_PATH = "authors"; // URL path for this service

   public static void main(String[] args) {
      SpringApplication.run(AuthorServiceApplication.class, args);
   }

   @Bean
   public OpenAPI customOpenAPI() throws MalformedURLException {
      return new OpenAPI()
              .addServersItem(new Server().url(new URL(new URL(serverAddress), SERVICE_PATH).toString()))
              .info(new Info().title("Author service").version(version));
   }
}

package pl.jkuznik.trucktracking.openAPI.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
class SwaggerConfig {

    @Bean
    public OpenAPI myOpenAPI() {
        Server devServer = new Server();
        devServer.setUrl("http://localhost:8080");
        devServer.setDescription("Server URL in Development environment");

        Server prodServer = new Server();
        prodServer.setUrl("http://localhost:8081");
        prodServer.setDescription("Server URL in Production environment");

        Contact contact = new Contact();
        contact.setEmail("janusz.kuznik89@gmail.com");
        contact.setName("Truck Tracking");
        contact.setUrl("https://www.github.com/jkuznik");

        License mitLicense = new License().name("GPL License").url("https://choosealicense.com/licenses/gpl-3.0/");

        Info info = new Info()
                .title("Truck Tracker")
                .version("1.0")
                .contact(contact)
                .description("This API exposes endpoints to manage truck and trailers.")
                .license(mitLicense);

        return new OpenAPI().info(info).servers(List.of(devServer, prodServer));
    }
}


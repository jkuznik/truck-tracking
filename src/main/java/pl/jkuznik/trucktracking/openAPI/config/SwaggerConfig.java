package pl.jkuznik.trucktracking.openAPI.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.core.util.Json;
import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

@Configuration
class SwaggerConfig {

    @Bean
    public OpenAPI myOpenAPI() {

        try {
            Path path = Paths.get("src/main/resources/swagger.yaml");
            InputStream inputStream = Files.newInputStream(path);
            Yaml yaml = new Yaml();
            Map<String, Object> yamlMap = yaml.load(inputStream);

            OpenAPI openAPI = convertToOpenAPI(yamlMap);

            return openAPI;
        } catch (Exception e) {
            e.printStackTrace();
            return new OpenAPI();
        }
    }

    private OpenAPI convertToOpenAPI(Map<String, Object> yamlMap) {
        ObjectMapper mapper = Json.mapper();
        return mapper.convertValue(yamlMap, OpenAPI.class);
    }
}


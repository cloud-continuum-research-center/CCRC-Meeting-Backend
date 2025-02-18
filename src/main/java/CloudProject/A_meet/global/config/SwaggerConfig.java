package CloudProject.A_meet.global.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@OpenAPIDefinition(

        info = @Info(title = "AI기반 비대면 미팅",
                description = "2025 World IT Show",
                version = "v1")
)

@Configuration
public class SwaggerConfig {
    private String serverUrl;

    @Bean
    public OpenAPI openAPI() {
        Server localServer = new Server().url("https://localhost:8443").description("Local server");

        Server dynamicServer = null;
        if (serverUrl != null) {
            dynamicServer = new Server().url(serverUrl).description("Configured server");
        }

        OpenAPI openAPI = new OpenAPI()
                .components(new Components())
                .servers(List.of(localServer));

        if (dynamicServer != null) {
            openAPI.getServers().add(dynamicServer);
        }

        return openAPI;
    }
}
package CloudProject.A_meet.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(
                        "http://localhost:5173",
                        "https://localhost:5173",
                        "https://127.0.0.1:5173",
                        "http://127.0.0.1:5173",
                        "http://localhost:8080",
                        "https://file-space.store"
                )
                .allowedMethods("GET", "POST", "PATCH", "DELETE", "PUT")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3000);
    }
}

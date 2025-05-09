package dk.sdu.cbse.scoring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Entry point for the Scoring microservice.
 * This Spring Boot application provides REST endpoints for managing game scores.
 * It runs independently of the main AsteroidsFX application and communicates via HTTP.
 */
@SpringBootApplication
public class ScoringApplication {

    /**
     * Main method that starts the Spring Boot application.
     * The application will run on port 8080 by default (configured in application.properties).
     *
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(ScoringApplication.class, args);
    }
    
    /**
     * Configure CORS to allow requests from the game client.
     * This is important for local development where the game and service run on different ports.
     */
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("*")
                        .allowedMethods("GET", "POST", "PUT", "DELETE");
            }
        };
    }
}


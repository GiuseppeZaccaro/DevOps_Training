package com.example.auth;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class AppConfig {

    /** CORS aperto: va bene per un esercizio, non per la produzione. */
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/**")
                        .allowedOriginPatterns("*")
                        .allowedMethods("GET", "POST", "OPTIONS")
                        .allowedHeaders("*");
            }
        };
    }

    /** Crea l'utente di prova admin / admin123 al primo avvio. */
    @Bean
    public CommandLineRunner seedUser(AppUserRepository repository) {
        return args -> {
            if (repository.findByUsername("admin").isEmpty()) {
                String hash = new BCryptPasswordEncoder().encode("admin123");
                repository.save(new AppUser("admin", hash));
            }
        };
    }
}

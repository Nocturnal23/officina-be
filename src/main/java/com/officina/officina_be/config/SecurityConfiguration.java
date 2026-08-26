package com.officina.officina_be.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.BeanRegistrarDslMarker;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
public class SecurityConfiguration {
    @Bean
    public PasswordEncoder pswCrypt(){
        /*
        Questo in futuro mi permetterebbe di aggiungere altri tipi
        di encodig.
        */
        Map<String, PasswordEncoder> encoders = new HashMap<>();
        //Questo dice che se l'hash è bcrypt allora usa questo algortmo.
        encoders.put("bcrypt", new BCryptPasswordEncoder()); 
        // Esempio:
        // encoders.put("sha256", new StandardPasswordEncoder());

        /*
        Questo invece dice di creare un nuovo hash usando bcrypt.
        Se dovessi aggiungere un nuovo metodo di encoding allora forse avrebbe
        senso aggiornare anche l'algoritmo col nuovo.
        */
        return new DelegatingPasswordEncoder("bcrypt", encoders); 
    }    

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            //Serve ad abilitare la gestione CORS di Spirng Security.
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))

            .csrf(csrf -> csrf.disable())
            //Serve a gestire una sessione stateless.
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            //Permette le chiamate publiche verso gli endpoint.
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/users/**").permitAll() // Permette login e register
                .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                .anyRequest().authenticated()
            );

            return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:4200"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}

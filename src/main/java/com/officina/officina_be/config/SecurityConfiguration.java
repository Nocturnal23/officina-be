package com.officina.officina_be.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
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

        /*
        Questo invece dice di creare un nuovo hash usando bcrypt.
        Se dovessi aggiungere un nuovo metodo di encoding allora forse avrebbe
        senso aggiornare anche l'algoritmo col nuovo.
        */
        return new DelegatingPasswordEncoder("bcrypt", encoders); 

        /*
        * =====================================================================================
        * GUIDA ALLA MIGRAZIONE DELLE PASSWORD PER IL FUTURO
        * =====================================================================================
        *
        * SCENARIO 1: Conversione di password in chiaro (senza prefisso {id})
        * -------------------------------------------------------------------------------------
        * PROBLEMA:
        * Spring Security cerca le parentesi graffe per determinare l'algoritmo. Se trova una 
        * password come "admin", "cliente0"... non trova il prefisso (id = null) e lancia:
        * "IllegalArgumentException: There is no PasswordEncoder mapped for the id null".
        *
        *
        * COSA AGGIUNGERE PER AGGIORNARE:
        * 1. Importare NoOpPasswordEncoder:
        *    import org.springframework.security.crypto.password.NoOpPasswordEncoder;
        *
        * 2. Modificare il metodo pswCrypt() come segue:
        *    @Bean
        *    public PasswordEncoder pswCrypt() {
        *        LA MAPPA RIMANE INVARIATA.
        *
        *        DelegatingPasswordEncoder delegatingPasswordEncoder = 
        *                new DelegatingPasswordEncoder("bcrypt", encoders);
        *
        *        // Istruisce Spring ad usare NoOpPasswordEncoder se l'id estratto è null
        *        delegatingPasswordEncoder.setDefaultPasswordEncoderForMatches(NoOpPasswordEncoder.getInstance());
        *
        *        return delegatingPasswordEncoder;
        *    }
        *
        * 3. Nel metodo 'loginUser' di UserService.java:
        *    Assicurarsi che ci sia il controllo di migrazione automatica al primo accesso:
        *    if (pswEncoder.upgradeEncoding(user.getPassword())) {
        *        user.setPassword(pswEncoder.encode(request.password()));
        *        userRepo.save(user);
        *    }
        *
        * -------------------------------------------------------------------------------------
        * SCENARIO 2: Aggiunta di un nuovo algoritmo (es. Argon2 al posto di BCrypt)
        * -------------------------------------------------------------------------------------
        * COSA AGGIUNGERE PER AGGIORNARE:
        * 1. Aggiungere il nuovo encoder nella mappa:
        *    encoders.put("argon2", Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8());
        *
        * 2. NON RIMUOVERE BCRYPT DALLA MAPPA.
        * 
        * 3. Impostare argon2 come default per le NUOVE password:
        *    return new DelegatingPasswordEncoder("argon2", encoders);
        * =====================================================================================
     */
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

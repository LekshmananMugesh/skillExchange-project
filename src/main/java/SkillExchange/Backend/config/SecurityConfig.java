package SkillExchange.Backend.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http)


            throws Exception {

        http
            .cors(cors -> cors.configurationSource(request -> {
                    var config = new org.springframework.web.cors
                            .CorsConfiguration();
                    config.setAllowedOrigins(
                            java.util.List.of("http://localhost:3000"));
                    config.setAllowedMethods(
                            java.util.List.of("GET","POST","PUT","DELETE"));
                    config.setAllowedHeaders(
                            java.util.List.of("*"));
                    config.setAllowCredentials(true);
                    return config;
                }))
                // disable csrf
                .csrf(AbstractHttpConfigurer::disable)

                // disable default form login
                .formLogin(AbstractHttpConfigurer::disable)

                // disable basic auth popup
                .httpBasic(AbstractHttpConfigurer::disable)

                // URL rules
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll()
                        .anyRequest().authenticated()
                )

                // no sessions
                .sessionManagement(session -> session
                        .sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS)
                )

                // add jwt filter
                .addFilterBefore(jwtFilter,
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
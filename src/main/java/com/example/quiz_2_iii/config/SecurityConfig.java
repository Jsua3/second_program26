package com.example.quiz_2_iii.config;

import com.example.quiz_2_iii.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeExchange(exchanges -> exchanges
                        // Endpoints públicos
                        .pathMatchers("/auth/**").permitAll()

                        // Endpoints de Pedidos (Solo ADMIN)
                        .pathMatchers("/api/pedidos/**").hasAuthority("ADMIN")

                        // Endpoints de Productos (Separación por método HTTP)
                        .pathMatchers(HttpMethod.GET, "/api/productos/**").hasAnyAuthority("ADMIN", "USER")
                        .pathMatchers(HttpMethod.POST, "/api/productos").hasAuthority("ADMIN")
                        .pathMatchers(HttpMethod.PUT, "/api/productos/**").hasAuthority("ADMIN")
                        .pathMatchers(HttpMethod.DELETE, "/api/productos/**").hasAuthority("ADMIN")

                        // Todas las demás peticiones requieren autenticación
                        .anyExchange().authenticated()
                )
                .addFilterAt(jwtAuthFilter, SecurityWebFiltersOrder.AUTHENTICATION);

        return http.build();
    }
}

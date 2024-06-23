package com.apj.platform.commons.configs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import com.apj.platform.commons.filters.JwtAuthenticationFilter;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@EnableWebSecurity
public class WebSecurityConfiguration {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final String authExcludes;

    public WebSecurityConfiguration(
            JwtAuthenticationFilter jwtAuthenticationFilter,
            @Value(value = "${auth.excludes:/actuator/**}") String authExcludes) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.authExcludes = authExcludes;
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        String[] excludes = this.authExcludes.split(",");
        log.info("excludes from auth {}", authExcludes);
        http.cors(Customizer.withDefaults());
        http.csrf(csrf -> csrf.disable());
        http.exceptionHandling(exceptionHandling -> exceptionHandling.authenticationEntryPoint(
                (request, response, exception) -> {
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, exception.getMessage());
                }));
        http.sessionManagement(sessionManager -> sessionManager.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        http.authorizeHttpRequests(
                request -> request.requestMatchers(excludes).permitAll().anyRequest()
                        .authenticated());
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        // config.setAllowedOriginPatterns(yamlConfig.getCorsAllowedList());
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}

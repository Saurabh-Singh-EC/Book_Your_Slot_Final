package com.codeWithSrb.BookYourSlot.config;


import com.codeWithSrb.BookYourSlot.Enumeration.RolePermission;
import com.codeWithSrb.BookYourSlot.Service.UserDetailsServiceImpl;
import com.codeWithSrb.BookYourSlot.filter.CustomizeAuthorizationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfiguration {

    CustomizeAuthorizationFilter customizeAuthorizationFilter;
    PasswordEncoder passwordEncoder;
    AccessDeniedHandlerImpl accessDeniedHandler;
    AuthenticationEntryPointImpl authenticationEntryPoint;

    public SecurityConfiguration(CustomizeAuthorizationFilter customizeAuthorizationFilter,
                                 PasswordEncoder passwordEncoder,
                                 AccessDeniedHandlerImpl accessDeniedHandler,
                                 AuthenticationEntryPointImpl authenticationEntryPoint) {
        this.customizeAuthorizationFilter = customizeAuthorizationFilter;
        this.passwordEncoder = passwordEncoder;
        this.accessDeniedHandler = accessDeniedHandler;
        this.authenticationEntryPoint = authenticationEntryPoint;
    }

    @Bean
    UserDetailsService userDetailsService() {
        return new UserDetailsServiceImpl();
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity security) throws Exception {
        return security.csrf(AbstractHttpConfigurer::disable)
                .cors(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(configurer -> configurer
                        .requestMatchers("/api/v1/booking/register",
                                "/api/v1/booking/login",
                                "/api/v1/booking/profile",
                                "/api/v1/booking/refresh/token",
                                "/api/v1/booking/*",
                                "/api/v1/booking/book").permitAll()
                        .requestMatchers("/api/v1/booking").hasAuthority(RolePermission.USER.name())
                        .requestMatchers("/api/v1/booking/**").hasAnyAuthority(RolePermission.ADMIN.name(), RolePermission.USER.name())
                        .anyRequest()
                        .authenticated())
                .exceptionHandling(customizer -> customizer.accessDeniedHandler(accessDeniedHandler).authenticationEntryPoint(authenticationEntryPoint))
                .sessionManagement(config -> config.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(customizeAuthorizationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService(userDetailsService());
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder);
        return daoAuthenticationProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

}
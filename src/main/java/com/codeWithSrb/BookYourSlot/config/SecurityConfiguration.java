package com.codeWithSrb.BookYourSlot.config;


import com.codeWithSrb.BookYourSlot.Service.UserDetailsServiceImpl;
import com.codeWithSrb.BookYourSlot.filter.CustomizeAuthorizationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    private final CustomizeAuthorizationFilter customizeAuthorizationFilter;
    private final PasswordEncoder passwordEncoder;
    private final AccessDeniedHandlerImpl accessDeniedHandler;
    private final AuthenticationEntryPointImpl authenticationEntryPoint;
    private final UserDetailsServiceImpl userDetailsService;

    public SecurityConfiguration(CustomizeAuthorizationFilter customizeAuthorizationFilter,
                                 PasswordEncoder passwordEncoder,
                                 AccessDeniedHandlerImpl accessDeniedHandler,
                                 AuthenticationEntryPointImpl authenticationEntryPoint, UserDetailsServiceImpl userDetailsService) {
        this.customizeAuthorizationFilter = customizeAuthorizationFilter;
        this.passwordEncoder = passwordEncoder;
        this.accessDeniedHandler = accessDeniedHandler;
        this.authenticationEntryPoint = authenticationEntryPoint;
        this.userDetailsService = userDetailsService;
    }

    @Bean
    UserDetailsService userDetailsService() {
        return userDetailsService;
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity security) throws Exception {
        return security.csrf(AbstractHttpConfigurer::disable)
                .cors(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(configurer -> configurer
                        .requestMatchers("/api/v1/booking/register").permitAll()
                        .requestMatchers("/api/v1/booking/login").permitAll()
                        .requestMatchers("/api/v1/booking/password-reset").permitAll()
                        .requestMatchers("/api/v1/booking/verify/password/**").permitAll()
                        .requestMatchers("api/v1/booking/password-reset/key").permitAll()

                        .requestMatchers("/api/v1/booking/profile").hasAnyRole("USER","ADMIN")
                        .requestMatchers("/api/v1/booking/profile").hasAnyAuthority("USER:READ","ADMIN:READ")

                        .requestMatchers("/api/v1/booking/profile/delete/**").hasAnyRole("USER","ADMIN")
                        .requestMatchers("/api/v1/booking/profile/delete/**").hasAnyAuthority("USER:DELETE","ADMIN:DELETE")

                        .requestMatchers("/api/v1/booking/user/reset-password").hasAnyRole("USER","ADMIN")
                        .requestMatchers("/api/v1/booking/user/reset-password").hasAnyAuthority("USER:WRITE","ADMIN:WRITE")

                        .requestMatchers("/api/v1/booking/slots/**").hasAnyRole("USER","ADMIN")
                        .requestMatchers("/api/v1/booking/slots/**").hasAnyAuthority("USER:READ","ADMIN:READ")

                        .requestMatchers("/api/v1/booking/book").hasAnyRole("USER","ADMIN")
                        .requestMatchers("/api/v1/booking/book").hasAnyAuthority("USER:CREATE","ADMIN:CREATE")

                        .anyRequest()
                        .authenticated())

                .exceptionHandling(customizer -> customizer.accessDeniedHandler(accessDeniedHandler).authenticationEntryPoint(authenticationEntryPoint))
                .sessionManagement(config -> config.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(customizeAuthorizationFilter, UsernamePasswordAuthenticationFilter.class)
                .authenticationProvider(authenticationProvider())
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
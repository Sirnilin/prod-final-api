package com.example.prodolymp.configurations;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import tech.ailef.snapadmin.external.SnapAdminProperties;

import java.util.List;


@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final SnapAdminProperties properties;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /*@Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }*/

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        /*String baseUrl = properties.getBaseUrl();
        http.csrf().disable()
                .authorizeRequests((requests) -> requests

                        .mvcMatchers("/api.html").permitAll()
                        .mvcMatchers("/api/**").permitAll()
                        .mvcMatchers("/swagger-ui/index.html").permitAll()
                        .mvcMatchers(baseUrl).hasAnyRole("ROLE_ADMIN")
                        .anyRequest().permitAll()
                )
                .formLogin((form) -> form
                        .loginPage("/login")
                        .failureUrl("/login?error=true")
                        .permitAll()
                        .failureHandler(authenticationFailureHandler())
                )
                .logout(LogoutConfigurer::permitAll)
        ;

        return http.build();*/

        return http
                .headers(headersConfigurer -> headersConfigurer.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin))
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/api/**").permitAll()
                        .requestMatchers("/admin").authenticated()
                        .anyRequest().permitAll())
                .build();
    }



    @Bean
    public AuthenticationFailureHandler authenticationFailureHandler() {
        return (request, response, exception) -> {
            System.out.println("Login failed with: " + exception.getMessage());
            response.sendRedirect("/login?error=true");
        };
    }

    @Bean
    public UrlBasedCorsConfigurationSource corsConfigurationSource() {
        List<String> corsAllList = List.of(CorsConfiguration.ALL);
        var corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowedOriginPatterns(corsAllList);
        corsConfiguration.setAllowedMethods(corsAllList);
        corsConfiguration.setAllowCredentials(true);
        corsConfiguration.setAllowedHeaders(corsAllList);
        corsConfiguration.setExposedHeaders(corsAllList);
        var corsConfigurationSource = new UrlBasedCorsConfigurationSource();
        corsConfigurationSource.registerCorsConfiguration("/**", corsConfiguration);
        return corsConfigurationSource;
    }

    /*@Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return (request, response, authException) -> response.sendRedirect("/login");
    }

    @Bean
    public UserDetailsService userDetailsService() {
        UserModel user = new UserModel();
        user.setLogin("user");
        user.setEmail("user@example.com");
        user.setPassword(passwordEncoder().encode("password"));
        user.setRoles(Collections.singleton(Role.ROLE_USER));

        return new InMemoryUserDetailsManager(user);
    }*/
}

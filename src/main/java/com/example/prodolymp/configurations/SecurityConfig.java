    package com.example.prodolymp.configurations;

    import java.util.List;
    import org.springframework.context.annotation.Bean;
    import org.springframework.context.annotation.Configuration;
    import org.springframework.security.config.annotation.web.builders.HttpSecurity;
    import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
    import org.springframework.security.config.annotation.web.configuration.WebSecurityConfiguration;
    import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
    import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
    import org.springframework.security.crypto.password.PasswordEncoder;
    import org.springframework.security.web.SecurityFilterChain;



    @Configuration
    @EnableWebSecurity
    public class SecurityConfig {

        @Bean
        public PasswordEncoder passwordEncoder() {
            return new BCryptPasswordEncoder();
        }

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
            http.csrf().disable()
                    .authorizeRequests((requests) -> requests
                            .anyRequest().permitAll()
                    )
                    .formLogin((form) -> form
                            .loginPage("/login").permitAll()
                    )
                    .logout(LogoutConfigurer::permitAll)
            ;

            return http.build();
        }
    }

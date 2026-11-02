package com.senac.controle_obra;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
                .formLogin(withDefaults())
                .httpBasic(withDefaults());

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        // Criando múltiplos usuários com senhas e acessos isolados
        UserDetails admin = User.withDefaultPasswordEncoder()
                .username("admin")
                .password("12072023")
                .roles("USER")
                .build();

        UserDetails tiago = User.withDefaultPasswordEncoder()
                .username("tiago")
                .password("1312")
                .roles("USER")
                .build();

        UserDetails engenheiro = User.withDefaultPasswordEncoder()
                .username("lucas")
                .password("9876")
                .roles("USER")
                .build();
        UserDetails Jesiel = User.withDefaultPasswordEncoder()
                .username("jesiel")
                .password("4321")
                .roles("USER")
                .build();

        return new InMemoryUserDetailsManager(admin, tiago, engenheiro);
    }
}
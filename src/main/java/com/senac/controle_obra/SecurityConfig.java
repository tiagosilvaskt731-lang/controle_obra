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

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // DESATIVA A VALIDAÇÃO DO CSRF PARA LIBERAR OS FORMULÁRIOS POST
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(authorize -> authorize
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .defaultSuccessUrl("/", true)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                );

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails admin = User.withDefaultPasswordEncoder()
                .username("admin")
                .password("1234")
                .roles("USER")
                .build();

        UserDetails tiago = User.withDefaultPasswordEncoder()
                .username("tiago")
                .password("1213")
                .roles("USER")
                .build();

        UserDetails lucas = User.withDefaultPasswordEncoder()
                .username("Lucas")
                .password("2405")
                .roles("USER")
                .build();

        UserDetails jesiel = User.withDefaultPasswordEncoder()
                .username("jesiel")
                .password("1512")
                .roles("USER")
                .build();

        return new InMemoryUserDetailsManager(admin, tiago, lucas, jesiel);
    }
}
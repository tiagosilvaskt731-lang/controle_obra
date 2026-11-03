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
                .authorizeHttpRequests(authorize -> authorize
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .defaultSuccessUrl("/", true) // Força ir para a rota limpa inicial após o login
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .invalidateHttpSession(true) // DESTRÓI a sessão antiga na memória do servidor
                        .clearAuthentication(true)   // LIMPA as credenciais do Spring Security
                        .deleteCookies("JSESSIONID") // APAGA o cookie de sessão no navegador do usuário
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
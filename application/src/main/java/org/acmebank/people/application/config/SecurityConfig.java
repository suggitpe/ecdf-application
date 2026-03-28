package org.acmebank.people.application.config;

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
            .authorizeHttpRequests(requests -> requests
                .requestMatchers("/css/**", "/js/**", "/images/**").permitAll()
                .requestMatchers("/login").permitAll()
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/", true)
                .permitAll()
            )
            .logout(logout -> logout
                .logoutSuccessUrl("/login?logout")
                .permitAll()
            );

        return http.build();
    }

    // Basic setup for testing
    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails user = User.withDefaultPasswordEncoder()
            .username("user@acmebank.org")
            .password("password")
            .roles("USER")
            .build();
            
        UserDetails manager = User.withDefaultPasswordEncoder()
            .username("manager@acmebank.org")
            .password("password")
            .roles("MANAGER")
            .build();

        UserDetails charlie = User.withDefaultPasswordEncoder()
            .username("charlie@acmebank.org")
            .password("password")
            .roles("USER")
            .build();

        UserDetails bob = User.withDefaultPasswordEncoder()
            .username("bob@acmebank.org")
            .password("password")
            .roles("USER")
            .build();

        UserDetails ita = User.withDefaultPasswordEncoder()
            .username("ita@acmebank.org")
            .password("password")
            .roles("ITA")
            .build();

        UserDetails alice = User.withDefaultPasswordEncoder()
            .username("alice@acmebank.org")
            .password("password")
            .roles("ITA")
            .build();
        
        UserDetails arthur = User.withDefaultPasswordEncoder()
                .username("arthur@acmebank.org")
                .password("password")
                .roles("USER")
                .build();

        UserDetails admin = User.withDefaultPasswordEncoder()
                .username("admin@acmebank.org")
                .password("password")
                .roles("ADMIN", "PROMOTION_COORDINATOR")
                .build();

        UserDetails coordinator = User.withDefaultPasswordEncoder()
                .username("coordinator@acmebank.org")
                .password("password")
                .roles("PROMOTION_COORDINATOR")
                .build();

        return new InMemoryUserDetailsManager(user, manager, charlie, bob, ita, alice, arthur, admin, coordinator);
    }
}

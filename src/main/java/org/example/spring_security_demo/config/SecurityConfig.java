package org.example.spring_security_demo.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@RequiredArgsConstructor
@Configuration
@EnableMethodSecurity(securedEnabled = true, jsr250Enabled = true, prePostEnabled = true)
@EnableWebSecurity
public class SecurityConfig {
  private final CustomAccessDenied customAccessDenied;
  private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http, JwtAuthFilter jwtAuthFilter)
      throws Exception {
    http.csrf(Customizer.withDefaults()) // Enable CSRF protection with default settings
        .cors(Customizer.withDefaults())
        // disable csrf filter
        .csrf(AbstractHttpConfigurer::disable)
        // disable logout filter
        .logout(AbstractHttpConfigurer::disable)
        // disable default login page
        .formLogin(AbstractHttpConfigurer::disable)
        // disable request cache aware filter
        .requestCache(AbstractHttpConfigurer::disable)
        .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
        .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(
            request ->
                request.requestMatchers("/auth/login").permitAll().anyRequest().authenticated())
        .exceptionHandling(
            ex ->
                ex.accessDeniedHandler(customAccessDenied)
                    .authenticationEntryPoint(customAuthenticationEntryPoint));

    return http.build();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    // Always encode passwords, even for in-memory users
    return new BCryptPasswordEncoder();
  }

//  @Bean
//  public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
//    // Define a regular user
//    UserDetails user =
//        User.builder()
//            .username("user")
//            .password(passwordEncoder.encode("password123"))
//            .roles("USER")
//            .build();
//
//    // Define an admin user
//    UserDetails admin =
//        User.builder()
//            .username("admin")
//            .password(passwordEncoder.encode("admin123"))
//            .roles("SUPER_ADMIN", "ADMIN", "MANAGER")
//            .build();
//
//    // Return the in-memory manager populated with your users
//    return new InMemoryUserDetailsManager(user, admin);
//  }

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
      throws Exception {
    return config.getAuthenticationManager();
  }
}

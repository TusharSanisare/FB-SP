package com.example.jwt.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
// import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
// import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
// import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity // required for @PreAuthorize to work on controllers
public class SecurityConfig {

  @Autowired
  private JwtAuthenticationFilter jwtAuthFilter;

  @Autowired
  private UserDetailsService userDetailsService;

  @Value("${app.h2console.enabled:false}")
  private boolean h2ConsoleEnabled;

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        // CSRF is safe to disable ONLY because this is a stateless, token-authenticated
        // API (no cookies/sessions used for auth). If you ever add cookie-based auth,
        // re-enable CSRF protection.
        .csrf(csrf -> csrf.disable())
        .cors(cors -> cors.configurationSource(corsConfigurationSource()))
        .authorizeHttpRequests(auth -> auth
            // --- Public, unauthenticated ---
            .requestMatchers(new AntPathRequestMatcher("/h2-console/**")).permitAll()
            .requestMatchers(new AntPathRequestMatcher("/api/auth/login", "POST")).permitAll()
            .requestMatchers(new AntPathRequestMatcher("/api/auth/register", "POST")).permitAll()
            .requestMatchers(new AntPathRequestMatcher("/api/flights/**", "GET")).permitAll() // browsing flights is

            // --- Admin account creation now requires an existing ADMIN's token ---
            // This was previously permitAll() -- anyone could self-register as ADMIN.
            .requestMatchers(new AntPathRequestMatcher("/api/auth/register-admin", "POST")).hasRole("ADMIN")

            // --- Flight mutation requires ADMIN ---
            .requestMatchers(new AntPathRequestMatcher("/api/flights/**", "POST")).hasRole("ADMIN")
            .requestMatchers(new AntPathRequestMatcher("/api/flights/**", "PUT")).hasRole("ADMIN")
            .requestMatchers(new AntPathRequestMatcher("/api/flights/**", "DELETE")).hasRole("ADMIN")

            // --- Bookings require an authenticated user (not anonymous) ---
            .requestMatchers(new AntPathRequestMatcher("/api/bookings/**")).hasAnyRole("USER", "ADMIN")

            // --- Admin/user demo namespaces ---
            .requestMatchers(new AntPathRequestMatcher("/api/admin/**")).hasRole("ADMIN")
            .requestMatchers(new AntPathRequestMatcher("/api/user/**")).hasAnyRole("USER", "ADMIN")

            // --- H2 console: dev-only, never expose in production ---
            // .requestMatchers(new AntPathRequestMatcher("/h2-console/**"))
            // .access((authentication, context) -> new
            // AuthorizationDecision(h2ConsoleEnabled))

            .anyRequest().authenticated())
        .sessionManagement(session -> session
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authenticationProvider(authenticationProvider())
        .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
        // Only relax frame options when the H2 console is actually enabled (dev
        // profile).
        .headers(headers -> {
          if (h2ConsoleEnabled) {
            headers.frameOptions(frameOptions -> frameOptions.sameOrigin());
          }
        });

    return http.build();
  }

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    // Replace with your actual frontend origin(s) -- do not use "*" once
    // credentials/tokens are involved.
    configuration.setAllowedOrigins(List.of("http://localhost:4200"));
    configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
    configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));
    configuration.setExposedHeaders(List.of("Authorization"));
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
  }

  @Bean
  public AuthenticationProvider authenticationProvider() {
    DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
    authProvider.setUserDetailsService(userDetailsService);
    authProvider.setPasswordEncoder(passwordEncoder());
    return authProvider;
  }

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
      throws Exception {
    return config.getAuthenticationManager();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    // strength 12 instead of the BCrypt default (10) for extra margin
    return new BCryptPasswordEncoder(12);
  }
}

// @Configuration
// @EnableWebSecurity
// public class SecurityConfig {
// @Autowired
// private JwtAuthenticationFilter jwtAuthFilter;

// @Autowired
// private UserDetailsService userDetailsService;

// @Bean
// public SecurityFilterChain securityFilterChain(HttpSecurity http) throws
// Exception {
// http
// .csrf(csrf -> csrf.disable())
// .authorizeHttpRequests(auth -> auth
// .requestMatchers(new AntPathRequestMatcher("/api/flights/**")).permitAll()
// .requestMatchers(new AntPathRequestMatcher("/api/bookings/**")).permitAll()
// .requestMatchers(new AntPathRequestMatcher("/api/auth/**")).permitAll()
// .requestMatchers(new AntPathRequestMatcher("/h2-console/**")).permitAll()
// .requestMatchers(new AntPathRequestMatcher("/api/admin/**")).hasRole("ADMIN")
// .requestMatchers(new
// AntPathRequestMatcher("/api/user/**")).hasAnyRole("USER", "ADMIN")
// .anyRequest().authenticated())
// .sessionManagement(session -> session
// .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
// .authenticationProvider(authenticationProvider())
// .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
// .headers(headers -> headers.frameOptions(frameOptions ->
// frameOptions.disable()));

// return http.build();
// }

// @Bean
// public AuthenticationProvider authenticationProvider() {
// DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
// authProvider.setUserDetailsService(userDetailsService);
// authProvider.setPasswordEncoder(passwordEncoder());
// return authProvider;
// }

// @Bean
// public AuthenticationManager
// authenticationManager(AuthenticationConfiguration config)
// throws Exception {
// return config.getAuthenticationManager();
// }

// @Bean
// public PasswordEncoder passwordEncoder() {
// return new BCryptPasswordEncoder();
// }
// }
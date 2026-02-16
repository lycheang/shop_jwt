package com.dreamshops.security.config;

import com.dreamshops.security.jwt.AuthTokenFilter;
import com.dreamshops.security.jwt.JwtAuthEntryPoint;
import com.dreamshops.security.user.ShopUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@RequiredArgsConstructor
@EnableWebSecurity
@Configuration
@EnableMethodSecurity(prePostEnabled = true)
public class ShopConfig {
    private final ShopUserDetailsService userDetailsService;
    private final JwtAuthEntryPoint jwtAuthEntryPoint;

    private static final List<String> PUBLIC_URLS = List.of(
            "/api/v1/auth/**",
            "/api/v1/products/all",
            "/api/v1/products/{id}",
            "/api/v1/products/category/**",
            "/api/v1/products/brand/**",
            "/api/v1/categories/all",
            "/api/v1/categories/{id}",
            "/api/v1/categories/category/{name}",
            "/api/v1/images/download/**",
            "/api/v1/users/register",
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/swagger-ui.html"
    );

    // 2. USER URLs: Requires a valid JWT token (ROLE_USER or ROLE_ADMIN)
    private static final List<String> USER_URLS = List.of(
            "/api/v1/carts/**",
            "/api/v1/carts-item/**",
            "/api/v1/orders/**" // Assuming users can place/view orders
    );

    // 3. ADMIN URLs: Strictly for Admins (Can also be handled by @PreAuthorize in Controllers)
    private static final List<String> ADMIN_URLS = List.of(
            "/api/v1/users/**", // User management
            "/api/v1/products/add",
            "/api/v1/products/update/**",
            "/api/v1/products/delete/**",
            "/api/v1/products/*/status",
            "/api/v1/categories/add",
            "/api/v1/categories/{id}",  // PUT & DELETE
            "/api/v1/images/upload",
            "/api/v1/images/update/**",
            "/api/v1/images/delete/**"
    );
    @Bean
    public ModelMapper modelMapper(){
        return new ModelMapper();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthTokenFilter authTokenFilter(){
        return new AuthTokenFilter();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider(){
        var authProvider= new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .exceptionHandling(auth -> auth.authenticationEntryPoint(jwtAuthEntryPoint))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // A. Allow Public URLs
                        .requestMatchers(PUBLIC_URLS.toArray(String[]::new)).permitAll()

                        // B. Secure Admin URLs (Defense in depth, even if Controller has @PreAuthorize)
                        .requestMatchers(ADMIN_URLS.toArray(String[]::new)).hasRole("ADMIN")

                        // C. Secure User URLs (Any authenticated user)
                        .requestMatchers(USER_URLS.toArray(String[]::new)).authenticated()

                        // D. Lock down everything else by default (Best Practice)
                        .anyRequest().authenticated()
                );

        http.authenticationProvider(daoAuthenticationProvider());
        http.addFilterBefore(authTokenFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // Allow your React Frontend URL
        configuration.setAllowedOrigins(List.of("http://localhost:3000", "http://localhost:5173"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

}

package com.eCommerce.Ecommerce.Config;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.eCommerce.Ecommerce.JWTSecurity.JwtAuthenticationEntryPoint;
import com.eCommerce.Ecommerce.JWTSecurity.JwtAuthenticationFilter;
import com.eCommerce.Ecommerce.Services.SecurityCustomUserDetailsService;

@Configuration
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationEntryPoint point;

    @Autowired
    private JwtAuthenticationFilter filter;

    @Autowired
    private SecurityCustomUserDetailsService customUserDetailsService;

    @Autowired
    private OAuthenticationSuccessHandler handler;

    @Autowired
    private AuthFailureHandler authFailureHandler;

    // Security Filter Chain
    @Bean
    public SecurityFilterChain formLoginFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrfConfig -> csrfConfig.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/login", "/register", "/verify-otp",
                                "/sellers/verify-seller-otp",
                                "/sellers/register", "/sellers/verify-otp",
                                "/otp", "/resend-otp", "/oauth2/**", "/css/**", "/js/**",
                                "/images/**", "/products/**", "/api/seed/tribal")
                        .permitAll()
                        .requestMatchers("/user/**", "/user/cart/**").authenticated()
                        .anyRequest().authenticated())
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/authenticate")
                        .defaultSuccessUrl("/user/main", true)
                        .failureHandler(authFailureHandler)
                        .usernameParameter("email")
                        .passwordParameter("password")
                        .permitAll())
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout=true"))
                .oauth2Login(oauth -> oauth
                        .loginPage("/login")
                        .successHandler(handler)
                        .failureHandler((request, response, exception) -> {
                            // Forward the request to the form-based failure handler
                            authFailureHandler.onAuthenticationFailure(request, response, exception);
                        })

                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED));

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration builder) throws Exception {
        return builder.getAuthenticationManager();
    }
}

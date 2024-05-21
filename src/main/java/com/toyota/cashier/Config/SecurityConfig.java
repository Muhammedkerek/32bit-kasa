package com.toyota.cashier.Config;


import com.toyota.cashier.Services.RolesDetailsServiceImp;
import com.toyota.cashier.filter.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private RolesDetailsServiceImp rolesDetailsServiceImp;
    private JwtAuthenticationFilter jwtAuthenticationFilter;
    private CustomLogOutHandler logOutHandler;
    public SecurityConfig(RolesDetailsServiceImp rolesDetailsServiceImp, JwtAuthenticationFilter jwtAuthenticationFilter , CustomLogOutHandler logOutHandler) {
        this.rolesDetailsServiceImp = rolesDetailsServiceImp;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.logOutHandler = logOutHandler;
    }
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception
    {
        return
                http.csrf(AbstractHttpConfigurer::disable)
                        .authorizeHttpRequests(
                        req-> req.requestMatchers("/login/**" , "register/**" , "/products")
                                .permitAll()
                                .requestMatchers("/admin_only/**").hasAuthority("ADMIN")
                                .requestMatchers("/add_product").hasAnyAuthority("ADMIN" , "STORE_MANAGER")
                                .anyRequest()
                                .authenticated()
                        ).userDetailsService(rolesDetailsServiceImp)
                        .sessionManagement(session-> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                        .addFilterBefore(jwtAuthenticationFilter , UsernamePasswordAuthenticationFilter.class)
                        .logout(l-> l.logoutUrl("/logout").addLogoutHandler(logOutHandler).logoutSuccessHandler((
                                (request, response, authentication) -> SecurityContextHolder.clearContext()
                                )))
                        .build();

    }
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

}

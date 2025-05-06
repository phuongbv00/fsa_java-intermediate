package lecture.spring.web.config;

import lecture.spring.web.security.JwtAuthenticationConvertor;
import lecture.spring.web.security.JwtAuthenticationProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        Filter jwtAuthenticationFilter = new JwtAuthenticationFilter();
        AuthenticationFilter jwtAuthenticationFilter = new AuthenticationFilter(new ProviderManager(new JwtAuthenticationProvider()), new JwtAuthenticationConvertor());
        jwtAuthenticationFilter.setSuccessHandler((request, response, authentication) -> {
        });
        return http
                .csrf(CsrfConfigurer::disable)
                .httpBasic(Customizer.withDefaults())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/login/jwt").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/products").hasAnyAuthority("ADMIN", "ROOT")
                        .anyRequest().authenticated())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(e -> e
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setContentType(MediaType.APPLICATION_JSON.toString());
                            response.setStatus(HttpStatus.UNAUTHORIZED.value());
                            response.getWriter().write(authException.getMessage());
                            response.getWriter().flush();
                            response.getWriter().close();
                        })
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.setContentType(MediaType.APPLICATION_JSON.toString());
                            response.setStatus(HttpStatus.FORBIDDEN.value());
                            response.getWriter().write(accessDeniedException.getMessage());
                            response.getWriter().flush();
                            response.getWriter().close();
                        }))
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

package com.roberto.interview.configuration;

import static org.springframework.security.config.Customizer.withDefaults;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.web.BearerTokenAuthenticationEntryPoint;
import org.springframework.security.oauth2.server.resource.web.access.BearerTokenAccessDeniedHandler;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;

import com.roberto.interview.filter.SpaWebFilter;
import com.roberto.interview.security.AuthoritiesConstants;

@Configuration
@EnableMethodSecurity(securedEnabled = true)
public class SecurityConfiguration {

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) {
    http
      .cors(withDefaults())
      .csrf(AbstractHttpConfigurer::disable)
      .anonymous(AbstractHttpConfigurer::disable)
      .addFilterAfter(new SpaWebFilter(), BasicAuthenticationFilter.class)
      .headers(headers ->
        headers
          .frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin)
          .referrerPolicy(referrer -> referrer.policy(ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN))
          .permissionsPolicyHeader(permissions ->
            permissions.policy(
              "camera=(), fullscreen=(self), geolocation=(), gyroscope=(), magnetometer=(), microphone=(), midi=(), payment=(), sync-xhr=()"
            )
          )
      )
      .authorizeHttpRequests(authz ->
        authz
          .requestMatchers("/index.html", "/*.js", "/*.txt", "/*.json", "/*.map", "/*.css").permitAll()
          .requestMatchers("/*.ico", "/*.png", "/*.svg", "/*.webapp").permitAll()
          .requestMatchers("/app/**").permitAll()
          .requestMatchers("/content/**").permitAll()
          .requestMatchers(HttpMethod.GET, "/api/auth").permitAll()
          .requestMatchers(HttpMethod.POST, "/api/auth").permitAll()
          .requestMatchers(HttpMethod.POST, "/api/auth/refresh").permitAll()
          .requestMatchers(HttpMethod.POST, "/api/auth/logout").permitAll()
          .requestMatchers("/api/users").hasAuthority(AuthoritiesConstants.ADMIN)
          .requestMatchers("/api/**").hasAuthority(AuthoritiesConstants.USER)
          .requestMatchers("/management/health").permitAll()
          .requestMatchers("/management/health/**").permitAll()
          .requestMatchers("/management/info").permitAll()
          .requestMatchers("/management/prometheus").permitAll()
      )
      .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
      .exceptionHandling(exceptions ->
        exceptions
          .authenticationEntryPoint(new BearerTokenAuthenticationEntryPoint())
          .accessDeniedHandler(new BearerTokenAccessDeniedHandler())
      )
      .oauth2ResourceServer(oauth2 -> oauth2.jwt(withDefaults()));
    return http.build();
  }

}

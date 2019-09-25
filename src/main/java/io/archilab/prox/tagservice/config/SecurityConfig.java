package io.archilab.prox.tagservice.config;

import org.keycloak.adapters.KeycloakConfigResolver;
import org.keycloak.adapters.springboot.KeycloakSpringBootConfigResolver;
import org.keycloak.adapters.springsecurity.KeycloakConfiguration;
import org.keycloak.adapters.springsecurity.authentication.KeycloakAuthenticationProvider;
import org.keycloak.adapters.springsecurity.config.KeycloakWebSecurityConfigurerAdapter;
import org.keycloak.adapters.springsecurity.management.HttpSessionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.authority.mapping.SimpleAuthorityMapper;
import org.springframework.security.web.authentication.session.NullAuthenticatedSessionStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;

@Profile("!disabled-security")
@KeycloakConfiguration
class SecurityConfig extends KeycloakWebSecurityConfigurerAdapter {

  @Autowired
  public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
    KeycloakAuthenticationProvider keycloakAuthenticationProvider =
        this.keycloakAuthenticationProvider();
    keycloakAuthenticationProvider.setGrantedAuthoritiesMapper(new SimpleAuthorityMapper());
    auth.authenticationProvider(keycloakAuthenticationProvider);
  }

  @Bean
  @Override
  @ConditionalOnMissingBean(HttpSessionManager.class)
  protected HttpSessionManager httpSessionManager() {
    return new HttpSessionManager();
  }

  @Bean
  @Override
  protected SessionAuthenticationStrategy sessionAuthenticationStrategy() {
    return new NullAuthenticatedSessionStrategy();
  }

  @Bean
  public KeycloakConfigResolver KeycloakConfigResolver() {
    return new KeycloakSpringBootConfigResolver();
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    super.configure(http);
    http.csrf().disable().authorizeRequests()
    .antMatchers(HttpMethod.GET, "/tags/**").permitAll()
    .antMatchers(HttpMethod.HEAD, "/tags/**").permitAll()
    .antMatchers(HttpMethod.OPTIONS, "/tags/**").permitAll()
    .antMatchers("/tags/**").hasRole("professor")
    
    .antMatchers(HttpMethod.GET, "/tagCollections/**").permitAll()
    .antMatchers(HttpMethod.HEAD, "/tagCollections/**").permitAll()
    .antMatchers(HttpMethod.OPTIONS, "/tagCollections/**").permitAll()
    .antMatchers(HttpMethod.POST, "/tagCollections/**/tags").hasRole("professor")
    .antMatchers(HttpMethod.POST, "/tagCollections/**").denyAll()
    .antMatchers(HttpMethod.DELETE, "/tagCollections/**").denyAll()
    .antMatchers("/tagCollections/**").hasRole("professor")
    
    .antMatchers(HttpMethod.GET, "/tagRecommendations/**").permitAll()
    
    .anyRequest().denyAll();
  }
}

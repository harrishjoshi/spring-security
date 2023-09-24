package com.harrish.devoxxbe;

import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationEventPublisher;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http, AuthenticationEventPublisher publisher
    ) throws Exception {
        // Configure the authentication event publisher for the AuthenticationManagerBuilder
        {
            http.getSharedObject(AuthenticationManagerBuilder.class)
                    .authenticationEventPublisher(publisher);
        }

        var configurer = new RobotLoginConfigurer()
                .password("secret")
                .password("secret1");

        return http
                .authorizeRequests(authorizeConfig -> {
                    authorizeConfig.antMatchers("/").permitAll();
                    authorizeConfig.antMatchers("/error").permitAll();
                    authorizeConfig.antMatchers("/favicon.io").permitAll();
                    authorizeConfig.anyRequest().authenticated();
                })
                .httpBasic(Customizer.withDefaults())
                .formLogin(Customizer.withDefaults())
                .oauth2Login(
                        oauthConfigurer -> {
                            oauthConfigurer.withObjectPostProcessor(
                                    new ObjectPostProcessor<AuthenticationProvider>() {
                                        @Override
                                        public <O extends AuthenticationProvider> O postProcess(O object) {
                                            return (O) new RateLimitedAuthenticationProvider(object);
                                        }
                                    }
                            );
                        }
                )
                .apply(configurer).and()
                .authenticationProvider(new CustomAuthenticationProvider())
                .build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return new InMemoryUserDetailsManager(
                User.builder()
                        .username("user")
                        .password("{noop}password")
                        .authorities("ROLE_USER")
                        .build()
        );
    }

    @Bean
    public ApplicationListener<AuthenticationSuccessEvent> successListener() {
        return event -> System.out.printf(
                "🥳 SUCCESS [%s] %s\n",
                event.getAuthentication().getClass().getSimpleName(),
                event.getAuthentication().getName()
        );
    }
}
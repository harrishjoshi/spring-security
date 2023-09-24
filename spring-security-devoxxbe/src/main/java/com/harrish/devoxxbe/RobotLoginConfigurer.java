package com.harrish.devoxxbe;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;

import java.util.ArrayList;
import java.util.List;

public class RobotLoginConfigurer extends AbstractHttpConfigurer<RobotLoginConfigurer, HttpSecurity> {

    public final List<String> passwords = new ArrayList<>();

    @Override
    public void init(HttpSecurity http) {
        http.authenticationProvider(new RobotAuthenticationProvider(passwords));
    }

    @Override
    public void configure(HttpSecurity http) {
        var authenticationManager = http.getSharedObject(AuthenticationManager.class);
        http.addFilterBefore(new RobotFilter(authenticationManager), FilterSecurityInterceptor.class);
    }

    public RobotLoginConfigurer password(String password) {
        this.passwords.add(password);
        return this;
    }
}

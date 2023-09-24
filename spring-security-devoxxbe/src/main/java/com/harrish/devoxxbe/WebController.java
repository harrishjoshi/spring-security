package com.harrish.devoxxbe;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
public class WebController {

    @GetMapping("/")
    public String publicPage() {
        return "Hello Devoxx!";
    }

    @GetMapping("/private")
    public String privatePage(Authentication authentication) throws InterruptedException {
//        var authentication = SecurityContextHolder.getContext().getAuthentication();
//        System.out.println("OUTSIDE THREAD -> " + authentication);
//
//        var thread = new Thread(() -> {
//            var inThread = SecurityContextHolder.getContext().getAuthentication();
//            System.out.println("IN THREAD -> " + inThread);
//        });
//        thread.start();
//        thread.join();

        return "Welcome to the VIP room ~[ " + getName(authentication) + " ]~ 😀";
    }

    private static String getName(Authentication authentication) {
        return Optional.of(authentication.getPrincipal())
                .filter(OidcUser.class::isInstance)
                .map(OidcUser.class::cast)
                .map(OidcUser::getEmail)
                .orElse(authentication.getName());
    }
}

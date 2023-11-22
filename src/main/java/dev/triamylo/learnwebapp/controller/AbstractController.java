package dev.triamylo.learnwebapp.controller;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.security.Principal;
import java.util.Collection;

public abstract class AbstractController {


    // check if the login user is Admin
    protected boolean hasAdminRole(Principal principal) {

        if (principal instanceof UsernamePasswordAuthenticationToken user) {

            Collection<GrantedAuthority> authorities = user.getAuthorities();

            for (GrantedAuthority role : authorities) {
                if (role.toString().equals("ROLE_ADMIN")) {
                    return true;
                }
            }
        }

        return false;
    }

    // check if the login user has user role
    protected boolean hasUserRole(Principal principal) {
        if (principal instanceof UsernamePasswordAuthenticationToken user) {
            Collection<GrantedAuthority> authorities = user.getAuthorities();

            for (GrantedAuthority role : authorities) {
                if (role.toString().equals("ROLE_USER")) {
                    return true;
                }
            }
        }
        return false;
    }


}

package dev.triamylo.learnwebapp.securityConfiguration;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractAuthenticationFilterConfigurer;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        // SOS --> /images/** --> all unter one ordner so that they can authenticate with one's
        //all can use the home page and can add new user, all the others musst be authenticated
        http.authorizeHttpRequests((requests) -> requests
                        .requestMatchers("/", "/formula", "/images/**").permitAll()
                        .requestMatchers("/users").hasRole("ADMIN") // noch mehr Endpunkt hier, das ist nur exemplar
                        .anyRequest()
                        .authenticated())
                .formLogin(AbstractAuthenticationFilterConfigurer::permitAll)  // das ist die default site from the security. (permitAll) I don't need to have permit to access in the login form.
                .logout(LogoutConfigurer::permitAll); //(permitAll) I don't need to have permit to access in the logout form.

        return http.build();
    }

    // use service
// zwei user mit ADMIN role by spring alle Rolle wird mit GROß
    @Bean
    public UserDetailsService userDetailsService() {

        List<UserDetails> users = new ArrayList<>();

        UserDetails user = User.withDefaultPasswordEncoder()
                .username("user")
                .password("1")
                .roles("USER")
                .build();

        users.add(user);

        UserDetails user2 = User.withDefaultPasswordEncoder()
                .username("user2")
                .password("2")
                .roles("ADMIN")
                .build();
        users.add(user2);

        UserDetails user3 = User.withDefaultPasswordEncoder()
                .username("user3")
                .password("3")
                .build();

        users.add(user3);

        return new InMemoryUserDetailsManager(users);
    }

}

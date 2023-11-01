package dev.triamylo.learnwebapp.securityConfiguration;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
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
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
            http
                    .authorizeHttpRequests((requests) -> requests // SOS that with the images, the same with others object javascript or something like this all unter one ordner so that they can authenticate with one's
                            .requestMatchers("/","/formula", "/images/**").permitAll() //all can in the home and can add new use
                            .anyRequest().authenticated()  // all the others musst be authenticated
                    )
                    .formLogin((form) -> form  // pos prepei na kanoun anmelden oi  xristes
                             // das ist die default site from the security
                            .permitAll() //  gia na mporo ta to xrisimopoiiso xoris na kano login kapou prota
                    )
                    .logout((logout) -> logout.permitAll()); // to idio einai kia edo

            return http.build();
    }

// use service
// zwei user mit ADMIN role by spring alle Rolle wird mit GROß
    @Bean
    public UserDetailsService userDetailsService() {

        List<UserDetails> users = new ArrayList<>();

        UserDetails user =  User.withDefaultPasswordEncoder()
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

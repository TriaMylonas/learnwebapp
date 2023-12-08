package dev.triamylo.learnwebapp.configuration;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractAuthenticationFilterConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
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
                        .requestMatchers("/", "/user/create","/user/post", "/images/**").permitAll()
                        .requestMatchers("/user/delete/**","/role/**").hasRole("ADMIN") // die Liste kann von ADMIN ausgerufen und bearbeiten werden.
                        .requestMatchers("/user/list").hasRole("ADMIN")
                        .requestMatchers( "/user/update/**").hasAnyRole("ADMIN", "USER")
                        .anyRequest()
                        .authenticated())
                .formLogin(AbstractAuthenticationFilterConfigurer::permitAll)  // das ist die default site from the security. (permitAll) I don't need to have permit to access in the login form.
                .logout((logout) -> logout.logoutSuccessUrl("/").permitAll()); //(permitAll) I don't need to have permit to access in the logout form & goes to the start site again.
//                .securityMatcher(PathRequest.toH2Console())
//                .csrf((csrf)-> csrf.disable())
//                .headers((headers)-> headers.frameOptions((frame)->frame.sameOrigin()));

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {

        List<UserDetails> users = new ArrayList<>();

        UserDetails user = User.withUsername("1")
                .password(encoder().encode("1"))
                .roles("USER")
                .build();

        users.add(user);

        UserDetails user2 = User.withUsername("2")
                .password(encoder().encode("2"))
                .roles("ADMIN")
                .build();
        users.add(user2);

        UserDetails user3 = User.withUsername("user3")
                .password(encoder().encode("3"))
                .build();

        users.add(user3);

        return new InMemoryUserDetailsManager(users);
    }

    @Bean
    public PasswordEncoder encoder(){
        return new BCryptPasswordEncoder();
    }


}

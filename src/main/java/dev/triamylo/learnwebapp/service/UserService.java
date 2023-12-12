package dev.triamylo.learnwebapp.service;

import dev.triamylo.learnwebapp.model.User;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;
import java.util.Optional;

public interface UserService{
    List<User> list();

    void add(User user);

    void delete(String uuid);

    User get(String uuid);

    void update(User aUser);

    Optional<User> findByFirstName(String name);

    Optional<User> findByUsername(String username);
}

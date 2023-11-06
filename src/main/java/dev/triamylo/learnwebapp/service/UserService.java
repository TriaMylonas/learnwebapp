package dev.triamylo.learnwebapp.service;

import dev.triamylo.learnwebapp.model.User;

import java.util.List;

public interface UserService {
    List<User> list();

    void add(User user);

    void delete(String uuid);

    User get(String uuid);

    void update(User aUser);

    User findByName(String name);
}

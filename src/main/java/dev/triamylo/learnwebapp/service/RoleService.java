package dev.triamylo.learnwebapp.service;


import dev.triamylo.learnwebapp.model.Role;

import java.util.List;
import java.util.Optional;

public interface RoleService {

    List<Role> list();
    void add(Role role);
    void delete(String uuid);
    Role get(String uuid);
    void update(Role role);

    Role findByName(String selectedRole);

}

package dev.triamylo.learnwebapp.repository;

import dev.triamylo.learnwebapp.model.Role;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface RoleRepository extends CrudRepository<Role,String> {

    Optional<Role> findByRoleName(String name);
}

package dev.triamylo.learnwebapp.service;

import dev.triamylo.learnwebapp.model.Role;
import dev.triamylo.learnwebapp.repository.RoleRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class RoleServiceImp implements RoleService {

    private final RoleRepository roleRepository;

    public RoleServiceImp(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public List<Role> list() {
        return (List<Role>) roleRepository.findAll();
    }

    @Override
    public void add(Role role) {
        roleRepository.save(role);
    }


    @Override
    public void delete(String uuid) {
        roleRepository.deleteById(uuid);
    }

    @Override
    public Role get(String uuid) {
        return roleRepository.findById(uuid).orElse(null);
    }

    @Override
    public void update(Role role) {
        roleRepository.save(role);
    }

}

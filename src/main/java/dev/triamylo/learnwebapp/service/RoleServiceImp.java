package dev.triamylo.learnwebapp.service;

import dev.triamylo.learnwebapp.model.Role;
import dev.triamylo.learnwebapp.repository.RoleRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class RoleServiceImp implements RoleService{

    private final RoleRepository roleRepository;
    public RoleServiceImp(RoleRepository roleRepository){
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
    public void delete(String uuid){
        roleRepository.deleteById(uuid);
    }

    @Override
    public Role get(String uuid) {
        return roleRepository.findById(uuid).orElse(null);
    }

    @Override
    public void update(Role role) {
        Role oldRole = roleRepository.findById(role.getUuid()).orElse(null);

        if(oldRole == null){
            return;
        }

        oldRole.setRoleName(role.getRoleName());
        oldRole.setRoleDescription(role.getRoleDescription());

        roleRepository.save(oldRole);
    }

    @Override
    public Role findByName(String selectedRole) {
        return roleRepository.findByRoleName(selectedRole).orElse(null);
    }

}

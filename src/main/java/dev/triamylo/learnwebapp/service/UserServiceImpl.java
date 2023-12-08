package dev.triamylo.learnwebapp.service;

import dev.triamylo.learnwebapp.model.User;
import dev.triamylo.learnwebapp.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * This service can add, update or delete users. The service shows the know user.
 */
@Service
@Transactional
public class UserServiceImpl implements UserService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    @Override
    public List<User> list() {
        return (List<User>) userRepository.findAll();
    }

    @Override
    public void add(User user) {
        //ich hash der password erstmal, und dann schicke ich zu DB
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }

    @Override
    public void delete(String uuid) {
        userRepository.deleteById(uuid);
    }

    @Override
    public User get(String uuid) {
        return userRepository.findById(uuid).orElse(null);
    }


    @Override
    public void update(User aUser) {
        User existingUser = userRepository.findById(aUser.getUuid()).orElse(null);
        if(existingUser == null){
            return;
        }

        // Update changes
        existingUser.setFirstName(aUser.getFirstName());
        existingUser.setLastName(aUser.getLastName());
        existingUser.setUsername(aUser.getUsername());
        existingUser.setDob(aUser.getDob());
        existingUser.setHeight(aUser.getHeight());

        // Update password and encode password, if password is set
        if (aUser.getPassword() != null && !aUser.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(aUser.getPassword()));
        }

        userRepository.save(existingUser);
    }


    //find the user objekt by his name from the database
    @Override
    public Optional<User> findByFirstName(String name) {
        return userRepository.findByFirstName(name);
    }

    @Override
    public Optional<User> findByUsername(String name) {
        return userRepository.findByUsername(name);
    }


}

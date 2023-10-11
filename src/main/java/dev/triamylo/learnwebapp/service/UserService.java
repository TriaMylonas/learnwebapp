package dev.triamylo.learnwebapp.service;

import dev.triamylo.learnwebapp.model.User;
import dev.triamylo.learnwebapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * This service can add, update or delete users. The service shows the know user.
 */
@Service
public class UserService {


    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }




    public List<User> list(){
     return (List<User>) userRepository.findAll();
    }

    public void add(User user){
        userRepository.save(user);
    }

    public void delete(String uuid) {
        userRepository.deleteById(uuid);
    }

    public User get(String uuid){
        return userRepository.findById(uuid).orElse(null);
    }


    public void update(User aUser) {

        User altUser = userRepository.findById(aUser.getUuid()).orElse(null);

        if(altUser == null){
            return;
        }

        altUser.setFirstName(aUser.getFirstName());
        altUser.setLastName(aUser.getLastName());
        altUser.setDob(aUser.getDob());
        altUser.setHeight(aUser.getHeight());

        userRepository.save(altUser);
    }
}

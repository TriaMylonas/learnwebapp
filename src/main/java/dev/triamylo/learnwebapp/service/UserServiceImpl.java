package dev.triamylo.learnwebapp.service;

import dev.triamylo.learnwebapp.model.User;
import dev.triamylo.learnwebapp.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * This service can add, update or delete users. The service shows the know user.
 */
@Service
public class UserServiceImpl implements UserService {


    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }




    @Override
    public List<User> list(){
     return (List<User>) userRepository.findAll();
    }

    @Override
    public void add(User user){
        userRepository.save(user);
    }

    @Override
    public void delete(String uuid) {
        userRepository.deleteById(uuid);
    }

    @Override
    public User get(String uuid){
        return userRepository.findById(uuid).orElse(null);
    }


    @Override
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



    //find the user objekt by his name from the database
    //TODO --> auch Test für diese Methode !
    @Override
    public Optional<User> findByName(String name) {

        List<User> users = (List<User>) userRepository.findAll();

        //TODO --> ich muss nicht alle User von den Datenbank load! ich muss etwas machen, dem in der Datenbank zucht und mir das Ergebnisse zurück gibt (Denis hat mir aud Teams Kapitel gesicht)

        return users.stream().filter(user -> user.getFirstName().equals(name)).findFirst();

/*        for (User user:users){
 *            if(user.getFirstName().equals(name)){
 *                return user;
 *            }
 *        }
 *        return null;
*/
    }

}

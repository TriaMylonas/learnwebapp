package dev.triamylo.learnwebapp.service;

import dev.triamylo.learnwebapp.model.User;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * This service can add, update or delete users. The service shows the know user.
 */
@Service
public class UserService {


//    die durch das UserRepository ersetzt werden. Wir brauchen nicht mehr die Ram users List.
    private final List<User> users = new ArrayList<>();


    public List<User> list(){
     return Collections.unmodifiableList(users);
    }

    public void add(User user){
        user.setUuid(UUID.randomUUID().toString());
        users.add(user);
    }

    public void delete(String uuid) {
        // I have made it with for loop, but IDE suggest me this...
        users.removeIf(user -> user.getUuid().equals(uuid));
    }


    public User get(String uuid){
         return users.stream().filter(user -> user.getUuid().equals(uuid)).findFirst().orElse(null);
    }


    public void update(User aUser) {
        User altUser = get(aUser.getUuid());
        if(altUser == null){
            return;
        }

        altUser.setFirstName(aUser.getFirstName());
        altUser.setLastName(aUser.getLastName());
        altUser.setDob(aUser.getDob());
        altUser.setHeight(aUser.getHeight());
    }
}

package dev.triamylo.learnwebapp;

import dev.triamylo.learnwebapp.model.Role;
import dev.triamylo.learnwebapp.model.User;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;

@SpringBootTest
@ActiveProfiles(profiles = "test")
public abstract class AbstractApplicationTests {
    /*Diese Klass ist nur damit ich die Annotations weider zu erben zu den andere Test-Klassen.
    * @ActiveProfiles(profiles = "test") -> es wird der application-test.properties lessen und die parameters
    * für den Tests zur nehmen.
    */

    protected static User getNewUser() {
        return getNewUser("SomeRandomUuidInTheUrl", "35");
    }
    protected static User getNewUserWithNullUuid(String username) {
        return getNewUser(null, username);
    }

    protected static User getNewUser(String uuid, String username) {
        //create the parameters for the method
        User newUser = new User();
        newUser.setUuid(uuid);
        newUser.setUsername(username);
        newUser.setPassword("password");
        newUser.setFirstName("testFirstName");
        newUser.setLastName("testLastName");
        newUser.setDob(LocalDate.of(1999, 5, 5));
        newUser.setHeight(185);
        return newUser;
    }






    protected static Role getNewRole(String uuid, String roleName, String roleDescription ){
        Role role = new Role(roleName);
        role.setUuid(uuid);
        role.setRoleDescription(roleDescription);

        return role;
    }

    protected static Role getNewRoleWithNullUuid(String roleName){
        return getNewRole(null,roleName,"description");
    }
    protected static Role getNewRole(String roleName){
        return getNewRole("1",roleName,"description1");
    }
    protected static Role getNewRole(String roleName, String description){
        return getNewRole("1",roleName,description);
    }
    protected static Role getNewRole(){
        return getNewRole("1","role1","description1");
    }

}

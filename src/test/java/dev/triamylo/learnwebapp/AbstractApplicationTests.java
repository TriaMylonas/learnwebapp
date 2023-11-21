package dev.triamylo.learnwebapp;

import dev.triamylo.learnwebapp.model.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.boot.test.context.SpringBootTest;
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
        //create the parameters for the method
        User newUser = new User();
        newUser.setUuid("SomeRandomUuidInTheUrl");
        newUser.setUsername("35");
        newUser.setFirstName("testFirstName");
        newUser.setLastName("testLastName");
        newUser.setDob(LocalDate.of(1999, 5, 5));
        newUser.setHeight(185);
        return newUser;
    }
    protected static User getNewUserWithNullUuid(String username) {
        //create the parameters for the method
        User newUser = new User();
        newUser.setUuid(null);
        newUser.setUsername(username);
        newUser.setFirstName("testFirstName");
        newUser.setLastName("testLastName");
        newUser.setDob(LocalDate.of(1999, 5, 5));
        newUser.setHeight(185);
        return newUser;
    }
}

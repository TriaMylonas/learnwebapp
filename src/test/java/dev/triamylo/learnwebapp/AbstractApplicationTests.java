package dev.triamylo.learnwebapp;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles(profiles = "test")
public abstract class AbstractApplicationTests {
    /*Diese Klass ist nur damit ich die Annotations weider zu erben zu den andere Test-Klassen.
    * @ActiveProfiles(profiles = "test") -> es wird der application-test.properties lessen und die parameters
    * für den Tests zur nehmen.
    */
}

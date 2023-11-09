package dev.triamylo.learnwebapp;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

@AutoConfigureMockMvc
public abstract class AbstractMockUpTests extends AbstractApplicationTests {
    //Diese Klass ist nur damit ich die Annotation @AutoConfigureMockMvc erben zu andere Test-Klassen.
    //Und ich erbe auch eine Annotation von der AbstractApplicationTest.
}

package dev.triamylo.learnwebapp.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "Users")
public class User {

    @Id
    @UuidGenerator
    private String uuid;

    @NotEmpty
    @NotNull
    @Size(min = 1, max = 10)
    private String firstName;
    @NotEmpty
    @NotNull
    @Size(min = 2, max = 12)
    private String lastName;


    @Past(message = "muss in der Vergangenheit sein")
    @NotNull
    private LocalDate dob;

    @Min(1)
    private int height;

    public User() {

    }

    public User(String firstName, String lastName, LocalDate dob, int height) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.dob = dob;
        this.height = height;
    }

    public String getUuid() {
        return uuid;
    }
    public void setUuid(String uuid){this.uuid = uuid;}


    public LocalDate getDob() {
        return dob;
    }


    public void setDob(LocalDate dob) {

        this.dob = dob;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Override
    public String toString() {
        return String.format("Hello World! Iam %s %s!!", firstName, lastName);
    }

    @Override
    public boolean equals(Object o) {

        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(uuid, user.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid);
    }
}

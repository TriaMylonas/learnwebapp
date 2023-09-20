package dev.triamylo.learnwebapp.Models;

import jakarta.validation.constraints.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

public class User {
    @NotEmpty
    @NotNull
    @Size(min = 1, max = 10)
    private String firstName;
    private String lastName;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Past
    @NotNull
    private Date dob;

    @Min(1)
    private int height;

    public User(){

    }

    public User(String firstName, String lastName, Date dob, int height) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.dob = dob;
        this.height = height;
    }

    public Date getDob() {
        return dob;
    }

    public void setDob(Date dob) {
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
}

package dev.triamylo.learnwebapp.Models;

public class AGuy {

    private String firstName;
    private String lastName;
    private int alter;

    public AGuy(String firstName, String lastName, int alter){
        this.firstName = firstName;
        this.lastName = lastName;
        this.alter = alter;
    }


    @Override
    public String toString() {
        return String.format("Hello World! Iam %s %s and I am %d years alt",firstName,lastName,alter);
    }
}

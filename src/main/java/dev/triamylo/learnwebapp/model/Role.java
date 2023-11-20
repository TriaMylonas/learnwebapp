package dev.triamylo.learnwebapp.model;


import jakarta.persistence.*;
import org.hibernate.annotations.UuidGenerator;

import java.util.List;

@Entity
@Table(name = "role")
public class Role {

    @ManyToMany(mappedBy = "roles")
    private List<User> users;


    @Id
    @UuidGenerator
    private String uuid;


    @Column(unique = true)
    private String roleName;



    public Role() {
    }

    public Role(String uuid, String roleName) {
        this.uuid = uuid;
        this.roleName = roleName;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }
}

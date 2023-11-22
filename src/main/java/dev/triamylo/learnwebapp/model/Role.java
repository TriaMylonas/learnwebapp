package dev.triamylo.learnwebapp.model;


import jakarta.persistence.*;
import org.hibernate.annotations.UuidGenerator;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "role")
public class Role {

    @ManyToMany(mappedBy = "roles", cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
    private List<User> users = new ArrayList<>();


    @Id
    @UuidGenerator
    private String uuid;


    @Column(unique = true)
    private String roleName;

    private String beschreibung; // kurz was das role macht. vielleicht bis 100char zb.


    public Role() {
    }

    public Role(String roleName) {
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

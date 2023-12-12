package dev.triamylo.learnwebapp.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.UuidGenerator;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "users")
public class User implements UserDetails {

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH}, fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_role",
            joinColumns = {@JoinColumn(name = "user_uuid")},
            inverseJoinColumns = {@JoinColumn(name = "role_uuid")}
    )
    private List<Role> roles = new ArrayList<>();


    @Id
    @UuidGenerator
    private String uuid;

    @Column(unique = true)
    @NotNull
    @NotEmpty
    private String username;

    @Column(nullable = false)
    private String password;
    @NotEmpty
    @NotNull
    @Size(min = 1, max = 20)
    private String firstName;

    @NotEmpty
    @NotNull
    @Size(min = 2, max = 20)
    private String lastName;

    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dob;
    @Min(1)
    private int height;


    public User() {

    }

    public User(String username, String password, String firstName, String lastName, LocalDate dob, int height) {
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.dob = dob;
        this.height = height;
    }



    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // one empty list of GrantedAuthority's
        List<GrantedAuthority> authorities = new ArrayList<>();
        // then I fulled this list with my roles
        for (Role role : roles) {
            //the roles name must be always uppercase and all authorities are starting with ROLE_
            authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getRoleName().toUpperCase()));
        }
        return authorities;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }


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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public void addRole(Role role) {
        roles.add(role);
    }

}

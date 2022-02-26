package com.garden.treehouse.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.garden.treehouse.entities.security.UserRole;
import lombok.Data;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@Data
public class User {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name="id", nullable = false, updatable = false)
    private Long id;
    private String password;
    private String firstName;
    private String lastName;

    @Column(name="email", nullable = false, updatable = false)
    private String email;
    private String phone;
    private boolean enabled=false;
    private boolean credentialsNonExpired=true;
    private boolean accountNonLocked=true;
    private boolean accountNonExpired=true;

    @OneToMany(mappedBy = "user", cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    @JsonIgnore
    private Set<UserRole> userRoles = new HashSet<>();
}

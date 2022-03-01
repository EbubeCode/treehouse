package com.garden.treehouse.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.garden.treehouse.entities.security.UserRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
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
    private boolean enabled=false;

    @OneToMany(mappedBy = "user", cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    @JsonIgnore
    private Set<UserRole> userRoles = new HashSet<>();
}

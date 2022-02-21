package com.elsys.globalserver.Models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(nullable = false)
    private String fullname;
    @Column(nullable = false, unique = true)
    private String username;
    @Column(nullable = false)
    private String password;
    @Column(nullable = false)
    private String role;

    private String uin;
    private String workplace;

    public User(String fullname, String username, String password, String role) {
        this.fullname = fullname;
        this.username = username;
        this.password = password;
        this.role = role;
    }
}

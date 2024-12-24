package com.sixsense.newsfeed.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "user")
@Entity
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "address", nullable = false)
    private String address;

    @Column(name = "age", nullable = false)
    private Integer age;

<<<<<<< Updated upstream
=======
    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status;


    @Builder
    public User(String email, String password, String name, String address, Integer age) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.address = address;
        this.age = age;
        status = Status.ACTIVE;
    }
>>>>>>> Stashed changes
}

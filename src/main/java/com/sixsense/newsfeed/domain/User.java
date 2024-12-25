package com.sixsense.newsfeed.domain;

import com.sixsense.newsfeed.dto.UpdateUserRequestDto;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
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

    @Column(name = "address")
    private String address;

    @Column(name = "age")
    private Integer age;


    @Builder
    public User(String email, String password, String name, String address, Integer age) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.address = address;
        this.age = age;
        status = Status.ACTIVE;
    }

    public void update(UpdateUserRequestDto requestDto, String encodedPassword) {
        this.name = requestDto.name();
        this.password = encodedPassword;
        this.address = requestDto.address();
        this.age = requestDto.age();
    }

    public void deleteMe() {
        // Soft deletion
        status = Status.DELETED;
    }
}

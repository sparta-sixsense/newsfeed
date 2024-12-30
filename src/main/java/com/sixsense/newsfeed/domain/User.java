package com.sixsense.newsfeed.domain;

import com.sixsense.newsfeed.dto.UpdateUserRequestDto;
import jakarta.persistence.*;
import lombok.*;

@ToString
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

    @Column(name = "profile_img_url")
    private String profileImgUrl;

    @Column(name = "address")
    private String address;

    @Column(name = "age")
    private Integer age;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private Status status;


    @Builder
    public User(String email, String password, String name, String profileImgUrl, String address, Integer age) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.profileImgUrl = profileImgUrl;
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

package com.sixsense.newsfeed.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;

    @Column(name = "img_url")
    private String imgUrl;

    @Column(name = "content", nullable = false)
    private String content;

    @JoinColumn(name = "user_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    public Post(String content, String imgUrl, User user){
        this.content = content;
        this.imgUrl = imgUrl;
        this.user = user;
        status = Status.ACTIVE;
    }

    public void update(String content, String imgUrl) {
        this.content = content;
        this.imgUrl = imgUrl;
    }
}

package com.sixsense.newsfeed.domain;

import com.sixsense.newsfeed.error.ErrorCode;
import com.sixsense.newsfeed.error.exception.base.ConflictException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "post")
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

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status;

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

    public void deactive(){
        if(this.status == Status.INACTIVE){
            throw new ConflictException(ErrorCode.POST_ALREADY_INACTIVE);
        }
        this.status = Status.INACTIVE;
    }

    public void activate(){
        if(this.status == Status.ACTIVE){
            throw new ConflictException(ErrorCode.POST_ALREADY_ACTIVE);
        }
        this.status = Status.ACTIVE;
    }
}

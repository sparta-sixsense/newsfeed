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

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted = false;

    @JoinColumn(name = "user_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    public Post(String content, String imgUrl, User user){
        this.content = content;
        this.imgUrl = imgUrl;
        this.user = user;
    }

    public void update(String content, String imgUrl) {
        this.content = content;
        this.imgUrl = imgUrl;
    }

    public void setIsDeleted(boolean isDeleted) {
        if (this.isDeleted == isDeleted) {
            throw new ConflictException(isDeleted ? ErrorCode.POST_ALREADY_DELETED : ErrorCode.POST_ALREADY_RESTORED);
        }
        this.isDeleted = isDeleted;
    }
}

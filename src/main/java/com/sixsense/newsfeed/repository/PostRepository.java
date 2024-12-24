package com.sixsense.newsfeed.repository;

import com.sixsense.newsfeed.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findAllByUserId(Long userId);
    List<Post> findAllByOrderByUpdatedAtDesc();

    default Post findByIdOrElseThrow(Long id) {
        return findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Does not exist id = " + id));
    }
}

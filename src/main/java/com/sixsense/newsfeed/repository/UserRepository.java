package com.sixsense.newsfeed.repository;

import com.sixsense.newsfeed.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}

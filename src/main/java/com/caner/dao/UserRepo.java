package com.caner.dao;

import com.caner.bean.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepo extends JpaRepository<User, Long> {
    User findByEmail(String email);

    User findByUserUUId(String userUUId);
}

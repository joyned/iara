package com.iara.core.service;

import com.iara.core.entity.User;
import io.jsonwebtoken.Claims;

import java.util.Optional;

public interface UserService extends BaseService<User> {

    void resetPassword(String id) throws IllegalArgumentException;

    void changePassword(String oldPassword, String newPassword);

    Optional<User> findByEmail(String email);

    User me(Claims claims);
}

package com.iara.core.service.impl;

import com.iara.core.entity.User;
import com.iara.core.exception.UserModificationException;
import com.iara.core.exception.UserNotFoundException;
import com.iara.core.repository.UserRepository;
import com.iara.core.service.AuthenticationService;
import com.iara.core.service.UserService;
import com.iara.utils.PasswordGenerator;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    @Value("${security.password.minLen}")
    private int minLenPassword;

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;


    @Override
    public Page<User> search(Specification<User> spec, Pageable pageable) {
        return repository.findAll(spec, pageable);
    }

    @Override
    public User persist(User entity) {
        if (StringUtils.isNotBlank(entity.getId())) {
            Optional<User> optionalUser = repository.findById(entity.getId());
            optionalUser.ifPresent(user -> entity.setPassword(user.getPassword()));
        }
        return repository.save(entity);
    }

    @Override
    public void delete(String id) {
        repository.deleteById(id);
    }

    @Override
    public void resetPassword(String id) throws IllegalArgumentException {
        Optional<User> optionalUser = repository.findById(id);

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();

            if (Objects.nonNull(user.getIsSSO()) && user.getIsSSO()) {
                throw new UserModificationException("The following user %s is a SSO user. Password cannot be reset.", user.getEmail());
            }

            String newPassword = PasswordGenerator.generateSecurePassword(minLenPassword);
            String encodedPassword = passwordEncoder.encode(newPassword);
            user.setPassword(encodedPassword);
            repository.save(user);
        }
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return repository.findByEmail(email);
    }

    @Override
    public User me(Claims claims) {
        Optional<User> optionalUser = findByEmail(claims.getSubject());
        return optionalUser.orElseThrow(() -> new UserNotFoundException("User %s was not found.", claims.getSubject()));
    }
}

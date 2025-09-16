package com.iara.core.service;

import com.iara.core.entity.User;
import com.iara.core.exception.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@Transactional
@Rollback
class UserServiceTest {

    @Autowired
    UserService userService;

    @Autowired
    RoleService roleService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @BeforeEach
    void init() {
        User user = new User();
        user.setName("Testing");
        user.setEmail("testing@email.com");
        user.setIsSSO(false);
        user.setPassword(passwordEncoder.encode("testing_password"));
        user.setRoles(roleService.search(null, Pageable.unpaged()).stream().collect(Collectors.toSet()));
        userService.persist(user);

        User sso = new User();
        sso.setName("Testing SSO");
        sso.setEmail("sso@email.com");
        sso.setIsSSO(true);
        sso.setRoles(roleService.search(null, Pageable.unpaged()).stream().collect(Collectors.toSet()));
        userService.persist(sso);
    }

    @Test
    void Given_NoFilter_ShouldFetch() {
        Page<User> res = userService.search(null, Pageable.unpaged());
        assertNotNull(res);
        assertEquals(3, res.getContent().size());
    }

    @Test
    void Given_ValidUser_ShouldPersist() {
        User user = new User();
        user.setName("Testing 123");
        user.setEmail("testing123@email.com");
        user.setIsSSO(false);
        user.setPassword("testing_password");
        user.setRoles(roleService.search(null, Pageable.unpaged()).stream().collect(Collectors.toSet()));
        User persisted = userService.persist(user);
        assertNotNull(persisted);
        assertNotNull(persisted.getId());
        assertEquals("Testing 123", persisted.getName());
    }

    @Test
    void Given_InvalidUserWithEmptyName_ShouldThrows() {
        User user = new User();
        user.setName("");
        user.setEmail("testing123@email.com");
        user.setIsSSO(false);
        user.setPassword("testing_password");
        user.setRoles(roleService.search(null, Pageable.unpaged()).stream().collect(Collectors.toSet()));
        assertThrows(InvalidUserException.class, () -> userService.persist(user));
    }

    @Test
    void Given_InvalidUserWithEmptyEmail_ShouldThrows() {
        User user = new User();
        user.setName("Testing User");
        user.setEmail("");
        user.setIsSSO(false);
        user.setPassword("testing_password");
        user.setRoles(roleService.search(null, Pageable.unpaged()).stream().collect(Collectors.toSet()));
        assertThrows(InvalidUserException.class, () -> userService.persist(user));
    }

    @Test
    void Given_InvalidUserWithNullName_ShouldThrows() {
        User user = new User();
        user.setName(null);
        user.setEmail("testing123@email.com");
        user.setIsSSO(false);
        user.setPassword("testing_password");
        user.setRoles(roleService.search(null, Pageable.unpaged()).stream().collect(Collectors.toSet()));
        assertThrows(InvalidUserException.class, () -> userService.persist(user));
    }

    @Test
    void Given_InvalidUserWithNullEmail_ShouldThrows() {
        User user = new User();
        user.setName("Testing User");
        user.setEmail(null);
        user.setIsSSO(false);
        user.setPassword("testing_password");
        user.setRoles(roleService.search(null, Pageable.unpaged()).stream().collect(Collectors.toSet()));
        assertThrows(InvalidUserException.class, () -> userService.persist(user));
    }

    @Test
    void Given_NewUserWithAlreadyExistingEmail_ShouldThrow() {
        User user = new User();
        user.setName("Testing User");
        user.setEmail("testing@email.com");
        user.setIsSSO(false);
        user.setPassword("testing_password");
        user.setRoles(roleService.search(null, Pageable.unpaged()).stream().collect(Collectors.toSet()));
        assertThrows(DuplicatedUserEmailException.class, () -> userService.persist(user));
    }

    @Test
    void Given_ValidUser_ShouldDelete() {
        User user = new User();
        user.setName("Testing 123");
        user.setEmail("testing123@email.com");
        user.setIsSSO(false);
        user.setPassword("testing_password");
        user.setRoles(roleService.search(null, Pageable.unpaged()).stream().collect(Collectors.toSet()));
        User persisted = userService.persist(user);

        assertDoesNotThrow(() -> userService.delete(persisted.getId()));
    }

    @Test
    void Given_ValidUser_ShouldResetPassword() {
        Optional<User> optionalUser = userService.findByEmail("testing@email.com");

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            String oldPwd = user.getPassword();

            userService.resetPassword(user.getId());

            Optional<User> newOptUser = userService.findByEmail("testing@email.com");

            assertTrue(newOptUser.isPresent());
            assertNotEquals(oldPwd, newOptUser.get().getPassword());
        }
    }

    @Test
    void Given_SSOUser_ShouldThrowWhenResetPassword() {
        Optional<User> optionalUser = userService.findByEmail("sso@email.com");
        optionalUser.ifPresent(user -> assertThrows(UserModificationException.class, () -> userService.resetPassword(user.getId())));
    }

    @Test
    @WithMockUser(username = "testing@email.com")
    void Given_ValidUser_ShouldChangePassword() {
        Optional<User> optionalUser = userService.findByEmail("testing@email.com");

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            String newPwd = "new_testing_pwd";
            String oldPwd = user.getPassword();

            userService.changePassword("testing_password", newPwd);

            Optional<User> newOptUser = userService.findByEmail("testing@email.com");

            assertTrue(newOptUser.isPresent());
            assertTrue(passwordEncoder.matches(newPwd, newOptUser.get().getPassword()));
            assertNotEquals(oldPwd, newOptUser.get().getPassword());
        }
    }

    @Test
    @WithMockUser(username = "testing@email.com")
    void Given_ValidUserWithWrongOldPassword_ShouldThrows() {
        assertThrows(InvalidCredentialsException.class, () -> userService.changePassword("testing_password_wrong", "new_pwd"));
    }

    @Test
    @WithMockUser(username = "sso@email.com")
    void Given_SSOUser_ShouldThrows() {
        assertThrows(UserModificationException.class, () -> userService.changePassword("testing_password_wrong", "new_pwd"));
    }

    @Test
    @WithMockUser(username = "not_existing@email.com")
    void Given_InvalidUser_ShouldThrows() {
        assertThrows(UserNotFoundException.class, () -> userService.changePassword("testing_password_wrong", "new_pwd"));
    }

    @Test
    @WithMockUser(username = "testing@email.com")
    void Given_ValidUser_ShouldFetchUserInfo() {
        User user = userService.me();
        assertNotNull(user);
        assertEquals("testing@email.com", user.getEmail());
    }

    @Test
    @WithMockUser(username = "not_existing@email.com")
    void Given_InvalidUser_ShouldFetchUserInfo() {
        assertThrows(UserNotFoundException.class, () -> userService.me());
    }

}

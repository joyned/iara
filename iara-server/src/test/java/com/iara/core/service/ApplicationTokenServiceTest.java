package com.iara.core.service;

import com.iara.core.entity.ApplicationToken;
import com.iara.core.entity.Policy;
import com.iara.core.entity.Role;
import com.iara.core.entity.User;
import com.iara.core.exception.OperationNotPermittedException;
import com.iara.core.exception.UserNotFoundException;
import com.iara.core.repository.PolicyRepository;
import com.iara.core.repository.RoleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
@Transactional
public class ApplicationTokenServiceTest {

    @Autowired
    PolicyService policyService;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PolicyRepository policyRepository;

    @Autowired
    ApplicationTokenService applicationTokenService;

    @Autowired
    UserService userService;

    @Test
    @WithMockUser(username = "testing@iara.com")
    void Given_ListOfTokens_ShouldPersist() {
        ApplicationToken tokenOne = new ApplicationToken();
        ApplicationToken tokenTwo = new ApplicationToken();

        tokenOne.setName("T1");
        tokenOne.setPolicies(policyService.search(null, Pageable.unpaged()).getContent());

        tokenTwo.setName("T2");
        tokenTwo.setPolicies(policyService.search(null, Pageable.unpaged()).getContent());

        assertDoesNotThrow(() -> applicationTokenService.saveAll(List.of(tokenOne, tokenTwo)));
    }

    @Test
    @WithMockUser(username = "testing@iara.com")
    void Given_User_ShouldFetchTokens() {
        ApplicationToken tokenOne = new ApplicationToken();
        ApplicationToken tokenTwo = new ApplicationToken();

        tokenOne.setName("T1");
        tokenOne.setPolicies(policyService.search(null, Pageable.unpaged()).getContent());
        tokenOne.setCreatedBy("testing@iara.com");

        tokenTwo.setName("T2");
        tokenTwo.setPolicies(policyService.search(null, Pageable.unpaged()).getContent());
        tokenTwo.setCreatedBy("testing@iara.com");

        applicationTokenService.saveAll(List.of(tokenOne, tokenTwo));

        List<ApplicationToken> tokens = applicationTokenService.findByOwner("testing@iara.com");
        assertNotNull(tokens);
        assertEquals(2, tokens.size());
    }

    @Test
    @WithMockUser(username = "testing@iara.com")
    void Given_User_ShouldFetchTokensByUser() {
        ApplicationToken tokenOne = new ApplicationToken();
        ApplicationToken tokenTwo = new ApplicationToken();

        tokenOne.setName("T1");
        tokenOne.setPolicies(policyService.search(null, Pageable.unpaged()).getContent());
        tokenOne.setCreatedBy("testing@iara.com");

        tokenTwo.setName("T2");
        tokenTwo.setPolicies(policyService.search(null, Pageable.unpaged()).getContent());
        tokenTwo.setCreatedBy("testing@iara.com");

        applicationTokenService.saveAll(List.of(tokenOne, tokenTwo));

        Page<ApplicationToken> tokens = applicationTokenService.userTokens(Pageable.unpaged());
        assertNotNull(tokens);
        assertEquals(2, tokens.getSize());
    }

    @Test
    @WithMockUser(username = "testing@iara.com")
    void Given_Token_ShouldFetch() {
        ApplicationToken tokenOne = new ApplicationToken();
        tokenOne.setName("T1");
        tokenOne.setPolicies(policyService.search(null, Pageable.unpaged()).getContent());

        ApplicationToken persisted = applicationTokenService.persist(tokenOne);

        Optional<ApplicationToken> fetch = applicationTokenService.findByToken(persisted.getToken());

        assertTrue(fetch.isPresent());
        assertEquals(persisted.getToken(), fetch.get().getToken());
    }

    @Test
    @WithMockUser(username = "admin@example.com")
    void Given_Token_ShouldPersistWithUser() {
        ApplicationToken token = new ApplicationToken();
        token.setName("T1-NewUser");

        ApplicationToken persisted = applicationTokenService.persistUserToken(token);

        assertNotNull(persisted);
        assertNotNull(persisted.getToken());
        assertEquals("admin@example.com", token.getCreatedBy());
        assertEquals("Admin", persisted.getPolicies().getFirst().getName());
    }

    @Test
    @WithMockUser(username = "notexisting@iara.com")
    void Given_TokenWithNonExistingUser_ShouldThrow() {
        ApplicationToken token = new ApplicationToken();
        token.setName("T1-NewUser");

        assertThrows(UserNotFoundException.class, () -> applicationTokenService.persistUserToken(token));
    }

    @Test
    @WithMockUser(username = "admin@example.com")
    void Given_User_ShouldUpdateTokensPolicies() {
        Role newRole = createNewRoleAndPolicy();
        ApplicationToken token = new ApplicationToken();
        token.setName("T1-NewUser");

        ApplicationToken persisted = applicationTokenService.persistUserToken(token);

        Optional<User> optionalUser = userService.findByEmail("admin@example.com");

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();

            Set<Role> roles = new HashSet<>(user.getRoles());
            roles.add(newRole);
            user.setRoles(roles);

            userService.persist(user);

            applicationTokenService.updateUserTokensPolicies(user);

            Optional<ApplicationToken> optionalApplicationToken = applicationTokenService.findByToken(persisted.getToken());

            if (optionalApplicationToken.isPresent()) {
                ApplicationToken applicationToken = optionalApplicationToken.get();
                assertTrue(applicationToken.getPolicies().stream().anyMatch(policy -> policy.getName().equals("Testing Policy")));
            } else {
                fail();
            }
        } else {
            fail();
        }

    }

    @Test
    @WithMockUser(username = "testing@email.com")
    void Given_TokenId_ShouldDeleteWhenBelongs() {
        ApplicationToken tokenOne = new ApplicationToken();
        tokenOne.setName("T1");
        tokenOne.setPolicies(policyService.search(null, Pageable.unpaged()).getContent());
        tokenOne.setCreatedBy("testing@email.com");

        applicationTokenService.saveAll(List.of(tokenOne));

        List<ApplicationToken> tokens = applicationTokenService.findByOwner("testing@email.com");
        ApplicationToken applicationToken = tokens.getFirst();

        assertDoesNotThrow(() -> applicationTokenService.deleteUserToken(applicationToken.getId()));
    }

    @Test
    @WithMockUser(username = "admin@example.com")
    void Given_TokenId_ShouldThrowWhenNotBelongs() {
        ApplicationToken tokenOne = new ApplicationToken();
        tokenOne.setName("T1");
        tokenOne.setPolicies(policyService.search(null, Pageable.unpaged()).getContent());
        tokenOne.setCreatedBy("testing@email.com");

        applicationTokenService.saveAll(List.of(tokenOne));

        List<ApplicationToken> tokens = applicationTokenService.findByOwner("testing@email.com");
        ApplicationToken applicationToken = tokens.getFirst();

        assertThrows(OperationNotPermittedException.class, () -> applicationTokenService.deleteUserToken(applicationToken.getId()));
    }

    @Test
    void Given_NoFilter_ShouldFetchWithMaskedToken() {
        ApplicationToken tokenOne = new ApplicationToken();
        tokenOne.setName("T1");
        tokenOne.setPolicies(policyService.search(null, Pageable.unpaged()).getContent());
        tokenOne.setCreatedBy("testing@email.com");

        applicationTokenService.saveAll(List.of(tokenOne));

        Page<ApplicationToken> applicationTokens = applicationTokenService.search(null, Pageable.unpaged());

        ApplicationToken t1 = applicationTokens.getContent().getFirst();

        assertNotNull(applicationTokens);
        assertNotNull(t1);
        assertEquals("******", t1.getToken());
    }

    @Test
    @WithMockUser(username = "admin@example.com")
    void Given_AlreadyExistingToken_ShouldThrowWhenUpdating() {
        ApplicationToken tokenOne = new ApplicationToken();
        tokenOne.setName("T1");
        tokenOne.setPolicies(policyService.search(null, Pageable.unpaged()).getContent());
        ApplicationToken t1 = applicationTokenService.persist(tokenOne);

        t1.setName("UPDATED");

        assertThrows(OperationNotPermittedException.class, () -> applicationTokenService.persist(t1));
    }

    @Test
    void Given_AnyId_ShouldDeleteOrNotThrow() {
        assertDoesNotThrow(() -> applicationTokenService.delete(UUID.randomUUID().toString()));
    }

    Role createNewRoleAndPolicy() {
        Policy policy = new Policy();
        policy.setName("Testing Policy");
        policy.setRule("ALLOW READ AND WRITE IN KV AT @*");

        Policy persistedPolicy = policyRepository.saveAndFlush(policy);

        Role role = new Role();
        role.setName("Testing Role");
        role.setDescription("Testing Description");
        Set<Policy> policies = new HashSet<>();
        policies.add(persistedPolicy);
        role.setPolicies(policies);

        return roleRepository.saveAndFlush(role);
    }
}

package com.iara.core.service;

import com.iara.core.entity.Policy;
import com.iara.core.entity.Role;
import com.iara.core.exception.DuplicatedRoleException;
import com.iara.core.exception.RequiredParameterException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@Transactional
@Rollback
class RoleServiceTest {

    @Autowired
    RoleService roleService;

    @Autowired
    PolicyService policyService;

    @Test
    void Given_NoFilter_ShouldFetch() {
        Page<Role> roles = roleService.search(null, Pageable.unpaged());
        assertNotNull(roles);
        assertEquals(1, roles.getSize());
    }

    @Test
    void Given_ValidRole_ShouldPersist() {
        Role role = new Role();
        role.setName("Testing Role");
        role.setDescription("Testing Description");

        Policy policy = new Policy();
        policy.setName("Testing Policy");
        policy.setRule("ALLOW READ AND WRITE IN KV AT @*");

        role.setPolicies(Set.of(policy));

        Role persisted = roleService.persist(role);

        assertNotNull(persisted.getId());
        assertEquals("Testing Role", persisted.getName());
    }

    @Test
    void Given_InvalidPolicyWithEmptyName_ShouldThrows() {
        Role role = new Role();
        role.setName("");
        role.setDescription("Testing Description");

        Policy policy = new Policy();
        policy.setName("Testing Policy");
        policy.setRule("ALLOW READ AND WRITE IN KV AT @*");

        role.setPolicies(Set.of(policy));

        assertThrows(RequiredParameterException.class, () -> roleService.persist(role));
    }

    @Test
    void Given_InvalidPolicyWithNullName_ShouldThrows() {
        Role role = new Role();
        role.setName(null);
        role.setDescription("Testing Description");

        Policy policy = new Policy();
        policy.setName("Testing Policy");
        policy.setRule("ALLOW READ AND WRITE IN KV AT @*");

        role.setPolicies(Set.of(policy));

        assertThrows(RequiredParameterException.class, () -> roleService.persist(role));
    }

    @Test
    void Given_InvalidPolicyWithNoPolicy_ShouldThrows() {
        Role role = new Role();
        role.setName("Testing Role");
        role.setDescription("Testing Description");

        assertThrows(RequiredParameterException.class, () -> roleService.persist(role));
    }

    @Test
    void Given_ValidRoleWithAlreadyExistingName_ShouldThrows() {
        Role role = new Role();
        role.setName("Admin");
        role.setDescription("Testing Description");

        Policy policy = new Policy();
        policy.setName("Testing Policy");
        policy.setRule("ALLOW READ AND WRITE IN KV AT @*");

        role.setPolicies(Set.of(policy));

        assertThrows(DuplicatedRoleException.class, () -> roleService.persist(role));
    }

    @Test
    void Given_ValidRoleId_ShouldDelete() {
        Role role = new Role();
        role.setName("Testing Role");
        role.setDescription("Testing Description");

        Policy policy = new Policy();
        policy.setName("Testing Policy");
        policy.setRule("ALLOW READ AND WRITE IN KV AT @*");

        role.setPolicies(Set.of(policy));

        Role persisted = roleService.persist(role);

        assertDoesNotThrow(() -> roleService.delete(persisted.getId()));
    }
}

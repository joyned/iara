package com.iara.core.service;

import com.iara.core.entity.Policy;
import com.iara.core.exception.InvalidPolicyException;
import com.iara.core.exception.RequiredParameterException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@Transactional
class PolicyServiceTest {

    @Autowired
    PolicyService policyService;

    @Test
    void Given_NoFilter_ShouldFetch() {
        Page<Policy> policies = policyService.search(null, Pageable.unpaged());
        assertNotNull(policies);
        assertEquals(1, policies.getSize());
    }

    @Test
    void Given_ValidPolicy_ShouldPersist() {
        Policy policy = new Policy();

        policy.setName("Policy 1");
        policy.setRule("ALLOW READ AND WRITE IN KV AT @*");

        Policy persisted = policyService.persist(policy);

        assertNotNull(policy.getId());
        assertEquals("Policy 1", persisted.getName());
        assertEquals("ALLOW READ AND WRITE IN KV AT @*", persisted.getRule());
    }

    @Test
    void Given_InvalidPolicyWithEmptyName_ShouldThrows() {
        Policy policy = new Policy();

        policy.setName("");
        policy.setRule("ALLOW READ AND WRITE IN KV AT @*");

        assertThrows(RequiredParameterException.class, () -> policyService.persist(policy));
    }

    @Test
    void Given_InvalidPolicyWithNullName_ShouldThrows() {
        Policy policy = new Policy();

        policy.setName(null);
        policy.setRule("ALLOW READ AND WRITE IN KV AT @*");

        assertThrows(RequiredParameterException.class, () -> policyService.persist(policy));
    }

    @Test
    void Given_InvalidPolicyWithEmptyRule_ShouldThrows() {
        Policy policy = new Policy();

        policy.setName("Policy 1");
        policy.setRule("");

        assertThrows(RequiredParameterException.class, () -> policyService.persist(policy));
    }

    @Test
    void Given_InvalidPolicyWithNullRule_ShouldThrows() {
        Policy policy = new Policy();

        policy.setName("Policy 1");
        policy.setRule(null);

        assertThrows(RequiredParameterException.class, () -> policyService.persist(policy));
    }

    @Test
    void Given_InvalidPolicyWithInvalidRule_ShouldThrows_1() {
        Policy policy = new Policy();

        policy.setName("Policy 1");
        policy.setRule("ALLOW EDIT AND WRITE IN KV AT @*");

        assertThrows(InvalidPolicyException.class, () -> policyService.persist(policy));
    }

    @Test
    void Given_InvalidPolicyWithInvalidRule_ShouldThrows_2() {
        Policy policy = new Policy();

        policy.setName("Policy 1");
        policy.setRule("ALLOW EDIT IN KV AT @*");

        assertThrows(InvalidPolicyException.class, () -> policyService.persist(policy));
    }

    @Test
    void Given_InvalidPolicyWithInvalidRule_ShouldThrows_3() {
        Policy policy = new Policy();

        policy.setName("Policy 1");
        policy.setRule("ALLOW READ AND WRITE IN USERS AT @*");

        assertThrows(InvalidPolicyException.class, () -> policyService.persist(policy));
    }

    @Test
    void Given_InvalidPolicyWithInvalidRule_ShouldThrows_4() {
        Policy policy = new Policy();

        policy.setName("Policy 1");
        policy.setRule("ALLOW WRITE KV AT @*");

        assertThrows(InvalidPolicyException.class, () -> policyService.persist(policy));
    }

    @Test
    void Given_InvalidPolicyWithInvalidRule_ShouldThrows_5() {
        Policy policy = new Policy();

        policy.setName("Policy 1");
        policy.setRule("ALLOW READ AND WRITE IN USERS AT *");

        assertThrows(InvalidPolicyException.class, () -> policyService.persist(policy));
    }

    @Test
    void Given_InvalidPolicyWithInvalidRule_ShouldThrows_6() {
        Policy policy = new Policy();

        policy.setName("Policy 1");
        policy.setRule("ALLOW READ WRITE IN USERS AT @*");

        assertThrows(InvalidPolicyException.class, () -> policyService.persist(policy));
    }


    @Test
    void Given_ValidPolicy_ShouldDelete() {
        Policy policy = new Policy();

        policy.setName("Policy 1");
        policy.setRule("ALLOW READ AND WRITE IN KV AT @*");

        Policy persisted = policyService.persist(policy);

        assertDoesNotThrow(() -> policyService.delete(persisted.getId()));
    }
}

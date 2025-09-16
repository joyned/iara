package com.iara.core.service;

import com.iara.core.entity.Environment;
import com.iara.core.entity.Kv;
import com.iara.core.entity.Namespace;
import com.iara.core.exception.InvalidPolicyException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@Transactional
class PolicyExecutorServiceTest {

    @Autowired
    PolicyExecutorService policyExecutorService;

    @Autowired
    NamespaceService namespaceService;

    @Autowired
    EnvironmentService environmentService;

    @Autowired
    KeyValueService keyValueService;

    @Test
    @WithMockUser(authorities = {"@default/development:KV:READ"})
    void Given_UserWithAccessToDefaultNamespace_ShouldReturnTrue() {
        Namespace namespace = defaultNamespace();
        assertTrue(policyExecutorService.hasPermissionAtNamespace(namespace));
    }

    @Test
    void Given_ScopeWithAccessToDefaultNamespace_ShouldReturnTrue() {
        Namespace namespace = defaultNamespace();
        assertTrue(policyExecutorService.hasPermissionAtNamespace("@default/development:KV:READ", namespace));
    }

    @Test
    @WithMockUser(authorities = {"@*:KV:READ"})
    void Given_UserWithAccessToAllNamespace_ShouldReturnTrue() {
        Namespace namespace = defaultNamespace();
        assertTrue(policyExecutorService.hasPermissionAtNamespace(namespace));
    }

    @Test
    void Given_ScopeWithAccessToAllNamespace_ShouldReturnTrue() {
        Namespace namespace = defaultNamespace();
        assertTrue(policyExecutorService.hasPermissionAtNamespace("@*:KV:READ", namespace));
    }

    @Test
    @WithMockUser(authorities = {"@notaccess/development:KV:READ"})
    void Given_UserWithoutAccessToDefaultNamespace_ShouldReturnFalse() {
        Namespace namespace = defaultNamespace();
        assertFalse(policyExecutorService.hasPermissionAtNamespace(namespace));
    }

    @Test
    void Given_ScopeWithoutAccessToDefaultNamespace_ShouldReturnFalse() {
        Namespace namespace = defaultNamespace();
        assertFalse(policyExecutorService.hasPermissionAtNamespace("@notaccess/development:KV:READ", namespace));
    }

    @Test
    void Given_InvalidScope_ShouldReturnFalse() {
        Namespace namespace = defaultNamespace();
        assertFalse(policyExecutorService.hasPermissionAtNamespace("aaaaaaaaaaaaa/development:KV:READ", namespace));
    }

    @Test
    @WithMockUser(authorities = {"@default/development:KV:READ"})
    void Given_UserWithAccessToDevelopmentEnvironment_ShouldReturnTrue() {
        assertTrue(policyExecutorService.hasPermissionAtEnvironment(defaultEnvironment()));
    }

    @Test
    void Given_ScopeWithAccessToDevelopmentEnvironment_ShouldReturnTrue() {
        assertTrue(policyExecutorService.hasPermissionAtEnvironment("@default/development:KV:READ", defaultEnvironment()));
    }

    @Test
    @WithMockUser(authorities = {"@*:KV:READ"})
    void Given_UserWithAccessToAllEnvironment_ShouldReturnTrue() {
        assertTrue(policyExecutorService.hasPermissionAtEnvironment(defaultEnvironment()));
    }

    @Test
    void Given_ScopeWithAccessToAllEnvironment_ShouldReturnTrue() {
        assertTrue(policyExecutorService.hasPermissionAtEnvironment("@*:KV:READ", defaultEnvironment()));
    }

    @Test
    @WithMockUser(authorities = {"@aaaaa/aaaaaaa:KV:READ"})
    void Given_UserWithoutAccessToDevelopmentEnvironment_ShouldReturnTrue() {
        assertFalse(policyExecutorService.hasPermissionAtEnvironment(defaultEnvironment()));
    }

    @Test
    void Given_ScopeWithoutAccessToDevelopmentEnvironment_ShouldReturnFalse() {
        assertFalse(policyExecutorService.hasPermissionAtEnvironment("@aaaaa/aaaaaaa:KV:READ", defaultEnvironment()));
    }

    @Test
    void Given_InvalidScopeWithInvalidEnvironment_ShouldReturnTrue() {
        assertFalse(policyExecutorService.hasPermissionAtEnvironment("@aaaaa:KV:READ", defaultEnvironment()));
    }

    @Test
    @WithMockUser(authorities = {"@default/development:KV:WRITE"})
    void Given_UserWithWritePermissionInKV_ShouldReturnTrue() {
        Kv kv = createKv();
        assertTrue(policyExecutorService.hasWritePermissionInKV(kv));
    }

    @Test
    @WithMockUser(authorities = {"@default/development:KV:READ"})
    void Given_UserWithoutWritePermissionInKV_ShouldReturnTrue() {
        Kv kv = createKv();
        assertFalse(policyExecutorService.hasWritePermissionInKV(kv));
    }

    @Test
    @WithMockUser(authorities = {"@*:KV:WRITE"})
    void Given_UserWithWritePermissionInAllKV_ShouldReturnTrue() {
        Kv kv = createKv();
        assertTrue(policyExecutorService.hasWritePermissionInKV(kv));
    }

    @Test
    void Given_ValidPolicyRule_ShouldNotThrow() {
        assertDoesNotThrow(() -> policyExecutorService.validatePolicyRule("ALLOW READ AT #NAMESPACES"));
    }

    @Test
    void Given_ValidPolicyRule_ShouldNotThrow_1() {
        assertDoesNotThrow(() -> policyExecutorService.validatePolicyRule("ALLOW READ AND WRITE AT #NAMESPACES"));
    }

    @Test
    void Given_ValidPolicyRule_ShouldNotThrow_2() {
        assertDoesNotThrow(() -> policyExecutorService.validatePolicyRule("ALLOW WRITE AT #NAMESPACES"));
    }

    @Test
    void Given_ValidPolicyRule_ShouldNotThrow_3() {
        assertDoesNotThrow(() -> policyExecutorService.validatePolicyRule("ALLOW READ AND WRITE IN KV AT @default/development"));
    }

    @Test
    void Given_ValidPolicyRule_ShouldNotThrow_4() {
        assertDoesNotThrow(() -> policyExecutorService.validatePolicyRule("ALLOW WRITE IN KV AT @*"));
    }

    @Test
    void Given_EmptyPolicyRule_ShouldThrow() {
        assertThrows(InvalidPolicyException.class, () -> policyExecutorService.validatePolicyRule(""));
    }

    @Test
    void Given_NullPolicyRule_ShouldThrow() {
        assertThrows(InvalidPolicyException.class, () -> policyExecutorService.validatePolicyRule(null));
    }

    @Test
    void Given_ShortPolicyRule_ShouldThrow() {
        assertThrows(InvalidPolicyException.class, () -> policyExecutorService.validatePolicyRule("ALLOW READ #NAMESPACES"));
    }

    @Test
    void Given_PolicyRuleNotStartingWithAllow_ShouldThrow() {
        assertThrows(InvalidPolicyException.class, () -> policyExecutorService.validatePolicyRule("READ AND WRITE AT #NAMESPACES"));
    }

    @Test
    void Given_AdmPolicyRuleWithoutAt_ShouldThrow() {
        assertThrows(InvalidPolicyException.class, () -> policyExecutorService.validatePolicyRule("ALLOW READ AND WRITE #NAMESPACES"));
    }

    @Test
    void Given_InvalidPolicy_ShouldThrow_1() {
        assertThrows(InvalidPolicyException.class, () -> policyExecutorService.validatePolicyRule("ALLOW READ AND WRITE"));
    }

    @Test
    void Given_InvalidPolicy_ShouldThrow_2() {
        assertThrows(InvalidPolicyException.class, () -> policyExecutorService.validatePolicyRule("ALLOW READ WRITE AND AT #NAMESPACES"));
    }

    @Test
    void Given_InvalidPolicy_ShouldThrow_3() {
        assertThrows(InvalidPolicyException.class, () -> policyExecutorService.validatePolicyRule("ALLOW EDIT AT #NAMESPACES"));
    }

    @Test
    void Given_InvalidPolicy_ShouldThrow_4() {
        assertThrows(InvalidPolicyException.class, () -> policyExecutorService.validatePolicyRule("ALLOW READ AND WRITE AT"));
    }

    @Test
    void Given_InvalidPolicy_ShouldThrow_5() {
        assertThrows(InvalidPolicyException.class, () -> policyExecutorService.validatePolicyRule("ALLOW READ AND WRITE AT NAMESPACES"));
    }

    @Test
    void Given_InvalidPolicy_ShouldThrow_6() {
        assertThrows(InvalidPolicyException.class, () -> policyExecutorService.validatePolicyRule("ALLOW READ AND WRITE AT #NOVALIDRESOURCE"));
    }

    @Test
    void Given_InvalidPolicy_ShouldThrow_7() {
        assertThrows(InvalidPolicyException.class, () -> policyExecutorService.validatePolicyRule("ALLOW READ AND WRITE AT #NAMESPACES WITH OTHER PARAMS"));
    }

    @Test
    void Given_InvalidPolicy_ShouldThrow_8() {
        assertThrows(InvalidPolicyException.class, () -> policyExecutorService.validatePolicyRule("ALLOW READ AND WRITE IN NOT_VALID AT @dev/dev"));
    }

    @Test
    void Given_InvalidPolicy_ShouldThrow_9() {
        assertThrows(InvalidPolicyException.class, () -> policyExecutorService.validatePolicyRule("ALLOW READ AND WRITE IN NOT_VALID AT"));
    }

    @Test
    void Given_InvalidPolicy_ShouldThrow_10() {
        assertThrows(InvalidPolicyException.class, () -> policyExecutorService.validatePolicyRule("ALLOW READ AND WRITE IN NOT_VALID AT dev/dev"));
    }

    @Test
    void Given_InvalidPolicy_ShouldThrow_11() {
        assertThrows(InvalidPolicyException.class, () -> policyExecutorService.validatePolicyRule("ALLOW READ AND WRITE IN NOT_VALID AT @dev"));
    }


    Kv createKv() {
        Namespace namespace = defaultNamespace();
        Environment environment = defaultEnvironment();
        Kv kv = new Kv();

        kv.setKey("Testing Key");
        kv.setValue("Testing Value");
        kv.setNamespace(namespace);
        kv.setEnvironment(environment);
        return keyValueService.persist(kv);
    }

    Environment defaultEnvironment() {
        Namespace namespace = defaultNamespace();
        return environmentService.findByNamespace(namespace).getFirst();
    }

    Namespace defaultNamespace() {
        Optional<Namespace> namespaceOptional = namespaceService.findByName("default");
        return namespaceOptional.orElse(null);
    }

}

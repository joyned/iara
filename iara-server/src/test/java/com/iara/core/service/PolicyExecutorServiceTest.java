package com.iara.core.service;

import com.iara.core.entity.*;
import com.iara.core.entity.specification.BaseNamespacedSpecification;
import com.iara.core.entity.specification.KvSpecification;
import com.iara.core.entity.specification.SecretSpecification;
import com.iara.core.exception.InvalidPolicyException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

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

    @Autowired
    SecretService secretService;

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
    @WithMockUser(authorities = {"@*:KV:WRITE"})
    void Given_UserWithWritePermissionInAllKV_ShouldReturnTrue() {
        Kv kv = createKv();
        assertTrue(policyExecutorService.hasWritePermissionInKV(kv));
    }

    @Test
    @WithMockUser(authorities = {"@default/development:SECRET:WRITE"})
    void Given_UserWithWritePermissionInSecret_ShouldReturnTrue() {
        Secret secret = createSecret();
        assertTrue(policyExecutorService.hasWritePermissionInSecret(secret));
    }

    @Test
    @WithMockUser(authorities = {"@*:KV:WRITE"})
    void Given_UserWithWritePermissionInAllSecret_ShouldReturnTrue() {
        Secret secret = createSecret();
        assertTrue(policyExecutorService.hasWritePermissionInSecret(secret));
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

    @Test
    @WithMockUser(authorities = {"@default/development:KV:READ", "@team-a/staging:KV:READ"})
    void Given_UserWithMultipleNamespacesAndEnvironments_ShouldBuildSpecWithCorrectFilters() {
        BaseNamespacedSpecification<Kv> namespacedSpec = KvSpecification.builder().build();
        BaseNamespacedSpecification<Kv> spy = Mockito.spy(namespacedSpec);

        Specification<Kv> result = policyExecutorService.buildNamespacedSpec(spy);

        assertNotNull(result);
        verify(spy).hasPermission(
                Set.of("default", "team-a"),
                Set.of("development", "staging")
        );
    }

    @Test
    @WithMockUser(authorities = {"@*:KV:READ"})
    void Given_UserWithWildcardNamespace_ShouldBuildSpecWithEmptyFilters() {
        BaseNamespacedSpecification<Kv> namespacedSpec = KvSpecification.builder().build();
        BaseNamespacedSpecification<Kv> spy = Mockito.spy(namespacedSpec);

        Specification<Kv> result = policyExecutorService.buildNamespacedSpec(spy);

        assertNotNull(result);
        verify(spy).hasPermission(Set.of(), Set.of());
    }

    @Test
    @WithMockUser(authorities = {"@default/*:KV:READ"})
    void Given_UserWithWildcardEnvironment_ShouldBuildSpecWithNamespaceOnly() {
        BaseNamespacedSpecification<Kv> namespacedSpec = KvSpecification.builder().build();
        BaseNamespacedSpecification<Kv> spy = Mockito.spy(namespacedSpec);

        Specification<Kv> result = policyExecutorService.buildNamespacedSpec(spy);

        assertNotNull(result);
        verify(spy).hasPermission(Set.of("default"), Set.of("*"));
    }

    @Test
    @WithMockUser(authorities = {"@*/development:KV:READ"})
    void Given_UserWithWildcardNamespaceButSpecificEnvironment_ShouldBuildSpecWithEnvironmentOnly() {
        BaseNamespacedSpecification<Kv> namespacedSpec = KvSpecification.builder().build();
        BaseNamespacedSpecification<Kv> spy = Mockito.spy(namespacedSpec);

        Specification<Kv> result = policyExecutorService.buildNamespacedSpec(spy);

        assertNotNull(result);
        verify(spy).hasPermission(Set.of(), Set.of());
    }

    @Test
    void Given_NullRootSpecification_ShouldReturnNull() {
        Specification<Object> result = policyExecutorService.buildNamespacedSpec(null);

        assertNull(result);
    }

    @Test
    @WithMockUser(authorities = {})
    void Given_UserWithNoScopes_ShouldBuildSpecWithEmptyFilters() {
        BaseNamespacedSpecification<Kv> namespacedSpec = KvSpecification.builder().build();
        BaseNamespacedSpecification<Kv> spy = Mockito.spy(namespacedSpec);

        Specification<Kv> result = policyExecutorService.buildNamespacedSpec(spy);

        assertNotNull(result);
        verify(spy).hasPermission(Set.of(), Set.of());
    }

    @Test
    @WithMockUser(authorities = {"invalid-scope-format"})
    void Given_UserWithInvalidScopeFormat_ShouldBuildSpecWithEmptyFilters() {
        BaseNamespacedSpecification<Kv> namespacedSpec = KvSpecification.builder().build();
        BaseNamespacedSpecification<Kv> spy = Mockito.spy(namespacedSpec);

        Specification<Kv> result = policyExecutorService.buildNamespacedSpec(spy);

        assertNotNull(result);
        verify(spy).hasPermission(Set.of(), Set.of());
    }

    @Test
    @WithMockUser(authorities = {"@default/development:KV:READ", "@team-a/staging:KV:READ", "@*:KV:READ"})
    void Given_UserWithMixedWildcardAndSpecificScopes_ShouldBuildSpecWithOnlySpecificFilters() {
        BaseNamespacedSpecification<Kv> namespacedSpec = KvSpecification.builder().build();
        BaseNamespacedSpecification<Kv> spy = Mockito.spy(namespacedSpec);

        Specification<Kv> result = policyExecutorService.buildNamespacedSpec(spy);

        assertNotNull(result);
        verify(spy).hasPermission(Set.of("default", "team-a"), Set.of("development", "staging"));
    }

    @Test
    @WithMockUser(authorities = {"@default/development:KV:READ", "@default/production:KV:READ"})
    void Given_UserWithSameNamespaceDifferentEnvironments_ShouldBuildSpecWithDeduplicatedValues() {
        BaseNamespacedSpecification<Kv> namespacedSpec = KvSpecification.builder().build();
        BaseNamespacedSpecification<Kv> spy = Mockito.spy(namespacedSpec);

        Specification<Kv> result = policyExecutorService.buildNamespacedSpec(spy);

        assertNotNull(result);
        verify(spy).hasPermission(Set.of("default"), Set.of("development", "production"));
    }

    @Test
    @WithMockUser(authorities = {"@default/development:KV:READ", "@team-a/development:KV:READ"})
    void Given_UserWithDifferentNamespacesSameEnvironment_ShouldBuildSpecWithDeduplicatedValues() {
        BaseNamespacedSpecification<Kv> namespacedSpec = KvSpecification.builder().build();
        BaseNamespacedSpecification<Kv> spy = Mockito.spy(namespacedSpec);

        Specification<Kv> result = policyExecutorService.buildNamespacedSpec(spy);

        assertNotNull(result);
        verify(spy).hasPermission(Set.of("default", "team-a"), Set.of("development"));
    }

    @Test
    @WithMockUser(authorities = {"@default/development:SECRET:READ", "@team-a/staging:SECRET:READ"})
    void Given_UserWithMultipleNamespacesAndEnvironmentsForSecrets_ShouldBuildSpecWithCorrectFilters() {
        BaseNamespacedSpecification<Secret> namespacedSpec = SecretSpecification.builder().build();
        BaseNamespacedSpecification<Secret> spy = Mockito.spy(namespacedSpec);

        Specification<Secret> result = policyExecutorService.buildNamespacedSpecForSecrets(spy);

        assertNotNull(result);
        verify(spy).hasPermission(
                Set.of("default", "team-a"),
                Set.of("development", "staging")
        );
    }

    @Test
    @WithMockUser(authorities = {"@*:SECRET:READ"})
    void Given_UserWithWildcardNamespaceForSecrets_ShouldBuildSpecWithEmptyFilters() {
        BaseNamespacedSpecification<Secret> namespacedSpec = SecretSpecification.builder().build();
        BaseNamespacedSpecification<Secret> spy = Mockito.spy(namespacedSpec);

        Specification<Secret> result = policyExecutorService.buildNamespacedSpecForSecrets(spy);

        assertNotNull(result);
        verify(spy).hasPermission(Set.of(), Set.of());
    }

    @Test
    @WithMockUser(authorities = {"@default/*:SECRET:READ"})
    void Given_UserWithWildcardEnvironmentForSecrets_ShouldBuildSpecWithNamespaceOnly() {
        BaseNamespacedSpecification<Secret> namespacedSpec = SecretSpecification.builder().build();
        BaseNamespacedSpecification<Secret> spy = Mockito.spy(namespacedSpec);

        Specification<Secret> result = policyExecutorService.buildNamespacedSpecForSecrets(spy);

        assertNotNull(result);
        verify(spy).hasPermission(Set.of("default"), Set.of("*"));
    }

    @Test
    @WithMockUser(authorities = {"@*/development:SECRET:READ"})
    void Given_UserWithWildcardNamespaceButSpecificEnvironmentForSecrets_ShouldBuildSpecWithEnvironmentOnly() {
        BaseNamespacedSpecification<Secret> namespacedSpec = SecretSpecification.builder().build();
        BaseNamespacedSpecification<Secret> spy = Mockito.spy(namespacedSpec);

        Specification<Secret> result = policyExecutorService.buildNamespacedSpecForSecrets(spy);

        assertNotNull(result);
        verify(spy).hasPermission(Set.of(), Set.of());
    }

    @Test
    void Given_NullRootSpecificationForSecrets_ShouldReturnNull() {
        Specification<Object> result = policyExecutorService.buildNamespacedSpecForSecrets(null);

        assertNull(result);
    }

    @Test
    @WithMockUser(authorities = {})
    void Given_UserWithNoScopesForSecrets_ShouldBuildSpecWithEmptyFilters() {
        BaseNamespacedSpecification<Secret> namespacedSpec = SecretSpecification.builder().build();
        BaseNamespacedSpecification<Secret> spy = Mockito.spy(namespacedSpec);

        Specification<Secret> result = policyExecutorService.buildNamespacedSpecForSecrets(spy);

        assertNotNull(result);
        verify(spy).hasPermission(Set.of(), Set.of());
    }

    @Test
    @WithMockUser(authorities = {"invalid-scope-format"})
    void Given_UserWithInvalidScopeFormatForSecrets_ShouldBuildSpecWithEmptyFilters() {
        BaseNamespacedSpecification<Secret> namespacedSpec = SecretSpecification.builder().build();
        BaseNamespacedSpecification<Secret> spy = Mockito.spy(namespacedSpec);

        Specification<Secret> result = policyExecutorService.buildNamespacedSpecForSecrets(spy);

        assertNotNull(result);
        verify(spy).hasPermission(Set.of(), Set.of());
    }

    @Test
    @WithMockUser(authorities = {"@default/development:SECRET:READ", "@team-a/staging:SECRET:READ", "@*:SECRET:READ"})
    void Given_UserWithMixedWildcardAndSpecificScopesForSecrets_ShouldBuildSpecWithOnlySpecificFilters() {
        BaseNamespacedSpecification<Secret> namespacedSpec = SecretSpecification.builder().build();
        BaseNamespacedSpecification<Secret> spy = Mockito.spy(namespacedSpec);

        Specification<Secret> result = policyExecutorService.buildNamespacedSpecForSecrets(spy);

        assertNotNull(result);
        verify(spy).hasPermission(Set.of("default", "team-a"), Set.of("development", "staging"));
    }

    @Test
    @WithMockUser(authorities = {"@default/development:SECRET:READ", "@default/production:SECRET:READ"})
    void Given_UserWithSameNamespaceDifferentEnvironmentsForSecrets_ShouldBuildSpecWithDeduplicatedValues() {
        BaseNamespacedSpecification<Secret> namespacedSpec = SecretSpecification.builder().build();
        BaseNamespacedSpecification<Secret> spy = Mockito.spy(namespacedSpec);

        Specification<Secret> result = policyExecutorService.buildNamespacedSpecForSecrets(spy);

        assertNotNull(result);
        verify(spy).hasPermission(Set.of("default"), Set.of("development", "production"));
    }

    @Test
    @WithMockUser(authorities = {"@default/development:SECRET:READ", "@team-a/development:SECRET:READ"})
    void Given_UserWithDifferentNamespacesSameEnvironmentForSecrets_ShouldBuildSpecWithDeduplicatedValues() {
        BaseNamespacedSpecification<Secret> namespacedSpec = SecretSpecification.builder().build();
        BaseNamespacedSpecification<Secret> spy = Mockito.spy(namespacedSpec);

        Specification<Secret> result = policyExecutorService.buildNamespacedSpecForSecrets(spy);

        assertNotNull(result);
        verify(spy).hasPermission(Set.of("default", "team-a"), Set.of("development"));
    }

    @Test
    @WithMockUser(authorities = {"@default/development:SECRET:READ", "@team-a/staging:KV:READ"})
    void Given_UserWithMixedSecretAndKVScopes_ShouldBuildSpecWithOnlySecretFilters() {
        BaseNamespacedSpecification<Secret> namespacedSpec = SecretSpecification.builder().build();
        BaseNamespacedSpecification<Secret> spy = Mockito.spy(namespacedSpec);

        Specification<Secret> result = policyExecutorService.buildNamespacedSpecForSecrets(spy);

        assertNotNull(result);
        verify(spy).hasPermission(Set.of("default"), Set.of("development"));
    }

    @Test
    @WithMockUser(authorities = {"@default/development:KV:READ", "@team-a/staging:KV:WRITE"})
    void Given_UserWithOnlyKVScopes_ShouldBuildSpecWithEmptyFilters() {
        BaseNamespacedSpecification<Secret> namespacedSpec = SecretSpecification.builder().build();
        BaseNamespacedSpecification<Secret> spy = Mockito.spy(namespacedSpec);

        Specification<Secret> result = policyExecutorService.buildNamespacedSpecForSecrets(spy);

        assertNotNull(result);
        verify(spy).hasPermission(Set.of(), Set.of());
    }

    @Test
    @WithMockUser(authorities = {"@default/development:SECRET:READ", "@default/development:SECRET:WRITE"})
    void Given_UserWithMultipleSecretPermissionsSameScope_ShouldBuildSpecWithDeduplicatedValues() {
        BaseNamespacedSpecification<Secret> namespacedSpec = SecretSpecification.builder().build();
        BaseNamespacedSpecification<Secret> spy = Mockito.spy(namespacedSpec);

        Specification<Secret> result = policyExecutorService.buildNamespacedSpecForSecrets(spy);

        assertNotNull(result);
        verify(spy).hasPermission(Set.of("default"), Set.of("development"));
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

    Secret createSecret() {
        Namespace namespace = defaultNamespace();
        Environment environment = defaultEnvironment();
        Secret secret = new Secret();
        SecretVersion version = new SecretVersion();
        version.setValue("123");
        version.setVersion(1);

        secret.setName("Testing Secret");
        secret.setVersions(Set.of(version));
        secret.setNamespace(namespace);
        secret.setEnvironment(environment);

        return secretService.persist(secret);
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

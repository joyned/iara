package com.iara.core.service;

import com.iara.core.entity.Environment;
import com.iara.core.entity.Namespace;
import com.iara.core.entity.Secret;
import com.iara.core.entity.SecretVersion;
import com.iara.core.entity.specification.SecretSpecification;
import com.iara.core.exception.*;
import com.iara.utils.TestUtils;
import org.apache.commons.lang3.SerializationUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
@Transactional
public class SecretServiceTest {

    @Autowired
    SecretService secretService;

    @Autowired
    NamespaceService namespaceService;

    @Autowired
    EnvironmentService environmentService;

    @MockitoSpyBean
    SecretService spy;

    Environment environment;

    Namespace namespace;

    @BeforeEach
    void init() {
        environment = defaultEnvironment();
        namespace = defaultNamespace();
    }

    @Test
    @WithMockUser(username = "testing@email.com", authorities = {"@*:SECRET:WRITE"})
    void Given_ValidSecret_ShouldPersist() {
        Secret secret = createSecret("Testing Secret", "initial-value");

        Secret persisted = secretService.persist(secret);

        assertNotNull(persisted.getId());
        assertEquals("Testing Secret", persisted.getName());
        assertFalse(persisted.getVersions().isEmpty());
        assertEquals("initial-value", getFirst(persisted).getValue());
    }

    @Test
    @WithMockUser(username = "testing@email.com", authorities = {"@*:SECRET:WRITE"})
    void Given_ValidSecret_ShouldPersistWithVersion() {
        Secret secret = createSecret("Testing Secret", "initial-value");
        Secret persisted = spy.persist(secret);

        SecretVersion newVersion = new SecretVersion();
        newVersion.setValue("new-version-value");
        newVersion.setVersion(2);
        spy.addVersion(persisted.getId(), newVersion, false);

        assertNotNull(persisted.getId());
        verify(spy, atLeastOnce()).addVersion(any(), any(), any());
    }

    @Test
    @WithMockUser(username = "testing@email.com", authorities = {"@*:SECRET:WRITE"})
    void Given_ValidSecretWithEmptyVersions_ShouldPersist() {
        Secret secret = new Secret();
        secret.setName("Testing Secret");
        secret.setNamespace(namespace);
        secret.setEnvironment(environment);
        secret.setVersions(Set.of());

        Secret persisted = secretService.persist(secret);
        assertNotNull(persisted.getId());
        assertEquals("Testing Secret", persisted.getName());
        assertTrue(persisted.getVersions().isEmpty());
    }

    @Test
    @WithMockUser(username = "testing@email.com", authorities = {"@*:SECRET:WRITE"})
    void Given_ValidSecret_ShouldThrowWhenUpdatingName() {
        Secret secret = createSecret("Testing Secret", "initial-value");
        Secret persisted = secretService.persist(secret);

        Secret copy = SerializationUtils.clone(persisted);
        copy.setName("Updated Secret Name");
        assertThrows(OperationNotPermittedException.class, () -> secretService.persist(copy));
    }

    @Test
    @WithMockUser(username = "testing@email.com", authorities = {"@*:SECRET:WRITE"})
    void Given_InvalidSecretWithDuplicatedName_ShouldThrow() {
        Secret secret = createSecret("Testing Secret", "initial-value");
        secretService.persist(secret);

        Secret newSecret = createSecret("Testing Secret", "another-value");

        assertThrows(DuplicatedSecretException.class, () -> secretService.persist(newSecret));
    }

    @Test
    @WithMockUser(username = "testing@email.com", authorities = {"@*:SECRET:WRITE"})
    void Given_InvalidSecretWithNullName_ShouldThrow() {
        Secret secret = new Secret();
        secret.setName(null);
        secret.setNamespace(namespace);
        secret.setEnvironment(environment);
        secret.setVersions(Set.of(createSecretVersion("value")));

        assertThrows(RequiredParameterException.class, () -> secretService.persist(secret));
    }

    @Test
    @WithMockUser(username = "testing@email.com", authorities = {"@*:SECRET:WRITE"})
    void Given_InvalidSecretWithEmptyName_ShouldThrow() {
        Secret secret = new Secret();
        secret.setName("");
        secret.setNamespace(namespace);
        secret.setEnvironment(environment);
        secret.setVersions(Set.of(createSecretVersion("value")));

        assertThrows(RequiredParameterException.class, () -> secretService.persist(secret));
    }

    @Test
    @WithMockUser(username = "testing@email.com", authorities = {"@*:SECRET:WRITE"})
    void Given_InvalidSecretWithNoNamespace_ShouldThrow() {
        Secret secret = createSecret("Testing Secret", "value");
        secret.setNamespace(null);

        assertThrows(RequiredParameterException.class, () -> secretService.persist(secret));
    }

    @Test
    @WithMockUser(username = "testing@email.com", authorities = {"@*:SECRET:WRITE"})
    void Given_InvalidSecretWithNoEnvironment_ShouldThrow() {
        Secret secret = createSecret("Testing Secret", "value");
        secret.setEnvironment(null);

        assertThrows(RequiredParameterException.class, () -> secretService.persist(secret));
    }

    @Test
    @WithMockUser(username = "testing@email.com", authorities = {"@*:SECRET:WRITE"})
    void Given_InvalidSecretWithNoNamespaceAndEnvironment_ShouldThrow() {
        Secret secret = createSecret("Testing Secret", "value");
        secret.setNamespace(null);
        secret.setEnvironment(null);

        assertThrows(RequiredParameterException.class, () -> secretService.persist(secret));
    }

    @Test
    @WithMockUser(username = "testing@email.com", authorities = {"@*:SECRET:WRITE"})
    void Given_ValidId_ShouldDelete() {
        Secret secret = createSecret("Testing Secret", "value");
        Secret persisted = secretService.persist(secret);

        assertDoesNotThrow(() -> secretService.delete(persisted.getId()));
    }

    @Test
    @WithMockUser(username = "testing@email.com", authorities = {"@*:SECRET:WRITE"})
    void Given_NonExistingSecret_ShouldThrowsOnDelete() {
        assertThrows(SecretNotFoundException.class, () -> secretService.getSecretVersionValue(UUID.randomUUID().toString(), "version-id"));
    }

    @Test
    @WithMockUser(username = "testing@email.com", authorities = {"@default/development:SECRET:WRITE"})
    void Given_ValidSecretVersion_ShouldGetValue() throws IllegalAccessException {
        Secret secret = createSecret("Testing Secret", "secret-value");
        Secret persisted = secretService.persist(secret);
        SecretVersion version = getFirst(persisted);

        String value = secretService.getSecretVersionValue(persisted.getId(), version.getId());

        assertEquals("secret-value", value);
    }

    @Test
    @WithMockUser(username = "testing@email.com", authorities = {"@default/development:SECRET:WRITE"})
    void Given_DestroyedSecretVersion_ShouldThrowOnGetValue() {
        Secret secret = createSecret("Testing Secret", "secret-value");
        Secret persisted = secretService.persist(secret);
        SecretVersion version = getFirst(persisted);

        secretService.destroySecretVersion(persisted.getId(), version.getVersion());

        assertThrows(DestroyedSecretException.class, () ->
                secretService.getSecretVersionValue(persisted.getId(), version.getId()));
    }

    @Test
    @WithMockUser(username = "testing@email.com", authorities = {"@default/development:SECRET:WRITE"})
    void Given_ValidSecret_ShouldAddVersion() {
        Secret secret = createSecret("Testing Secret", "initial-value");
        Secret persisted = secretService.persist(secret);

        SecretVersion newVersion = new SecretVersion();
        newVersion.setValue("new-version-value");
        newVersion.setVersion(2);

        SecretVersion addedVersion = secretService.addVersion(persisted.getId(), newVersion, false);

        assertNotNull(addedVersion.getId());
        assertEquals("new-version-value", addedVersion.getValue());
        assertEquals(2, addedVersion.getVersion());
    }

    @Test
    @WithMockUser(username = "testing@email.com", authorities = {"@default/development:SECRET:WRITE"})
    void Given_ValidSecret_ShouldAddVersionWithDisablePast() {
        Secret secret = createSecret("Testing Secret", "initial-value");
        Secret persisted = secretService.persist(secret);

        SecretVersion newVersion = new SecretVersion();
        newVersion.setValue("new-version-value");
        newVersion.setVersion(2);

        SecretVersion addedVersion = secretService.addVersion(persisted.getId(), newVersion, true);

        assertNotNull(addedVersion.getId());
        assertTrue(getFirst(persisted).getDisabled()); // Previous version should be disabled
    }

    @Test
    @WithMockUser(username = "testing@email.com", authorities = {"@default/development:SECRET:WRITE"})
    void Given_ValidSecretVersion_ShouldDisable() {
        Secret secret = createSecret("Testing Secret", "initial-value");
        Secret persisted = secretService.persist(secret);
        SecretVersion version = getFirst(persisted);

        assertDoesNotThrow(() -> secretService.disableSecretVersion(persisted.getId(), version.getVersion()));

        assertDoesNotThrow(() -> secretService.disableSecretVersion(persisted.getId(), version.getVersion()));
    }

    @Test
    @WithMockUser(username = "testing@email.com", authorities = {"@default/development:SECRET:WRITE"})
    void Given_ValidSecretVersion_ShouldDestroy() {
        Secret secret = createSecret("Testing Secret", "initial-value");
        Secret persisted = secretService.persist(secret);
        SecretVersion version = getFirst(persisted);

        assertDoesNotThrow(() -> secretService.destroySecretVersion(persisted.getId(), version.getVersion()));

        assertThrows(DestroyedSecretException.class, () ->
                secretService.getSecretVersionValue(persisted.getId(), version.getId()));
    }

    @Test
    @WithMockUser(username = "testing@email.com", authorities = {"@default/development:SECRET:WRITE"})
    void Given_ValidSearch_ShouldFetchSecrets() {
        Secret secret = createSecret("Testing Secret", "initial-value");
        secretService.persist(secret);

        Pageable pageable = PageRequest.of(0, 10);
        Specification<Secret> spec = SecretSpecification.builder().build();
        Page<Secret> result = secretService.search(spec, pageable);

        assertFalse(result.getContent().isEmpty());
        assertTrue(result.getContent().stream()
                .anyMatch(s -> s.getName().equals("Testing Secret")));
    }

    @Test
    @WithMockUser(username = "testing@email.com", authorities = {"@other/development:SECRET:READ"})
    void Given_UserWithoutPermission_ShouldThrowOnPersist() {
        Secret secret = createSecret("Testing Secret", "value");

        assertThrows(OperationNotPermittedException.class, () -> secretService.persist(secret));
    }

    @Test
    @WithMockUser(username = "testing@email.com", authorities = {"@default/development:SECRET:WRITE"})
    void Given_UserWithoutPermission_ShouldThrowOnDelete() {
        Secret secret = createSecret("Testing Secret", "value");
        Secret persisted = secretService.persist(secret);

        TestUtils.switchToUser("testing@email.com", "@other/development:SECRET:WRITE");

        assertThrows(OperationNotPermittedException.class, () -> secretService.delete(persisted.getId()));
    }

    @Test
    @WithMockUser(username = "testing@email.com", authorities = {"@default/development:SECRET:WRITE"})
    void Given_UserWithoutPermission_ShouldThrowOnAddVersion() {
        Secret secret = createSecret("Testing Secret", "value");
        Secret persisted = secretService.persist(secret);

        SecretVersion newVersion = new SecretVersion();
        newVersion.setValue("new-value");
        newVersion.setVersion(2);

        TestUtils.switchToUser("testing@email.com", "@other/development:SECRET:WRITE");

        assertThrows(OperationNotPermittedException.class, () ->
                secretService.addVersion(persisted.getId(), newVersion, false));
    }

    private Secret createSecret(String name, String value) {
        Secret secret = new Secret();
        secret.setName(name);
        secret.setNamespace(namespace);
        secret.setEnvironment(environment);

        SecretVersion version = createSecretVersion(value);
        secret.setVersions(Set.of(version));

        return secret;
    }

    private SecretVersion createSecretVersion(String value) {
        SecretVersion version = new SecretVersion();
        version.setValue(value);
        version.setVersion(1);
        version.setDisabled(false);
        version.setDestroyed(false);
        return version;
    }

    Environment defaultEnvironment() {
        Namespace namespace = defaultNamespace();
        return environmentService.findByNamespace(namespace).getFirst();
    }

    Namespace defaultNamespace() {
        Optional<Namespace> namespaceOptional = namespaceService.findByName("default");
        return namespaceOptional.orElse(null);
    }

    SecretVersion getFirst(Secret secret) {
        Iterator<SecretVersion> iterator = secret.getVersions().iterator();
        return iterator.next();
    }

}
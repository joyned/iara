package com.iara.core.service;

import com.iara.core.entity.Environment;
import com.iara.core.entity.Kv;
import com.iara.core.entity.Namespace;
import com.iara.core.exception.DuplicatedKvException;
import com.iara.core.exception.KeyValueNotFoundException;
import com.iara.core.exception.RequiredParameterException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
@Transactional
public class KeyValueServiceTest {

    @Autowired
    KeyValueService keyValueService;

    @Autowired
    NamespaceService namespaceService;

    @Autowired
    EnvironmentService environmentService;

    @MockitoSpyBean
    KeyValueService spy;

    Environment environment;

    Namespace namespace;

    @BeforeEach
    void init() {
        environment = defaultEnvironment();
        namespace = defaultNamespace();
    }

    @Test
    @WithMockUser(username = "testing@email.com", authorities = {"@*:KV:READ"})
    void Given_ValidKv_ShouldPersist() {
        Kv kv = new Kv();

        kv.setKey("Testing Key");
        kv.setValue("Testing Value");
        kv.setNamespace(namespace);
        kv.setEnvironment(environment);

        Kv persisted = keyValueService.persist(kv);
        assertNotNull(persisted.getId());
        assertEquals("Testing Key", persisted.getKey());
        assertEquals("Testing Value", persisted.getValue());
    }

    @Test
    @WithMockUser(username = "testing@email.com", authorities = {"@*:KV:READ"})
    void Given_ValidKv_ShouldPersistWithHistory() {
        Kv kv = new Kv();

        kv.setKey("Testing Key");
        kv.setValue("Testing Value");
        kv.setNamespace(namespace);
        kv.setEnvironment(environment);
        Kv persisted = spy.persist(kv);

        persisted.setValue("Edited to History");
        spy.persist(kv);

        assertNotNull(persisted.getId());
        verify(spy, atLeastOnce()).persistHistory(any());
    }

    @Test
    @WithMockUser(username = "testing@email.com", authorities = {"@*:KV:READ"})
    void Given_ValidKvWithEmptyValue_ShouldPersist() {
        Kv kv = new Kv();

        kv.setKey("Testing Key");
        kv.setValue("");
        kv.setNamespace(namespace);
        kv.setEnvironment(environment);

        Kv persisted = keyValueService.persist(kv);
        assertNotNull(persisted.getId());
        assertEquals("Testing Key", persisted.getKey());
    }

    @Test
    @WithMockUser(username = "testing@email.com", authorities = {"@*:KV:READ"})
    void Given_ValidKvWithNullValue_ShouldPersist() {
        Kv kv = new Kv();

        kv.setKey("Testing Key");
        kv.setValue(null);
        kv.setNamespace(namespace);
        kv.setEnvironment(environment);

        Kv persisted = keyValueService.persist(kv);
        assertNotNull(persisted.getId());
        assertEquals("Testing Key", persisted.getKey());
    }

    @Test
    @WithMockUser(username = "testing@email.com", authorities = {"@*:KV:READ"})
    void Given_ValidKv_ShouldUpdate() {
        Kv kv = new Kv();

        kv.setKey("Testing Key");
        kv.setValue("");
        kv.setNamespace(namespace);
        kv.setEnvironment(environment);

        Kv persisted = keyValueService.persist(kv);

        persisted.setValue("Edited");

        Kv updated = keyValueService.persist(persisted);

        assertEquals(persisted.getId(), updated.getId());
        assertEquals("Edited", updated.getValue());
    }

    @Test
    @WithMockUser(username = "testing@email.com", authorities = {"@*:KV:READ"})
    void Given_InvalidKvWithDuplicatedKey_ShouldThrow() {
        Kv kv = new Kv();

        kv.setKey("Testing Key");
        kv.setValue("");
        kv.setNamespace(namespace);
        kv.setEnvironment(environment);

        keyValueService.persist(kv);

        Kv newKv = new Kv();
        newKv.setKey("Testing Key");
        newKv.setNamespace(namespace);
        newKv.setEnvironment(environment);

        assertThrows(DuplicatedKvException.class, () -> keyValueService.persist(newKv));
    }

    @Test
    @WithMockUser(username = "testing@email.com", authorities = {"@*:KV:READ"})
    void Given_InvalidKvWithNullKey_ShouldThrow() {
        Kv kv = new Kv();

        kv.setKey(null);
        kv.setValue("Testing Value");
        kv.setNamespace(namespace);
        kv.setEnvironment(environment);

        assertThrows(RequiredParameterException.class, () -> keyValueService.persist(kv));
    }

    @Test
    @WithMockUser(username = "testing@email.com", authorities = {"@*:KV:READ"})
    void Given_InvalidKvWithEmptyKey_ShouldThrow() {
        Kv kv = new Kv();

        kv.setKey("");
        kv.setValue("Testing Value");
        kv.setNamespace(namespace);
        kv.setEnvironment(environment);

        assertThrows(RequiredParameterException.class, () -> keyValueService.persist(kv));
    }

    @Test
    @WithMockUser(username = "testing@email.com", authorities = {"@*:KV:READ"})
    void Given_InvalidKvWithNoNamespace_ShouldThrow() {
        Kv kv = new Kv();

        kv.setKey("Testing Key");
        kv.setValue("Testing Value");
        kv.setNamespace(null);
        kv.setEnvironment(environment);

        assertThrows(RequiredParameterException.class, () -> keyValueService.persist(kv));
    }

    @Test
    @WithMockUser(username = "testing@email.com", authorities = {"@*:KV:READ"})
    void Given_InvalidKvWithNoEnvironment_ShouldThrow() {
        Kv kv = new Kv();

        kv.setKey("Testing Key");
        kv.setValue("Testing Value");
        kv.setNamespace(namespace);
        kv.setEnvironment(null);

        assertThrows(RequiredParameterException.class, () -> keyValueService.persist(kv));
    }

    @Test
    @WithMockUser(username = "testing@email.com", authorities = {"@*:KV:READ"})
    void Given_InvalidKvWithNoNamespaceAndEnvironment_ShouldThrow() {
        Kv kv = new Kv();

        kv.setKey("Testing Key");
        kv.setValue("Testing Value");
        kv.setNamespace(null);
        kv.setEnvironment(null);

        assertThrows(RequiredParameterException.class, () -> keyValueService.persist(kv));
    }

    @Test
    @WithMockUser(username = "testing@email.com", authorities = {"@*:KV:READ"})
    void Given_ValidId_ShouldDelete() {
        Kv kv = new Kv();

        kv.setKey("Testing Key");
        kv.setValue("Testing Value");
        kv.setNamespace(namespace);
        kv.setEnvironment(environment);

        Kv persisted = keyValueService.persist(kv);

        assertDoesNotThrow(() -> keyValueService.delete(persisted.getId()));
    }

    @Test
    @WithMockUser(username = "testing@email.com", authorities = {"@*:KV:READ"})
    void Given_NonExistingKv_ShouldThrows() {
        assertThrows(KeyValueNotFoundException.class, () -> keyValueService.history(UUID.randomUUID().toString()));
    }

    @Test
    @WithMockUser(username = "testing@email.com", authorities = {"@default/development:KV:WRITE"})
    void Given_ValidSearch_ShouldFetchByNamespaceEnvironmentAndKey() {
        Kv kv = new Kv();

        kv.setKey("key1");
        kv.setValue("First Value");
        kv.setNamespace(namespace);
        kv.setEnvironment(environment);

        Kv persisted = keyValueService.persist(kv);

        Kv fromGet = keyValueService.get("default", "development", "key1");

        assertEquals(persisted.getId(), fromGet.getId());
        assertEquals(persisted.getValue(), fromGet.getValue());
    }

    @Test
    void Given_NonValidSearch_ShouldThrows() {
        assertThrows(KeyValueNotFoundException.class, () -> keyValueService.get("aaaaaa", "bbbbbb", "key1"));
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

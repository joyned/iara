package com.iara.core.service;

import com.iara.core.entity.Environment;
import com.iara.core.entity.Namespace;
import com.iara.core.exception.InvalidNamespaceException;
import com.iara.core.exception.NamespaceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@Transactional
class NamespaceServiceTest {

    @Autowired
    NamespaceService namespaceService;

    @BeforeEach
    void init() {
        Namespace namespace = new Namespace();
        namespace.setName("Namespace Default Testing");

        Environment environment = new Environment();
        environment.setName("Environment Default Testing");
        environment.setDescription("Description for testing");

        namespace.setEnvironments(List.of(environment));

        namespaceService.persist(namespace);
    }

    @Test
    @WithMockUser(authorities = {"@*"})
    void Given_NoFilter_ShouldFetch() {
        Page<Namespace> namespaces = namespaceService.search(null, Pageable.unpaged());
        assertNotNull(namespaces);
        assertEquals(2, namespaces.getSize());
    }

    @Test
    void Given_ValidNamespace_ShouldPersist() {
        Namespace namespace = new Namespace();
        namespace.setName("Namespace 1");
        namespace.setDescription("Description for testing");

        Environment environment = new Environment();
        environment.setName("Environment 1");
        environment.setDescription("Description for testing");

        namespace.setEnvironments(List.of(environment));

        Namespace persisted = namespaceService.persist(namespace);

        assertNotNull(persisted.getId());
        assertEquals(namespace.getName(), persisted.getName());
    }

    @Test
    void Given_ValidNamespaceWithoutDescription_ShouldPersist() {
        Namespace namespace = new Namespace();
        namespace.setName("Namespace 1");

        Environment environment = new Environment();
        environment.setName("Environment 1");
        environment.setDescription("Description for testing");

        namespace.setEnvironments(List.of(environment));

        Namespace persisted = namespaceService.persist(namespace);

        assertNotNull(persisted.getId());
        assertEquals(namespace.getName(), persisted.getName());
    }

    @Test
    void Given_InvalidNamespaceWithEmptyEnvironment_ShouldThrow() {
        Namespace namespace = new Namespace();
        namespace.setName("Namespace 1");
        namespace.setDescription("Description for testing");

        assertThrows(InvalidNamespaceException.class, () -> namespaceService.persist(namespace));
    }

    @Test
    void Given_InvalidNamespaceWithEmptyName_ShouldThrow() {
        Namespace namespace = new Namespace();
        namespace.setName("");
        namespace.setDescription("Description for testing");

        assertThrows(InvalidNamespaceException.class, () -> namespaceService.persist(namespace));
    }

    @Test
    void Given_InvalidNamespaceWithNullName_ShouldThrow() {
        Namespace namespace = new Namespace();
        namespace.setName(null);
        namespace.setDescription("Description for testing");

        assertThrows(InvalidNamespaceException.class, () -> namespaceService.persist(namespace));
    }

    @Test
    void Given_ValidUpdate_ShouldPersist() {
        Namespace namespace = new Namespace();
        namespace.setName("Namespace 1");
        namespace.setDescription("Description for testing");

        Environment environment = new Environment();
        environment.setName("Environment 1");
        environment.setDescription("Description for testing");

        namespace.setEnvironments(List.of(environment));

        Namespace persisted = namespaceService.persist(namespace);

        persisted.setName("Namespace Updated");

        Namespace updated = namespaceService.persist(persisted);

        assertEquals(persisted.getId(), updated.getId());
        assertEquals(persisted.getName(), updated.getName());
    }

    @Test
    void Given_ExistingId_ShouldDelete() {
        Namespace namespace = new Namespace();
        namespace.setName("Namespace 1");
        namespace.setDescription("Description for testing");

        Environment environment = new Environment();
        environment.setName("Environment 1");
        environment.setDescription("Description for testing");

        namespace.setEnvironments(List.of(environment));

        Namespace persisted = namespaceService.persist(namespace);

        assertDoesNotThrow(() -> namespaceService.delete(persisted.getId()));
    }

    @Test
    void Given_NotExistingId_ShouldThrows() {
        assertThrows(NamespaceNotFoundException.class, () -> namespaceService.delete(UUID.randomUUID().toString()));
    }
}

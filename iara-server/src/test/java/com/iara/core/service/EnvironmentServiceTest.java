package com.iara.core.service;

import com.iara.core.entity.Environment;
import com.iara.core.entity.Namespace;
import com.iara.core.exception.OperationNotPermittedException;
import com.iara.core.exception.RequiredParameterException;
import net.bytebuddy.agent.builder.AgentBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
@Transactional
class EnvironmentServiceTest {

    @Autowired
    EnvironmentService environmentService;

    @Autowired
    NamespaceService namespaceService;

    @Test
    @WithMockUser(authorities = {"@*"})
    void Given_NoFilter_ShouldFetch() {
        Page<Environment> environments = environmentService.search(null, Pageable.unpaged());
        assertNotNull(environments);
        assertEquals(1, environments.getSize());
    }

    @Test
    void Given_ValidEnvironment_ShouldPersist() {
        Environment environment = new Environment();
        environment.setName("Environment Testing");
        environment.setNamespace(defaultNamespace());

        Environment newEnv = environmentService.persist(environment);
        assertNotNull(newEnv.getId());
        assertEquals("Environment Testing", newEnv.getName());
    }

    @Test
    void Given_InvalidEnvironmentWithEmptyName_ShouldThrow() {
        Environment environment = new Environment();
        environment.setName("");
        environment.setNamespace(defaultNamespace());

        assertThrows(RequiredParameterException.class, () -> environmentService.persist(environment));
    }

    @Test
    void Given_InvalidEnvironmentWithNullName_ShouldThrow() {
        Environment environment = new Environment();
        environment.setName(null);
        environment.setNamespace(defaultNamespace());

        assertThrows(RequiredParameterException.class, () -> environmentService.persist(environment));
    }

    @Test
    void Given_InvalidEnvironmentWithEmptyNamespace_ShouldThrow() {
        Environment environment = new Environment();
        environment.setName("Environment Testing");

        assertThrows(RequiredParameterException.class, () -> environmentService.persist(environment));
    }

    @Test
    void Given_ValidIdToDelete_ShouldThrows() {
        assertThrows(OperationNotPermittedException.class, () -> environmentService.delete(UUID.randomUUID().toString()));
    }

    @Test
    void Given_DefaultNamespace_ShouldFetchEnvironments() {
        List<Environment> environments = environmentService.findByNamespace(defaultNamespace());
        assertEquals(1, environments.size());
    }

    @Test
    void Given_ListOfEnvironments_ShouldDeleteList() {
        assertDoesNotThrow(() -> environmentService.deleteAll(List.of(defaultEnvironment())));
    }

    Namespace defaultNamespace() {
        Optional<Namespace> namespaceOptional = namespaceService.findByName("default");
        return namespaceOptional.orElse(null);
    }

    Environment defaultEnvironment() {
        Namespace namespace = defaultNamespace();
        return environmentService.findByNamespace(namespace).getFirst();
    }
}

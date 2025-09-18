package com.iara.core.service;

import com.iara.core.entity.ApplicationParams;
import com.iara.core.exception.OperationNotPermittedException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
@Transactional
public class ApplicationParamsServiceTest {

    @Autowired
    ApplicationParamsService applicationParamsService;

    @Test
    void Given_ExistingParam_ShouldFetch() {
        ApplicationParams applicationParams = applicationParamsService.findByKey("GOOGLE_SSO_ENABLED");
        assertNotNull(applicationParams);
    }

    @Test
    void Given_NonExistingParam_ShouldFetchAndReturnNull() {
        ApplicationParams applicationParams = applicationParamsService.findByKey("AAAAAAAAAAAAAAAAAA");
        assertNull(applicationParams);
    }

    @Test
    void Given_Search_ShouldThrow() {
        assertThrows(UnsupportedOperationException.class, () -> applicationParamsService.search(null, Pageable.unpaged()));
    }

    @Test
    void Given_Param_ShouldFetch() {
        ApplicationParams applicationParams = new ApplicationParams();
        applicationParams.setKey("TESTING");
        applicationParams.setValue("123");
        applicationParams.setSecure(true);
        ApplicationParams persisted = applicationParamsService.persist(applicationParams);
        assertNotNull(persisted.getId());
    }

    @Test
    void Given_Delete_ShouldThrow() {
        assertThrows(OperationNotPermittedException.class, () -> applicationParamsService.delete(UUID.randomUUID().toString()));
    }
}

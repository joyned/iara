package com.iara.core.service;

import com.iara.core.entity.*;
import com.iara.core.entity.specification.BaseNamespacedSpecification;
import org.springframework.data.jpa.domain.Specification;

public interface PolicyExecutorService {

    void validatePolicyRule(String rule);

    boolean hasPermissionAtNamespace(Namespace namespace);

    boolean hasPermissionAtNamespace(String scope, Namespace namespace);

    boolean hasPermissionAtEnvironment(Environment environment);

    boolean hasPermissionAtEnvironment(String scope, Environment environment);

    boolean hasWritePermissionInKV(Kv kv);

    boolean hasWritePermissionInSecret(Secret secret);

    boolean isAllNamespacesAndEnvironments(String scope);

    String getNamespaceFromScope(String scope);

    String getEnvironmentFromScope(String scope);

    <T> Specification<T> buildNamespacedSpec(Specification<T> root);

    <T> Specification<T> buildNamespacedSpecForSecrets(Specification<T> root);
}

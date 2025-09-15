package com.iara.core.service;

import com.iara.core.entity.Environment;
import com.iara.core.entity.Kv;
import com.iara.core.entity.Namespace;
import com.iara.core.entity.Policy;
import com.iara.core.entity.specification.BaseNamespacedSpecification;
import org.springframework.data.jpa.domain.Specification;

public interface PolicyExecutorService {

    void validatePolicyRule(String rule);

    boolean hasPermissionAtNamespace(Namespace namespace);

    boolean hasPermissionAtNamespace(String scope, Namespace namespace);

    boolean hasPermissionAtEnvironment(Environment environment);

    boolean hasPermissionAtEnvironment(String scope, Environment environment);

    boolean hasWritePermissionInKV(Kv kv);

    boolean isAllNamespacesAndEnvironments(String scope);

    String getNamespaceFromScope(String scope);

    String getEnvironmentFromScope(String scope);

    <T> Specification<T> buildNamespacedSpec(Specification<T> root);
}

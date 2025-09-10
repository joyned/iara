package com.iara.core.service;

import com.iara.core.entity.Environment;
import com.iara.core.entity.Kv;
import com.iara.core.entity.Namespace;
import com.iara.core.entity.Policy;

public interface PolicyExecutorService {

    void validatePolicyRule(String rule);

    boolean executePolicy(Policy policy, Namespace namespace, Environment environment, String resource);

    boolean hasPermissionAtNamespace(Namespace namespace);

    boolean hasPermissionAtNamespace(String scope, Namespace namespace);

    boolean hasPermissionAtEnvironment(Environment environment);

    boolean hasPermissionAtEnvironment(String scope, Environment environment);

    boolean hasWritePermissionInKV(Kv kv);

    boolean isAllNamespacesAndEnvironments(String scope);

    String getNamespaceFromScope(String scope);

    String getEnvironmentFromScope(String scope);
}

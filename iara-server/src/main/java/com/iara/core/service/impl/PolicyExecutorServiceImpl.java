package com.iara.core.service.impl;

import com.iara.core.entity.Environment;
import com.iara.core.entity.Kv;
import com.iara.core.entity.Namespace;
import com.iara.core.entity.Secret;
import com.iara.core.entity.specification.BaseNamespacedSpecification;
import com.iara.core.exception.InvalidPolicyException;
import com.iara.core.service.PolicyExecutorService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
public class PolicyExecutorServiceImpl implements PolicyExecutorService {

    @Override
    public boolean hasPermissionAtNamespace(Namespace namespace) {
        List<String> scopes = getScopeList();
        for (String scope : scopes) {
            if (hasPermissionAtNamespace(scope, namespace) || isAllNamespacesAndEnvironments(scope)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean hasPermissionAtNamespace(String scope, Namespace namespace) {
        String namespaceName = getNamespaceFromScope(scope);

        if (isAllNamespacesAndEnvironments(scope)) {
            return true;
        }

        if (StringUtils.isNotBlank(namespaceName)) {
            return namespaceName.equals(namespace.getName());
        }
        return false;
    }

    @Override
    public boolean hasPermissionAtEnvironment(Environment environment) {
        List<String> scopes = getScopeList();
        for (String scope : scopes) {
            if (hasPermissionAtEnvironment(scope, environment) || isAllNamespacesAndEnvironments(scope)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean hasPermissionAtEnvironment(String scope, Environment environment) {
        String envName = getEnvironmentFromScope(scope);

        if (isAllNamespacesAndEnvironments(scope)) {
            return true;
        }

        if (StringUtils.isNotBlank(envName)) {
            return envName.equals(environment.getName());
        }
        return false;
    }

    @Override
    public boolean hasWritePermissionInKV(Kv kv) {
        List<String> scopes = getScopeList();
        for (String scope : scopes) {
            if (isAllNamespacesAndEnvironments(scope)) {
                return true;
            }

            boolean hasPermNamespaceAndEnv =
                    hasPermissionAtEnvironment(scope, kv.getEnvironment()) && hasPermissionAtNamespace(scope, kv.getNamespace());
            boolean hasPermInKv = scope.split(":")[1].equals("KV");
            boolean isWrite = "WRITE".equals(scope.split(":")[scope.split(":").length - 1]);

            if (hasPermNamespaceAndEnv && hasPermInKv && isWrite) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean hasWritePermissionInSecret(Secret secret) {
        List<String> scopes = getScopeList();
        for (String scope : scopes) {
            if (isAllNamespacesAndEnvironments(scope)) {
                return true;
            }

            boolean hasPermNamespaceAndEnv =
                    hasPermissionAtEnvironment(scope, secret.getEnvironment()) && hasPermissionAtNamespace(scope, secret.getNamespace());
            boolean hasPermInKv = scope.split(":")[1].equals("KV");
            boolean isWrite = "WRITE".equals(scope.split(":")[scope.split(":").length - 1]);

            if (hasPermNamespaceAndEnv && hasPermInKv && isWrite) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isAllNamespacesAndEnvironments(String scope) {
        return scope.startsWith("@*");
    }

    @Override
    public String getNamespaceFromScope(String scope) {
        if (isAllNamespacesAndEnvironments(scope)) {
            return "@*";
        }

        if (scope.startsWith("@")) {
            return scope.split("/")[0].substring(1);
        }
        return null;
    }

    @Override
    public String getEnvironmentFromScope(String scope) {
        try {
            if (isAllNamespacesAndEnvironments(scope)) {
                return "@*";
            }
            if (scope.startsWith("@")) {
                return scope.split(":")[0].split("/")[1];
            }
        } catch (Exception e) {
            log.error("Failed to extract environment from scope {}", scope);
        }
        return null;
    }

    @Override
    public <T> Specification<T> buildNamespacedSpec(Specification<T> root) {
        if (Objects.isNull(root)) {
            return null;
        }

        Set<String> namespaces = new LinkedHashSet<>();
        Set<String> environments = new LinkedHashSet<>();
        List<String> scopes = getScopeList();
        for (String scope : scopes) {
            String namespace = getNamespaceFromScope(scope);
            String env = getEnvironmentFromScope(scope);
            if (StringUtils.isNotBlank(namespace) && !namespace.startsWith("@*")) {
                namespaces.add(namespace);
            }

            if (StringUtils.isNotBlank(env) && !env.startsWith("@*")) {
                environments.add(env);
            }
        }
        return root.and(((BaseNamespacedSpecification<T>) root).hasPermission(namespaces, environments));
    }

    @Override
    public <T> Specification<T> buildNamespacedSpecForSecrets(Specification<T> root) {
        if (Objects.isNull(root)) {
            return null;
        }

        Set<String> namespaces = new LinkedHashSet<>();
        Set<String> environments = new LinkedHashSet<>();
        List<String> scopes = getScopeList();

        for (String scope : scopes) {
            boolean isSecret = "SECRET".equals(scope.split(":")[1]);
            String namespace = getNamespaceFromScope(scope);
            String env = getEnvironmentFromScope(scope);
            if (StringUtils.isNotBlank(namespace) && !namespace.startsWith("@*") && isSecret) {
                namespaces.add(namespace);
            }

            if (StringUtils.isNotBlank(env) && !env.startsWith("@*") && isSecret) {
                environments.add(env);
            }
        }
        return root.and(((BaseNamespacedSpecification<T>) root).hasPermission(namespaces, environments));
    }

    @Override
    public void validatePolicyRule(String rule) {
        if (rule == null || rule.trim().isEmpty()) {
            throw new InvalidPolicyException("Policy cannot be null or empty");
        }

        String[] lines = rule.split("\n");

        for (int i = 0; i < lines.length; i++) {
            String line = lines[i].trim();

            if (line.isEmpty()) {
                continue;
            }

            String[] steps = line.split("\\s+");

            try {
                validateRule(steps);
            } catch (InvalidPolicyException e) {
                throw new InvalidPolicyException("Error on line " + (i + 1) + ": " + e.getMessage() + ". Policy is " + line);
            }
        }
    }

    protected void validateRule(String[] steps) {
        if (steps.length < 4) {
            throw new InvalidPolicyException("Rule too short. Minimum 4 tokens required");
        }

        if (!"ALLOW".equals(steps[0])) {
            throw new InvalidPolicyException("First token must be 'ALLOW'");
        }

        int permissionIndex = validatePermissions(steps);

        if (permissionIndex + 1 < steps.length && "AT".equals(steps[permissionIndex])) {
            validateAdministrativeAccess(steps, permissionIndex);
        } else if (permissionIndex + 1 < steps.length &&
                "IN".equals(steps[permissionIndex]) &&
                "AT".equals(steps[permissionIndex + 2])) {
            validateNamespaceAccess(steps, permissionIndex);
        } else {
            throw new InvalidPolicyException("Invalid rule structure. Expected format: 'ALLOW PERMISSION AT #RESOURCE' for administrative access or 'ALLOW PERMISSION IN [KV|SECRET] AT @NAMESPACE/ENV' for namespace access");
        }
    }

    protected int validatePermissions(String[] steps) {
        if ("READ".equals(steps[1]) && "AND".equals(steps[2]) && "WRITE".equals(steps[3])) {
            return 4;
        } else if ("READ".equals(steps[1]) && "WRITE".equals(steps[2]) && "AND".equals(steps[3])) {
            throw new InvalidPolicyException("Invalid permission format. Use 'READ AND WRITE' instead of 'READ WRITE AND'");
        } else if ("READ".equals(steps[1])) {
            return 2;
        } else if ("WRITE".equals(steps[1])) {
            return 2;
        } else {
            throw new InvalidPolicyException("Invalid permission. Must be 'READ', 'WRITE' or 'READ AND WRITE'");
        }
    }

    protected void validateAdministrativeAccess(String[] steps, int permissionIndex) {
        if (steps.length <= permissionIndex + 1) {
            throw new InvalidPolicyException("Missing resource after 'AT'");
        }

        String resource = steps[permissionIndex + 1];

        if (!resource.startsWith("#")) {
            throw new InvalidPolicyException("Administrative resource must start with '#'");
        }

        List<String> validResources = Arrays.asList("#NAMESPACES", "#USERS", "#POLICIES", "#ROLES", "#*");

        if (!validResources.contains(resource)) {
            throw new InvalidPolicyException("Invalid administrative resource: " + resource + ". Valid resources are: " + validResources);
        }

        if (steps.length > permissionIndex + 3) {
            throw new InvalidPolicyException("Unexpected tokens after resource: " +
                    String.join(" ", Arrays.copyOfRange(steps, permissionIndex + 3, steps.length)));
        }
    }

    protected void validateNamespaceAccess(String[] steps, int permissionIndex) {
        String resourceType = steps[permissionIndex + 1];
        if (!"KV".equals(resourceType) && !"SECRET".equals(resourceType)) {
            throw new InvalidPolicyException("Resource type must be 'KV' or 'SECRET' after 'IN'");
        }

        if (steps.length <= permissionIndex + 3) {
            throw new InvalidPolicyException("Missing namespace/environment after 'AT'");
        }

        String namespaceEnv = steps[permissionIndex + 3];

        if (!namespaceEnv.startsWith("@")) {
            throw new InvalidPolicyException("Namespace/environment must start with '@'");
        }

        if (!namespaceEnv.contains("/") && !namespaceEnv.contains("*")) {
            throw new InvalidPolicyException("Invalid namespace/environment format. Expected '@namespace/environment'");
        }

        if (steps.length > permissionIndex + 5) {
            throw new InvalidPolicyException("Unexpected tokens after namespace/environment: " +
                    String.join(" ", Arrays.copyOfRange(steps, permissionIndex + 5, steps.length)));
        }
    }

    private static List<String> getScopeList() {
        List<String> scopes = new ArrayList<>();
        SecurityContextHolder.getContext().getAuthentication().getAuthorities().forEach(grantedAuthority ->
                scopes.add(grantedAuthority.getAuthority()));
        return scopes;
    }
}

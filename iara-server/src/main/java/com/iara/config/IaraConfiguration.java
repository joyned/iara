package com.iara.config;

import com.iara.core.entity.*;
import com.iara.core.service.*;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class IaraConfiguration {

    private final UserService userService;
    private final NamespaceService namespaceService;
    private final PolicyService policyService;
    private final RoleService roleService;
    private final ApplicationParamsService applicationParamsService;
    private final PasswordEncoder passwordEncoder;

    @PostConstruct
    public void init() {
        log.info("Initiating Iara configurations...");
        if (!checkIfAlreadyInitialized()) {
            log.info("Going to configure first access...");
            createNamespaceAndEnvironment();
            log.info("Default Namespace & Environment created.");
            Role adminRole = createAdminPolicyAndRole();
            log.info("Default Admin Policy & Role created.");
            createAdminUser(adminRole);
            log.info("Default Admin User created.");
            createDefaultApplicationParams();
            log.info("Application Parameters created.");
        } else {
            log.info("Iara configurations already exists.");
        }
    }

    protected boolean checkIfAlreadyInitialized() {
        String adminEmail = StringUtils.isBlank(System.getenv("IARA_ADMIN_EMAIL")) ? "admin@example.com" : System.getenv("IARA_ADMIN_EMAIL");
        return userService.findByEmail(adminEmail).isPresent();
    }

    protected void createNamespaceAndEnvironment() {
        Namespace namespace = new Namespace();
        Environment environment = new Environment();

        namespace.setName("default");
        namespace.setDescription("Default namespace for Iara Configuration Server.");

        environment.setName("development");
        environment.setDescription("Default environment for Iara Configuration Server");

        namespace.setEnvironments(List.of(environment));

        namespaceService.persist(namespace);
    }

    protected Role createAdminPolicyAndRole() {
        Policy policy = new Policy();

        policy.setName("Admin");
        policy.setDescription("Admin policy with full access.");
        policy.setRule("ALLOW READ AND WRITE IN KV AT @*\nALLOW READ AND WRITE IN SECRET AT @*\nALLOW READ AND WRITE AT #*");

        Policy persisted = policyService.persist(policy);

        Role role = new Role();
        role.setName("Admin");
        role.setDescription("Admin role with full access.");
        role.setPolicies(List.of(persisted));

        return roleService.persist(role);
    }

    protected void createAdminUser(Role adminRole) {
        String adminEmail = StringUtils.isBlank(System.getenv("IARA_ADMIN_EMAIL")) ? "admin@example.com" : System.getenv("IARA_ADMIN_EMAIL");
        String adminPassword = StringUtils.isBlank(System.getenv("IARA_ADMIN_PASSWORD")) ? "iara" : System.getenv("IARA_ADMIN_PASSWORD");
        User admin = new User();
        admin.setName("Administrator");
        admin.setEmail(adminEmail);
        admin.setPassword(passwordEncoder.encode(adminPassword));
        admin.setIsSSO(false);
        admin.setRoles(List.of(adminRole));
        userService.persist(admin);
    }

    protected void createDefaultApplicationParams() {
        List<ApplicationParams> applicationParams = new ArrayList<>();
        applicationParams.add(new ApplicationParams(null, "GOOGLE_SSO_ENABLED", Boolean.FALSE.toString(), false));
        applicationParams.add(new ApplicationParams(null, "GOOGLE_SSO_CLIENT_ID", null, false));
        applicationParams.add(new ApplicationParams(null, "GOOGLE_SSO_CLIENT_SECRET", null, true));

        applicationParams.forEach(applicationParamsService::persist);
    }

}

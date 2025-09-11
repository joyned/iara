import type { Policy } from "../types/Policy";
import type { Role } from "../types/Role";

const hasAccessTo = (role: Role, menuItem: string) => {
    return role.policies.some((policy: Policy) => policy.rule.includes(menuItem) || policy.rule.includes("#*"));
}

const hasAccessToBatch = (roles: Role[], menuItem: string) => {
    if (!menuItem) return true;
    return roles.some((role: Role) => role.policies.some((policy: Policy) => policy.rule.includes(menuItem) || policy.rule.includes("#*")));
}

export { hasAccessTo, hasAccessToBatch }
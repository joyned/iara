import type { Page } from "../types/Page";
import type { Role } from "../types/Role";
import type { BaseService } from "./BaseService";
import { HttpService } from "./HttpService";

export class RoleService implements BaseService<Role> {
    search(params?: Partial<Role> | undefined, page?: number, size?: number): Promise<Page<Role>> {
        return HttpService.doGet('v1/role', params, page, size);
    }
    persist(entity: Role): Promise<Role> {
        return HttpService.doPost('v1/role', entity);
    }
    delete(id: string): Promise<void> {
        return HttpService.doDelete(`v1/role/${id}`);
    }

}
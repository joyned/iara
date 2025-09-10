import type { Page } from "../types/Page";
import type { Policy } from "../types/Policy";
import type { BaseService } from "./BaseService";
import { HttpService } from "./HttpService";

export class PolicyService implements BaseService<Policy> {
    search(params?: Partial<Policy> | undefined, page?: number, size?: number): Promise<Page<Policy>> {
        return HttpService.doGet('v1/policy', params, page, size);
    }
    persist(entity: Policy): Promise<Policy> {
        return HttpService.doPost('v1/policy', entity);
    }
    delete(id: string): Promise<void> {
        return HttpService.doDelete(`v1/policy/${id}`);
    }

}
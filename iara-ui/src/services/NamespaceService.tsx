import type { Namespace } from "../types/Namespace";
import type { Page } from "../types/Page";
import type { BaseService } from "./BaseService";
import { HttpService } from "./HttpService";

export class NamespaceService implements BaseService<Namespace> {
    search(params?: Partial<Namespace> | undefined, page?: number, size?: number): Promise<Page<Namespace>> {
        return HttpService.doGet('v1/namespace', params, page, size);
    }
    persist(entity: Namespace): Promise<Namespace> {
        return HttpService.doPost('v1/namespace', entity);
    }
    delete(id: string): Promise<void> {
        return HttpService.doDelete(`v1/namespace/${id}`);
    }

}
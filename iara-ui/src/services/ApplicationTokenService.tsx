import type { ApplicationToken } from "../types/ApplicationToken";
import type { Page } from "../types/Page";
import type { BaseService } from "./BaseService";
import { HttpService } from "./HttpService";

export class ApplicationTokenService implements BaseService<ApplicationToken> {
    search(params?: Partial<ApplicationToken> | undefined, page?: number, size?: number): Promise<Page<ApplicationToken>> {
        return HttpService.doGet('v1/application-token', params, page, size);
    }
    persist(entity: ApplicationToken): Promise<ApplicationToken> {
        return HttpService.doPost('v1/application-token', entity);
    }
    delete(id: string): Promise<void> {
        return HttpService.doDelete(`v1/application-token/${id}`);
    }

}
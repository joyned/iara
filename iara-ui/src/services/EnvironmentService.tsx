import type { Environment } from "../types/Environment";
import type { Page } from "../types/Page";
import type { BaseService } from "./BaseService";
import { HttpService } from "./HttpService";

export class EnvironmentService implements BaseService<Environment> {
    search(params?: Partial<Environment> | undefined, page?: number, size?: number): Promise<Page<Environment>> {
        return HttpService.doGet('v1/environment', params, page, size);
    }
    persist(): Promise<Environment> {
        throw new Error("Method not implemented.");
    }
    delete(): Promise<void> {
        throw new Error("Method not implemented.");
    }

}
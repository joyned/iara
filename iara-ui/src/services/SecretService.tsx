import type { Page } from "../types/Page";
import type { Secret } from "../types/Secret";
import type { SecretVersion } from "../types/SecretVersion";
import type { BaseService } from "./BaseService";
import { HttpService } from "./HttpService";

export class SecretService implements BaseService<Secret> {
    search(params?: Partial<Secret> | undefined, page?: number, size?: number): Promise<Page<Secret>> {
        return HttpService.doGet('v1/secret', params, page, size);
    }
    persist(entity: Secret): Promise<Secret> {
        return HttpService.doPost('v1/secret', entity);
    }
    delete(id: string): Promise<void> {
        return HttpService.doDelete(`v1/secret/${id}`);
    }
    getSecretValue(secretId: string, secretVersionId: string): Promise<string> {
        return HttpService.doGet(`v1/secret/${secretId}/${secretVersionId}`);
    }
    addSecretVersion(secretId: string, version: SecretVersion, disablePastVersion: boolean): Promise<SecretVersion> {
        return HttpService.doPut(`v1/secret/version/${secretId}`, version, { "Iara-Disable-Past-Version": disablePastVersion });
    }

}
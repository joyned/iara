import type { KeyValue } from "../types/KeyValue";
import type { KeyValueHistory } from "../types/KeyValueHistory";
import type { Page } from "../types/Page";
import type { BaseService } from "./BaseService";
import { HttpService } from "./HttpService";


export class KeyValueService implements BaseService<KeyValue> {
    search(params?: Partial<KeyValue>, page?: number, size?: number): Promise<Page<KeyValue>> {
        return HttpService.doGet('v1/kv', params, page, size);
    }
    persist(entity: KeyValue): Promise<KeyValue> {
        return HttpService.doPost('v1/kv', entity);
    }
    delete(id: string): Promise<void> {
        return HttpService.doDelete(`v1/kv/${id}`);
    }
    history(id: string): Promise<KeyValueHistory[]> {
        return HttpService.doGet(`v1/kv/${id}/history`);
    }

}
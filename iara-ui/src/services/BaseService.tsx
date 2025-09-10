import type { Page } from "../types/Page";

export interface BaseService<T> {
    search(params?: Partial<T>, page?: number, size?: number): Promise<Page<T>>;
    persist(entity: T): Promise<T>;
    delete(id: string): Promise<void>;
}
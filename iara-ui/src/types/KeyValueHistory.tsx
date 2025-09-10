import type { KeyValue } from "./KeyValue";

export interface KeyValueHistory {
    id: string;
    keyValue: KeyValue;
    value: string;
    updatedAt: Date;
    user: string;
}
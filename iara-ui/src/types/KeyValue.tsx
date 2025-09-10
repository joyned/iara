import type { Environment } from "./Environment";
import type { Namespace } from "./Namespace";

export interface KeyValue {
    id?: string;
    key: string;
    value?: string;
    environment: Environment;
    namespace: Namespace;
}
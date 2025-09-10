import type { Environment } from "./Environment";
import type { Namespace } from "./Namespace";
import type { SecretVersion } from "./SecretVersion";

export interface Secret {
    id?: string;
    name: string;
    versions: SecretVersion[];
    environment: Environment;
    namespace: Namespace;
}
import type { Policy } from "./Policy";

export interface Role {
    id?: string;
    name: string;
    description?: string;
    policies: Policy[];
}
import type { Policy } from "./Policy";

export interface ApplicationToken {
    id?: string;
    name: string;
    token?: string;
    createdAt?: Date;
    createdBy?: string;
    expiresAt?: Date;
    policy?: Policy;
}
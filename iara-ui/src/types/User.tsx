import type { Role } from "./Role";

export interface User {
    id?: string;
    name: string;
    email: string;
    picture?: string;
    roles: Role[];
    isSSO: boolean;
    otpEnabled: boolean;
}
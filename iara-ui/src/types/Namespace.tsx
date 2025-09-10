import type { Environment } from "./Environment";

export interface Namespace {
    id?: string;
    name: string;
    description?: string;
    environments?: Environment[];
}
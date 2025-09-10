export interface SecretVersion {
    id?: string;
    version: number;
    value: string;
    disabled: boolean;
    destroyed: boolean;
}
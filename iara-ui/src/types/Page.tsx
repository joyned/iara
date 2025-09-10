export interface Page<T> {
    content: T[];
    page: number;
    totalElements: number;
    totalPages: number;
}
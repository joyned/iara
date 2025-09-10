import { toast } from "react-toastify";

export class HttpService {
    static async doGet<T>(uri: string, params?: any, page?: number, size?: number): Promise<T> {
        return fetch(this.getUrl(uri, params, page, size), {
            headers: {
                'Authorization': `Bearer ${localStorage.getItem('access_token')}`
            }
        })
            .then(async (response) => {
                if (response.ok) {
                    if (response.headers.get('Content-Type') != 'application/json') {
                        return response.text();
                    }
                    return response.json();
                }
                const body = await response.json();
                toast.error(body.message);
                return Promise.reject(response)
            });
    }

    static async doPost<T>(uri: string, body: any): Promise<T> {
        return fetch(this.getUrl(uri), {
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${localStorage.getItem('access_token')}`
            },
            method: 'POST',
            body: JSON.stringify(body)
        }).then(async (response) => {
            if (response.status === 204) {
                return;
            }
            if (response.ok) {
                return response.json();
            }
            const body = await response.json();
            toast.error(body.message);
            return Promise.reject(response)
        });
    }

    static async doPut<T>(uri: string, body: T, headers?: any): Promise<T> {
        return fetch(this.getUrl(uri), {
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${localStorage.getItem('access_token')}`,
                ...headers
            },
            method: 'PUT',
            body: JSON.stringify(body)
        }).then(async (response) => {
            if (response.status === 204) {
                return;
            }
            if (response.ok) {
                return response.json();
            }
            const body = await response.json();
            toast.error(body.message);
            return Promise.reject(response)
        });
    }

    static async doDelete<T>(uri: string): Promise<T> {
        return fetch(this.getUrl(uri), {
            method: 'DELETE',
            headers: {
                'Authorization': `Bearer ${localStorage.getItem('access_token')}`
            }
        }).then(async (response) => {
            if (response.status === 204) {
                return;
            }
            if (response.ok) {
                return response.json();
            }
            const body = await response.json();
            toast.error(body.message);
            return Promise.reject(response)
        });
    }

    private static getUrl(uri: string, params?: any, page?: number, size?: number) {
        const search = new URLSearchParams();
        if (params) {
            const entries = new Map(Object.entries(params));
            entries.forEach((value, key) => {
                if (value instanceof Object) {
                    search.set(key, (value as any).id)
                } else {
                    search.set(key, String(value));
                }
            });
        }

        if (page) {
            search.set('page', String(page));
        }

        if (size) {
            search.set('limit', String(size));
        }

        const finalUrl = new URL(import.meta.env.VITE_API_URL + "/" + uri);
        finalUrl.search = search.toString();
        return finalUrl.toString();
    }
}
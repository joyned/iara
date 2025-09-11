export { };

declare global {
    interface Window {
        __APP_ENV__: {
            VITE_API_URL: string;
            [key: string]: string;
        };
    }
}
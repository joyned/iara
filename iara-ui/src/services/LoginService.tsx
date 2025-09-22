import { HttpService } from "./HttpService";

export class LoginService {
    async doLogin(email: string, password: string): Promise<any> {
        const response = await fetch(HttpService.getUrl('v1/authentication'), {
            headers: {
                'Content-Type': 'application/json',
            },
            credentials: "include",
            method: 'POST',
            body: JSON.stringify({ email: email, password: password })
        });
        if (response.status === 204) {
            return Promise.resolve();
        }
        return await Promise.reject(response);
    }

    isGoogleSSOEnabled(): Promise<boolean> {
        return HttpService.doGet('v1/authentication/google-sso');
    }
}
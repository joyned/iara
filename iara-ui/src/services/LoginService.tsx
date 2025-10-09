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
        if (response.status === 200) {
            const body = await response.json();
            return Promise.resolve(body);
        }
        return await Promise.reject(response);
    }

    async verifyOtp(code: string, session: string) {
        const response = await fetch(HttpService.getUrl('v1/authentication/otp-verify'), {
            headers: {
                'Content-Type': 'application/json',
            },
            credentials: "include",
            method: 'POST',
            body: JSON.stringify({ code: code, session: session })
        });
        if (response.status === 204) {
            return Promise.resolve();
        }
        return await Promise.reject(response);
    }

    async doLoginGoogleSSO(code: string) {
        const response = await fetch(HttpService.getUrl('v1/authentication/google-sso', { code: code, redirect_uri: window.location.origin }), {
            headers: {
                'Content-Type': 'application/json',
            },
            credentials: "include",
            method: 'POST',
        });
        if (response.status === 204) {
            return Promise.resolve();
        }
        return await Promise.reject(response);
    }

    async doLogout() {
        const response = await fetch(HttpService.getUrl('v1/authentication/logout', {}), {
            headers: {
                'Content-Type': 'application/json',
            },
            credentials: "include",
            method: 'POST',
        });
        if (response.status === 204) {
            return Promise.resolve();
        }
        return await Promise.reject(response);
    }

    isGoogleSSOEnabled(): Promise<string> {
        return HttpService.doGet('v1/authentication/google-sso');
    }
}
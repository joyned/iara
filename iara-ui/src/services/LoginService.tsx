import type { Authentication } from "../types/Authentication";
import { HttpService } from "./HttpService";

export class LoginService {
    doLogin(email: string, password: string): Promise<Authentication> {
        return HttpService.doPost('v1/authentication', { email: email, password: password });
    }
}
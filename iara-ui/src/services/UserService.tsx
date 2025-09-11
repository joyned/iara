import type { Page } from "../types/Page";
import type { User } from "../types/User";
import type { BaseService } from "./BaseService";
import { HttpService } from "./HttpService";

export class UserService implements BaseService<User> {

    search(params?: Partial<User> | undefined, page?: number, size?: number): Promise<Page<User>> {
        return HttpService.doGet('v1/user', params, page, size);
    }
    persist(entity: User): Promise<User> {
        return HttpService.doPost('v1/user', entity);
    }
    delete(id: string): Promise<void> {
        return HttpService.doDelete(`v1/user/${id}`);
    }
    resetPassword(id: string): Promise<any> {
        return HttpService.doPost(`v1/user/${id}/reset-password`, {});
    }
    me(): Promise<User> {
        return HttpService.doGet('v1/user/me');
    }
    changePassword(old: string, newPwd: string): Promise<void> {
        return HttpService.doPost('v1/user/change-password', { oldPassword: old, newPassword: newPwd });
    }

}
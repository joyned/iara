import type { ApplicationParams } from "../types/ApplicationParams";
import { HttpService } from "./HttpService";

export class ApplicationParamsService {
    find(key: string): Promise<ApplicationParams> {
        return HttpService.doGet(`v1/params/${key}`)
    }

    persist(entity: ApplicationParams): Promise<ApplicationParams> {
        return HttpService.doPost('v1/params', entity);
    }
}
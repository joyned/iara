import { useEffect, useState } from "react";
import { useNavigate } from "react-router";
import Button from "../components/Button";
import Input from "../components/Input";
import ListItem from "../components/ListItem";
import { useEnvironment } from "../providers/EnvironmentProvider";
import { useLoading } from "../providers/LoadingProvider";
import { useNamespace } from "../providers/NamespaceProvider";
import { SecretService } from "../services/SecretService";
import type { Page } from "../types/Page";
import type { Secret } from "../types/Secret";
import { uuid } from "../utils/UUID";

export default function SecretsPage() {
    const navigate = useNavigate();
    const service = new SecretService();

    const [secrets, setSecrets] = useState<Secret[]>([]);

    const { namespace } = useNamespace();
    const { environment } = useEnvironment();

    const { setLoading } = useLoading();

    const search = (params?: Partial<Secret>, page?: number) => {
        if (!params) {
            params = { namespace: namespace, environment: environment };
        }

        setLoading(true);
        service.search(params, page).then((res: Page<Secret>) => {
            setSecrets(res.content);
        }).finally(() => setLoading(false));
    }

    useEffect(() => {
        search()
    }, []);

    return (
        <>
            <div className="flex flex-col gap-5">
                <h1>Secrets</h1>
                <div className="flex justify-between">
                    <Input placeholder="Search" />
                    <Button type="button" className="w-[150px]" onClick={() => navigate(`/secrets/new`)}>Create</Button>
                </div>
                <div className="flex flex-col gap-4">
                    {secrets.length > 0 && secrets.map((secret: Secret) => {
                        return (
                            <ListItem name={secret.name} onClick={() => navigate(`/secrets/${secret.id}`)} key={uuid()} />
                        )
                    })}
                </div>
            </div>
        </>
    )
}
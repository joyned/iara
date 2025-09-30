import { useEffect, useState } from "react";
import { useNavigate } from "react-router";
import TableList from "../components/TableList";
import { useEnvironment } from "../providers/EnvironmentProvider";
import { useLoading } from "../providers/LoadingProvider";
import { useNamespace } from "../providers/NamespaceProvider";
import { SecretService } from "../services/SecretService";
import type { Page } from "../types/Page";
import type { Secret } from "../types/Secret";

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
            <TableList title="secrets" data={secrets} dataLabel="name"
                onCreate={() => navigate(`/secrets/new`)}
                onEdit={(id: string) => navigate(`/secrets/${id}`)} />
        </>
    )
}
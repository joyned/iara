import { useEffect, useState } from "react";
import { useNavigate } from "react-router";
import Button from "../components/Button";
import Card from "../components/Card";
import ListItem from "../components/ListItem";
import NamespaceEnvironment from "../components/NamespaceEnvironment";
import { useEnvironment } from "../providers/EnvironmentProvider";
import { useLoading } from "../providers/LoadingProvider";
import { useNamespace } from "../providers/NamespaceProvider";
import { SecretService } from "../services/SecretService";
import type { Page } from "../types/Page";
import type { Secret } from "../types/Secret";
import Pageable from "../components/Pageable";
import { uuid } from "../utils/UUID";

export default function SecretsPage() {
    const navigate = useNavigate();
    const service = new SecretService();

    const [secrets, setSecrets] = useState<Secret[]>([]);
    const [page, setPage] = useState<number>(0);
    const [totalPages, setTotalPages] = useState<number>(0);

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
            setPage(res.pageable.pageNumber);
            setTotalPages(res.totalPages);
        }).finally(() => setLoading(false));
    }

    useEffect(() => {
        search()
    }, []);

    return (
        <>
            <NamespaceEnvironment />
            <Card title="Secrets">
                {secrets.length > 0 && secrets.map((secret: Secret) => {
                    return (
                        <ListItem name={secret.name} onClick={() => navigate(`/secrets/${secret.id}`)} key={uuid()} />
                    )
                })}
                <div className="flex justify-between mt-5">
                    <div className="flex gap-2">
                        <Button type="button" onClick={() => navigate(`/secrets/new`)}>Create</Button>
                    </div>
                    {secrets.length > 0 && <Pageable totalPages={totalPages} page={page} onPage={(page: number) => search(undefined, page)}></Pageable>}
                </div>
            </Card>
        </>
    )
}
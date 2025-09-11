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

    const [secrets, setSecrets] = useState<Secret[]>();
    const [page, setPage] = useState<number>(0);
    const [totalPages, setTotalPages] = useState<number>(0);

    const { namespace } = useNamespace();
    const { environment } = useEnvironment();

    const { setLoading } = useLoading();

    useEffect(() => {
        setLoading(true);
        service.search({ environment: environment, namespace: namespace }).then((res: Page<Secret>) => {
            setSecrets(res.content);
            setPage(res.page);
            setTotalPages(res.totalPages);
        }).finally(() => setLoading(false));
    }, []);

    return (
        <>
            <NamespaceEnvironment />
            <Card title="Secrets">
                {secrets && secrets.map((secret: Secret) => {
                    return (
                        <ListItem name={secret.name} onClick={() => navigate(`/secrets/${secret.id}`)} key={uuid()} />
                    )
                })}
                <div className="flex justify-between mt-5">
                    <div className="flex gap-2">
                        <Button type="button" onClick={() => navigate(`/secrets/new`)}>Create</Button>
                    </div>
                    <Pageable totalPages={totalPages} page={page}></Pageable>
                </div>
            </Card>
        </>
    )
}
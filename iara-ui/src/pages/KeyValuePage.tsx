import { useEffect, useState } from "react";
import { useNavigate } from "react-router";
import TableList from "../components/TableList";
import { useEnvironment } from "../providers/EnvironmentProvider";
import { useLoading } from "../providers/LoadingProvider";
import { useNamespace } from "../providers/NamespaceProvider";
import eventBus from "../services/EventBusService";
import { KeyValueService } from "../services/KeyValueService";
import type { KeyValue } from "../types/KeyValue";
import type { Page } from "../types/Page";

export default function KeyValuePage() {
    const navigate = useNavigate();
    const kvService = new KeyValueService();

    const [keyValues, setKeyValues] = useState<KeyValue[]>([]);

    const { namespace } = useNamespace();
    const { environment } = useEnvironment();

    const { setLoading } = useLoading();

    const search = (params?: Partial<KeyValue>, page?: number) => {
        if (!params) {
            params = { namespace: namespace, environment: environment };
        }

        setLoading(true);
        kvService.search(params, page).then((res: Page<KeyValue>) => {
            setKeyValues(res.content);
        }).finally(() => setLoading(false));
    }

    useEffect(() => {
        search({ namespace: namespace, environment: environment });

        const listenToNamespaceEnvChange = (data: any) => {
            search({ namespace: data.namespace, environment: data.environment });
        }

        eventBus.on('namespaceEnvironmentChange', listenToNamespaceEnvChange);

        return () => {
            eventBus.off('namespaceEnvironmentChange', listenToNamespaceEnvChange);
        };
    }, [environment, namespace])


    return (
        <>
            <TableList title="kv entries" data={keyValues} dataLabel="key"
                onCreate={() => navigate(`/kv/new`)}
                onEdit={(id: string) => navigate(`/kv/${id}`)} />
        </>
    )
}
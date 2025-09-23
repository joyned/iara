import { useEffect, useState } from "react";
import { useNavigate } from "react-router";
import TableList from "../components/TableList";
import { useLoading } from "../providers/LoadingProvider";
import { NamespaceService } from "../services/NamespaceService";
import type { Namespace } from "../types/Namespace";
import type { Page } from "../types/Page";

export default function NamespacesPage() {
    const navigate = useNavigate();
    const service = new NamespaceService();
    const { setLoading } = useLoading();

    const [namespaces, setNamespaces] = useState<Namespace[]>([]);

    useEffect(() => {
        setLoading(true);
        service.search().then((res: Page<Namespace>) => {
            setNamespaces(res.content);
        }).finally(() => setLoading(false));
    }, [setLoading])

    return (
        <>
            <TableList title="Namespaces" data={namespaces} dataLabel="name"
                onCreate={() => navigate(`/admin/namespaces/new`)}
                onEdit={(id: string) => navigate(`/admin/namespaces/${id}`)} />
        </>
    )
}
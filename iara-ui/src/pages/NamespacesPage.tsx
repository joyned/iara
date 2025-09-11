import { useEffect, useState } from "react";
import Card from "../components/Card";
import { NamespaceService } from "../services/NamespaceService";
import type { Namespace } from "../types/Namespace";
import ListItem from "../components/ListItem";
import type { Page } from "../types/Page";
import { useNavigate } from "react-router";
import Button from "../components/Button";
import { useLoading } from "../providers/LoadingProvider";
import { uuid } from "../utils/UUID";

export default function NamespacesPage() {
    const navigate = useNavigate();
    const service = new NamespaceService();
    const { setLoading } = useLoading();

    const [namespaces, setNamespaces] = useState<Namespace[]>();

    useEffect(() => {
        setLoading(true);
        service.search().then((res: Page<Namespace>) => {
            setNamespaces(res.content);
        }).finally(() => setLoading(false));
    }, [setLoading])

    return (
        <>
            <Card title="Namespaces" subtitle="Manage your Namespaces. Click to edit.">
                <>
                    {namespaces && namespaces.map((namespace: Namespace) => {
                        return (
                            <ListItem name={namespace.name} onClick={() => navigate(`/admin/namespaces/${namespace.id}`)} key={uuid()} />
                        )
                    })}
                    <div className="flex mt-5">
                        <Button onClick={() => navigate(`/admin/namespaces/new`)}>Create</Button>
                    </div>
                </>
            </Card>
        </>
    )
}
import { useEffect, useState } from "react";
import { useNavigate } from "react-router";
import TableList from "../components/TableList";
import { useLoading } from "../providers/LoadingProvider";
import { PolicyService } from "../services/PolicyService";
import type { Page } from "../types/Page";
import type { Policy } from "../types/Policy";

export default function PolicyPage() {
    const navigate = useNavigate();
    const service = new PolicyService();
    const { setLoading } = useLoading();

    const [policies, setPolicies] = useState<Policy[]>([]);

    useEffect(() => {
        setLoading(true);
        service.search().then((res: Page<Policy>) => {
            setPolicies(res.content);
        }).finally(() => setLoading(false));
    }, [setLoading])

    return (
        <>
            <TableList title="policies" data={policies} dataLabel="name"
                onCreate={() => navigate(`/admin/policies/new`)}
                onEdit={(id: string) => navigate(`/admin/policies/${id}`)} />
        </>
    )
}
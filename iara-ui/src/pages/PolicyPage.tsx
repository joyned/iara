import { useEffect, useState } from "react";
import { useNavigate } from "react-router";
import Button from "../components/Button";
import Card from "../components/Card";
import ListItem from "../components/ListItem";
import { useLoading } from "../providers/LoadingProvider";
import { PolicyService } from "../services/PolicyService";
import type { Page } from "../types/Page";
import type { Policy } from "../types/Policy";
import { uuid } from "../utils/UUID";

export default function PolicyPage() {
    const navigate = useNavigate();
    const service = new PolicyService();
    const { setLoading } = useLoading();

    const [policies, setPolicies] = useState<Policy[]>();

    useEffect(() => {
        setLoading(true);
        service.search().then((res: Page<Policy>) => {
            setPolicies(res.content);
        }).finally(() => setLoading(false));
    }, [setLoading])

    return (
        <>
            <Card title="Policies" subtitle="Manage your Policies. Check FAQ to understand how it works.">
                <>
                    {policies && policies.map((policy: Policy) => {
                        return (
                            <ListItem name={policy.name} onClick={() => navigate(`/admin/policies/${policy.id}`)} key={uuid()} />
                        )
                    })}
                    <div className="flex mt-5">
                        <Button onClick={() => navigate(`/admin/policies/new`)}>Create</Button>
                    </div>
                </>
            </Card>
        </>
    )
}
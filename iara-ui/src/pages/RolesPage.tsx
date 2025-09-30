import { useEffect, useState } from "react";
import { useNavigate } from "react-router";
import TableList from "../components/TableList";
import { useLoading } from "../providers/LoadingProvider";
import { RoleService } from "../services/RoleService";
import type { Page } from "../types/Page";
import type { Role } from "../types/Role";

export default function RolesPage() {
    const navigate = useNavigate();
    const service = new RoleService();

    const [roles, setRoles] = useState<Role[]>([]);

    const { setLoading } = useLoading();

    useEffect(() => {
        setLoading(true);
        service.search().then((res: Page<Role>) => {
            setRoles(res.content);
        }).finally(() => setLoading(false));
    }, [setLoading])

    return (
        <>
            <TableList title="roles" data={roles} dataLabel="name"
                onCreate={() => navigate(`/admin/roles/new`)}
                onEdit={(id: string) => navigate(`/admin/roles/${id}`)} />
        </>
    )
}
import { useEffect, useState } from "react";
import { useNavigate } from "react-router";
import Button from "../components/Button";
import Card from "../components/Card";
import ListItem from "../components/ListItem";
import { RoleService } from "../services/RoleService";
import type { Role } from "../types/Role";
import { useLoading } from "../providers/LoadingProvider";
import type { Page } from "../types/Page";
import { uuid } from "../utils/UUID";

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
    }, [])

    return (
        <>
            <Card title="Roles">
                {roles.length > 0 && roles.map((role: Role) => {
                    return (
                        <ListItem name={role.name} onClick={() => navigate(`/admin/roles/${role.id}`)} key={uuid()} />
                    )
                })}
                <div className="flex mt-5">
                    <Button onClick={() => navigate(`/admin/roles/new`)}>Create</Button>
                </div>
            </Card>
        </>
    )
}
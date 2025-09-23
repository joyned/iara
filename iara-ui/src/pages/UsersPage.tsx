import { useEffect, useState } from "react";
import { useNavigate } from "react-router";
import TableList from "../components/TableList";
import { useLoading } from "../providers/LoadingProvider";
import { UserService } from "../services/UserService";
import type { Page } from "../types/Page";
import type { User } from "../types/User";

export default function UsersPage() {
    const navigate = useNavigate();
    const service = new UserService();

    const [users, setUsers] = useState<User[]>([]);

    const { setLoading } = useLoading();

    useEffect(() => {
        setLoading(true);
        service.search().then((res: Page<User>) => {
            setUsers(res.content);
        }).finally(() => setLoading(false));
    }, [setLoading])

    return (
        <>
            <TableList title="Roles" data={users} dataLabel="name"
                onCreate={() => navigate(`/admin/users/new`)}
                onEdit={(id: string) => navigate(`/admin/users/${id}`)} />
        </>
    )
}
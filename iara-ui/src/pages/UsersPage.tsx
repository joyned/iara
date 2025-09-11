import { useEffect, useState } from "react";
import { useNavigate } from "react-router";
import Button from "../components/Button";
import Card from "../components/Card";
import ListItem from "../components/ListItem";
import { useLoading } from "../providers/LoadingProvider";
import { UserService } from "../services/UserService";
import type { Page } from "../types/Page";
import type { User } from "../types/User";
import { uuid } from "../utils/UUID";

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
    }, [service, setLoading])

    return (
        <>
            <Card title="Users">
                {users.length > 0 && users.map((user: User) => {
                    return (
                        <ListItem name={user.name} onClick={() => navigate(`/admin/users/${user.id}`)} key={uuid()} />
                    )
                })}
                <div className="flex mt-5">
                    <Button onClick={() => navigate(`/admin/users/new`)}>Create</Button>
                </div>
            </Card>
        </>
    )
}
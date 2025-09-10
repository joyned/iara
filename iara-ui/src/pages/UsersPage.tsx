import { useEffect, useState } from "react";
import { useNavigate } from "react-router";
import Card from "../components/Card";
import { UserService } from "../services/UserService";
import type { User } from "../types/User";
import type { Page } from "../types/Page";
import ListItem from "../components/ListItem";
import Button from "../components/Button";
import { useLoading } from "../providers/LoadingProvider";

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
    }, [])

    return (
        <>
            <Card title="Users">
                {users.length > 0 && users.map((user: User) => {
                    return (
                        <ListItem name={user.name} onClick={() => navigate(`/admin/users/${user.id}`)} />
                    )
                })}
                <div className="flex mt-5">
                    <Button onClick={() => navigate(`/admin/users/new`)}>Create</Button>
                </div>
            </Card>
        </>
    )
}
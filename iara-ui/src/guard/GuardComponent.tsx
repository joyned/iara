import { useEffect, type ReactNode } from "react";
import { UserService } from "../services/UserService";
import { useNavigate } from "react-router";

interface Props {
    destiny: ReactNode;
}

export default function GuardComponent(props: Props) {
    const userService = new UserService();
    const navigate = useNavigate();

    useEffect(() => {
        userService.me().catch(() => {
            navigate('/login')
        });
    })
    return (
        props.destiny
    )
}
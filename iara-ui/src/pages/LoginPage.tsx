import { useEffect, useState, type ChangeEvent } from "react";
import { FaGoogle } from "react-icons/fa";
import { useNavigate } from "react-router";
import Logo from "../assets/logo-name-white.svg?react";
import Button from "../components/Button";
import Input from "../components/Input";
import { LoginService } from "../services/LoginService";

export default function LoginPage() {
    const service = new LoginService();
    const navigate = useNavigate();

    const [isGoogleSSOEnabled, setIsGoogleSSOEnabled] = useState<boolean>(false);

    const [email, setEmail] = useState<string>('');
    const [password, setPassword] = useState<string>('');

    useEffect(() => {
        service.isGoogleSSOEnabled().then((res: boolean) => setIsGoogleSSOEnabled(res));
    }, [service])

    const doLogin = (e: ChangeEvent<HTMLFormElement>) => {
        e.preventDefault();

        service.doLogin(email, password).then(() => {
            navigate('/kv');
        });
    }

    return (
        <div className="flex w-screen h-screen bg-primary-color">
            <div className="w-full flex flex-col justify-center">
                <div className="flex flex-col items-center">
                    <Logo className="w-[300px] h-[100px]" />
                </div>
                <div className="w-full flex justify-center">
                    <form className="flex flex-col gap-5 p-5 w-md" onSubmit={doLogin}>
                        <div className="flex flex-col gap-2">
                            <label htmlFor="username" className="text-white">Email</label>
                            <Input name="username" type="email" onChange={(e: ChangeEvent<HTMLInputElement>) => setEmail(e.target.value)} />
                        </div>
                        <div className="flex flex-col gap-2">
                            <label htmlFor="password" className="text-white">Password</label>
                            <Input name="password" type="password" onChange={(e: ChangeEvent<HTMLInputElement>) => setPassword(e.target.value)} />
                        </div>
                        <div className="flex justify-center">
                            <Button className="w-[120px]">Login</Button>
                        </div>
                    </form>
                </div>
                {isGoogleSSOEnabled &&
                    <div className="w-full flex justify-center">
                        <span className="flex items-center bg-red-500 text-white gap-5 p-2 rounded-sm cursor-pointer hover:bg-red-600">
                            <FaGoogle></FaGoogle>
                            <span>Sign-in with Google SSO</span>
                        </span>
                    </div>
                }
            </div>
        </div>
    )
}
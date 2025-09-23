import { useEffect, useState, type ChangeEvent } from "react";
import { useNavigate } from "react-router";
import { LoginService } from "../services/LoginService";
import Footer from "../components/Footer";

export default function LoginPage() {
    const service = new LoginService();
    const navigate = useNavigate();

    const [isGoogleSSOEnabled, setIsGoogleSSOEnabled] = useState<boolean>(false);

    const [email, setEmail] = useState<string>('');
    const [password, setPassword] = useState<string>('');

    const [errorMessage, setErrorMessage] = useState<string>();

    const handleCredentialResponse = (response: any) => {
        service.doLoginGoogleSSO(response.code).then(() => {
            navigate('/kv');
        }).catch(async (err: any) => {
            const body = await err.json();
            if (body.key === "ACCOUNT_NOT_FOUND") {
                setErrorMessage("You don't have permission to access this resource. Please, contact your manager.");
            } else {
                setErrorMessage('An unknow error occurred. Please, if persist contact your manager.');
            }
        });
    };

    const [googleInstace, setGoogleInstance] = useState<any>();

    useEffect(() => {
        service.isGoogleSSOEnabled().then((res: string) => {
            setIsGoogleSSOEnabled(!!res)
            if (res && (window as any).google) {
                setGoogleInstance((window as any).google.accounts.oauth2.initCodeClient({
                    client_id: res,
                    scope: 'email profile',
                    callback: (res: any) => {
                        handleCredentialResponse(res);
                    }
                }))
            }
        });
    }, [])

    const doLogin = (e: ChangeEvent<HTMLFormElement>) => {
        e.preventDefault();

        service.doLogin(email, password).then(() => {
            navigate('/kv');
        }).catch(async (err: any) => {
            const body = await err.json();
            if (body.key === "INVALID_CREDENTIALS") {
                setErrorMessage('Your credentials are invalid. Please, check your E-mail and Password');
            } else {
                setErrorMessage('An unknow error occurred. Please, if persist contact your manager.');
            }
        });
    }

    return (
        <div className="w-screen h-screen flex flex-col">
            <div className="flex justify-center items-center w-full min-h-16 max-h-16 bg-primary-color">
                <h1 style={{ color: 'white', fontSize: '32px', margin: '0' }}>
                    IARA CM
                </h1>
            </div>
            <div className="flex justify-center items-center w-full h-full">
                <div className="flex flex-col gap-10 border-2 border-stone-200 p-5">
                    <div className="flex">
                        <span className="text-3xl">Welcome!</span>
                    </div>
                    {errorMessage &&
                        <div className="flex bg-red-500 text-white p-3">
                            <span>{errorMessage}</span>
                        </div>
                    }
                    <form className="flex flex-col gap-5" onSubmit={doLogin}>
                        <div className="flex flex-col gap-2">
                            <label htmlFor="email">Email</label>
                            <input className="bg-stone-100 p-3 w-2xl" id="email" name="email" type="email"
                                value={email} onChange={(e: ChangeEvent<HTMLInputElement>) => setEmail(e.target.value)} />
                        </div>

                        <div className="flex flex-col gap-2">
                            <label htmlFor="password">Password</label>
                            <input className="bg-stone-100 p-3" id="password" name="password" type="password"
                                value={password} onChange={(e: ChangeEvent<HTMLInputElement>) => setPassword(e.target.value)} />
                        </div>

                        <div className="flex">
                            <button className="w-full bg-primary-color text-white p-3 cursor-pointer">Login</button>
                        </div>

                        <div className="flex gap-5 justify-end">
                            {isGoogleSSOEnabled &&
                                <button className="gsi-material-button" type="button" onClick={() => googleInstace.requestCode()}>
                                    <div className="gsi-material-button-state"></div>
                                    <div className="gsi-material-button-content-wrapper">
                                        <div className="gsi-material-button-icon">
                                            <svg version="1.1" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 48 48" style={{ display: 'block' }}>
                                                <path fill="#EA4335" d="M24 9.5c3.54 0 6.71 1.22 9.21 3.6l6.85-6.85C35.9 2.38 30.47 0 24 0 14.62 0 6.51 5.38 2.56 13.22l7.98 6.19C12.43 13.72 17.74 9.5 24 9.5z"></path>
                                                <path fill="#4285F4" d="M46.98 24.55c0-1.57-.15-3.09-.38-4.55H24v9.02h12.94c-.58 2.96-2.26 5.48-4.78 7.18l7.73 6c4.51-4.18 7.09-10.36 7.09-17.65z"></path>
                                                <path fill="#FBBC05" d="M10.53 28.59c-.48-1.45-.76-2.99-.76-4.59s.27-3.14.76-4.59l-7.98-6.19C.92 16.46 0 20.12 0 24c0 3.88.92 7.54 2.56 10.78l7.97-6.19z"></path>
                                                <path fill="#34A853" d="M24 48c6.48 0 11.93-2.13 15.89-5.81l-7.73-6c-2.15 1.45-4.92 2.3-8.16 2.3-6.26 0-11.57-4.22-13.47-9.91l-7.98 6.19C6.51 42.62 14.62 48 24 48z"></path>
                                                <path fill="none" d="M0 0h48v48H0z"></path>
                                            </svg>
                                        </div>
                                        <span className="gsi-material-button-contents">Sign in with Google</span>
                                    </div>
                                </button>
                            }
                        </div>
                    </form>
                </div>
            </div>
            <Footer />
        </div>
    )
}
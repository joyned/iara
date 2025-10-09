import { useEffect, useState, type ChangeEvent } from "react";
import { FaGoogle } from "react-icons/fa";
import { useNavigate } from "react-router";
import { LoginService } from "../../services/LoginService";
import type { OTPConfig } from "../../types/OTPConfig";
// eslint-disable-next-line @typescript-eslint/ban-ts-comment
// @ts-ignore
import { QRCode } from "react-qr-code";

export default function LoginPage() {
    const service = new LoginService();
    const navigate = useNavigate();

    const [isGoogleSSOEnabled, setIsGoogleSSOEnabled] = useState<boolean>(false);

    const [email, setEmail] = useState<string>('');
    const [password, setPassword] = useState<string>('');

    const [errorMessage, setErrorMessage] = useState<string>();

    const [isOtp, setIsOtp] = useState<boolean>(false);
    const [OTPUrl, setOTPUrl] = useState<string>('');
    const [otpCode, setOtpCode] = useState<string>();
    const [session, setSession] = useState<string>();

    const handleCredentialResponse = (response: any) => {
        service.doLoginGoogleSSO(response.code).then(() => {
            navigate('/kv');
        }).catch(async (err: any) => {
            const body = await err.json();
            if (body.key === "ACCOUNT_NOT_FOUND") {
                setErrorMessage("you don't have permission to access this resource. please, contact your manager.");
            } else {
                setErrorMessage('an unknow error occurred. please, if persist contact your manager.');
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

        service.doLogin(email, password).then((config: OTPConfig) => {
            setOTPUrl(config.otpUrl);
            setSession(config.session);
            setIsOtp(true);
        }).catch(async (err: any) => {
            const body = await err.json();
            if (body.key === "INVALID_CREDENTIALS") {
                setErrorMessage('your credentials are invalid. please, check your e-mail and password');
            } else {
                setErrorMessage('an unknow error occurred. please, if persist contact your manager.');
            }
        });
    }

    const verifyOtp = (e: ChangeEvent<HTMLFormElement>) => {
        e.preventDefault();
        if (otpCode && session) {
            service.verifyOtp(otpCode, session).then(() => {
                navigate('/kv')
            }).catch(async (err: any) => {
                const body = await err.json();
                if (body.key === "OTP_ERROR") {
                    setErrorMessage('your code are invalid. please, try again.');
                } else {
                    setErrorMessage('an unknow error occurred. please, if persist contact your manager.');
                }
            });
        }
    }

    return (
        <div className="w-screen h-screen flex flex-col">
            <div className="flex justify-start items-center p-5 w-full min-h-13 max-h-13 bg-primary-color">
                <span className="text-title text-white text-4xl">IARA</span>
            </div>
            <div className="flex justify-center items-center w-full h-full">
                <div className="flex flex-col gap-5 p-5 sm:w-2/3 md:w-2/3 lg:w-1/3">
                    <div className="flex justify-center">
                        <span className="text-title text-3xl">Welcome!</span>
                    </div>
                    {errorMessage &&
                        <div className="flex bg-red-500 text-white p-3">
                            <span>{errorMessage}</span>
                        </div>
                    }
                    {!isOtp &&
                        <form className="flex flex-col gap-5" onSubmit={doLogin}>
                            <div className="flex flex-col gap-2">
                                <label htmlFor="email">email</label>
                                <input className="border border-gray-400 rounded p-2 w-full bg-transparent" id="email" name="email" type="email"
                                    value={email} onChange={(e: ChangeEvent<HTMLInputElement>) => setEmail(e.target.value)} />
                            </div>

                            <div className="flex flex-col gap-2">
                                <label htmlFor="password">password</label>
                                <input className="border border-gray-400 rounded p-2 w-full bg-transparent" id="password" name="password" type="password"
                                    value={password} onChange={(e: ChangeEvent<HTMLInputElement>) => setPassword(e.target.value)} />
                            </div>

                            <div className="flex">
                                <button className="w-full bg-primary-color text-white p-2 rounded cursor-pointer">login</button>
                            </div>

                            <div className="flex gap-5 justify-end">
                                {isGoogleSSOEnabled &&
                                    <button className="flex gap-2 items-center border border-gray-500 rounded p-3 cursor-pointer"
                                        type="button" onClick={() => googleInstace.requestCode()}>
                                        <FaGoogle />
                                        sign in with google
                                    </button>
                                }
                            </div>
                        </form>
                    }
                    {(isOtp && OTPUrl) &&
                        <div className="flex flex-col gap-2 items-center justify-center">
                            <span>Please, install Google Authenticator or any other 2FA app.</span>
                            <span>After installing, scan the QR Code and enter the code below to activate your 2FA.</span>
                            <QRCode value={OTPUrl} bgColor={"#EBEBEB"} />
                        </div>
                    }

                    {(isOtp) &&
                        <form className="flex flex-col gap-5" onSubmit={verifyOtp} >
                            <div className="flex flex-col gap-2">
                                <label htmlFor="email">code</label>
                                <input className="border border-gray-400 rounded p-2 w-full bg-transparent"
                                    id="code" name="code" type="text" maxLength={6} value={otpCode}
                                    onChange={(e: ChangeEvent<HTMLInputElement>) => setOtpCode(e.target.value)} />
                            </div>

                            <div className="flex">
                                <button className="w-full bg-primary-color text-white p-2 rounded cursor-pointer">verify</button>
                            </div>
                        </form>
                    }
                </div>
            </div>
        </div>
    )
}
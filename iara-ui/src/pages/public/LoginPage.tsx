import { useEffect, useState, type ChangeEvent } from "react";
import { FaGoogle } from "react-icons/fa";
import { useNavigate } from "react-router";
import { LoginService } from "../../services/LoginService";
import type { OTPConfig } from "../../types/OTPConfig";
// eslint-disable-next-line @typescript-eslint/ban-ts-comment
// @ts-ignore
import { QRCode } from "react-qr-code";
import FormLabel from "../../components/FormLabel";

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
                setErrorMessage('Your credentials are invalid. Please, check your e-mail and password');
            } else {
                setErrorMessage('An unknow error occurred. Please, if persist contact your manager.');
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
                    setErrorMessage('Your code are invalid. Please, try again.');
                } else {
                    setErrorMessage('An unknow error occurred. Please, if persist contact your manager.');
                }
            });
        }
    }

    return (
        <div className="w-screen h-screen flex flex-col text-white">
            <div className="flex justify-center items-center w-full h-full">
                <div className="flex flex-col gap-5 p-10 sm:w-2/3 md:w-2/3 lg:w-1/3 bg-primary-color rounded">
                    <div className="flex justify-start">
                        <span className="text-title text-3xl">IARA</span>
                    </div>
                    {errorMessage &&
                        <div className="flex bg-red-500 text-white p-3">
                            <span>{errorMessage}</span>
                        </div>
                    }
                    {!isOtp &&
                        <form className="flex flex-col gap-5" onSubmit={doLogin}>
                            <div className="flex flex-col gap-2">
                                <FormLabel htmlFor="email" required>Email</FormLabel>
                                <input className="border border-gray-400 rounded p-2 w-full bg-transparent" id="email" name="email" type="email"
                                    value={email} onChange={(e: ChangeEvent<HTMLInputElement>) => setEmail(e.target.value)} />
                            </div>

                            <div className="flex flex-col gap-2">
                                <FormLabel htmlFor="password" required>Password</FormLabel>
                                <input className="border border-gray-400 rounded p-2 w-full bg-transparent" id="password" name="password" type="password"
                                    value={password} onChange={(e: ChangeEvent<HTMLInputElement>) => setPassword(e.target.value)} />
                            </div>

                            <div className="flex">
                                <button className="w-full bg-primary-darker-color text-white p-2 rounded cursor-pointer">login</button>
                            </div>

                            <div className="flex gap-5 justify-end">
                                {isGoogleSSOEnabled &&
                                    <button className="flex gap-2 items-center border border-gray-500 rounded p-3 cursor-pointer"
                                        type="button" onClick={() => googleInstace.requestCode()}>
                                        <FaGoogle />
                                        Sign in with Google
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
                                <label htmlFor="email">MFA Code</label>
                                <input className="border border-gray-400 rounded p-2 w-full bg-transparent"
                                    id="code" name="code" type="text" maxLength={6} value={otpCode}
                                    onChange={(e: ChangeEvent<HTMLInputElement>) => setOtpCode(e.target.value)} />
                            </div>

                            <div className="flex">
                                <button className="w-full bg-primary-darker-color text-white p-2 rounded cursor-pointer">Verify</button>
                            </div>
                        </form>
                    }
                </div>
            </div>
        </div>
    )
}
import { useEffect, useState } from "react";
import { FaGoogle } from "react-icons/fa";
import Button from "../../components/Button";
import Card from "../../components/Card";
import Checkbox from "../../components/Checkbox";
import FormLabel from "../../components/FormLabel";
import Input from "../../components/Input";
import { ApplicationParamsService } from "../../services/ApplicationParamsService";
import type { ApplicationParams } from "../../types/ApplicationParams";

export default function SSOForm() {
    const service = new ApplicationParamsService();

    const GOOGLE_ENABLE_KEY = "GOOGLE_SSO_ENABLED";
    const GOOGLE_SSO_CLIENT_SECRET_KEY = "GOOGLE_SSO_CLIENT_SECRET";
    const GOOGLE_SSO_CLIENT_ID_KEY = "GOOGLE_SSO_CLIENT_ID";

    const [googleSsoEnabled, setGoogleSsoEnabled] = useState<boolean>(false);
    const [googleSsoClientId, setGoogleSsoClientId] = useState<string>();
    const [googleSsoClientSecret, setGoogleSsoClientSecret] = useState<string>();

    useEffect(() => {
        service.find(GOOGLE_ENABLE_KEY).then((res: ApplicationParams) => {
            if (res.value) {
                setGoogleSsoEnabled(Boolean(res.value));
                if (Boolean(res.value)) {
                    const clientIdPromise = service.find(GOOGLE_SSO_CLIENT_ID_KEY);
                    const secretPromise = service.find(GOOGLE_SSO_CLIENT_SECRET_KEY);
                    Promise.all([clientIdPromise, secretPromise]).then(([clientId, clientSecret]) => {
                        setGoogleSsoClientId(clientId.value)
                        setGoogleSsoClientSecret(clientSecret.value);
                    })
                }
            }
        })
    }, [service])

    return (
        <Card title="Sign-in options" subtitle="Enable or disable sign-in options.">
            <>
                <div className="flex flex-col gap-2">
                    <div className="flex gap-2">
                        <div className="flex items-baseline">
                            <FaGoogle />
                            <span>oogle</span>
                        </div>
                        <Checkbox value={googleSsoEnabled} onChange={(value: boolean) => setGoogleSsoEnabled(value)} />
                    </div>
                    {googleSsoEnabled &&
                        <>
                            <div className="flex flex-col gap-2">
                                <FormLabel required>Client ID</FormLabel>
                                <Input value={googleSsoClientId} />
                            </div>
                            <div className="flex flex-col gap-2">
                                <FormLabel required>Client Secret</FormLabel>
                                <Input type="password" value={googleSsoClientSecret} />
                            </div>
                        </>
                    }
                </div>
                <div className="flex mt-5">
                    <Button>Save</Button>
                </div>
            </>
        </Card>
    )
}
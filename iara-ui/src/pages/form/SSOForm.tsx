import { useEffect, useState, type ChangeEvent } from "react";
import { FaGoogle } from "react-icons/fa";
import Button from "../../components/Button";
import Checkbox from "../../components/Checkbox";
import FormLabel from "../../components/FormLabel";
import Input from "../../components/Input";
import Panel from "../../components/Panel";
import { useLoading } from "../../providers/LoadingProvider";
import { ApplicationParamsService } from "../../services/ApplicationParamsService";
import type { ApplicationParams } from "../../types/ApplicationParams";

export default function SSOForm() {
    const service = new ApplicationParamsService();

    const GOOGLE_ENABLE_KEY = "GOOGLE_SSO_ENABLED";
    const GOOGLE_SSO_CLIENT_SECRET_KEY = "GOOGLE_SSO_CLIENT_SECRET";
    const GOOGLE_SSO_CLIENT_ID_KEY = "GOOGLE_SSO_CLIENT_ID";

    const { setLoading } = useLoading();

    const [enabledId, setEnabledId] = useState<string>('');
    const [enabledKey, setEnabledKey] = useState<string>('');
    const [enabledValue, setEnabledValue] = useState<boolean>(false);
    const [enabledSecure, setEnabledSecure] = useState<boolean>(true);

    const [clientId, setClientId] = useState<string>('');
    const [clientKey, setClientKey] = useState<string>('');
    const [clientValue, setClientValue] = useState<string>('');
    const [clientSecure, setClientSecure] = useState<boolean>(true);

    const [secretId, setSecretId] = useState<string>('');
    const [secretKey, setSecretKey] = useState<string>('');
    const [secretValue, setSecretValue] = useState<string>('');
    const [secretSecure, setSecretSecure] = useState<boolean>(true);

    useEffect(() => {
        setLoading(true);
        service.find(GOOGLE_ENABLE_KEY).then((res: ApplicationParams) => {
            if (res.value) {
                setEnabledId(res.id);
                setEnabledKey(res.key)
                setEnabledValue(Boolean(res.value));
                setEnabledSecure(res.secure);

                if (Boolean(res.value)) {
                    const clientIdPromise = service.find(GOOGLE_SSO_CLIENT_ID_KEY);
                    const secretPromise = service.find(GOOGLE_SSO_CLIENT_SECRET_KEY);
                    Promise.all([clientIdPromise, secretPromise]).then(([clientId, clientSecret]) => {
                        setClientId(clientId.id);
                        setClientKey(clientId.key);
                        setClientValue(clientId.value || '');
                        setClientSecure(clientId.secure);

                        setSecretId(clientSecret.id);
                        setSecretKey(clientSecret.key);
                        setSecretValue(clientSecret.value || '');
                        setSecretSecure(clientSecret.secure);
                    })
                }
            }
        }).finally(() => setLoading(false));
    }, []);

    const onSubmit = (e: ChangeEvent<HTMLFormElement>) => {
        e.preventDefault();

        const enabledPromise = service.persist({
            id: enabledId,
            key: enabledKey,
            value: String(enabledValue),
            secure: enabledSecure
        })

        const clientPromise = service.persist({
            id: clientId,
            key: clientKey,
            value: clientValue,
            secure: clientSecure
        });

        if (secretValue !== '') {
            service.persist({
                id: secretId,
                key: secretKey,
                value: secretValue,
                secure: secretSecure
            }).then((secretPromise: ApplicationParams) => {
                setSecretId(secretPromise.id);
                setSecretKey(secretPromise.key);
                setSecretValue(secretPromise.value || '');
                setSecretSecure(secretPromise.secure);
            });
        }


        setLoading(true);
        Promise.all([enabledPromise, clientPromise]).then(([enabled, clientId]) => {
            setEnabledId(enabled.id);
            setEnabledKey(enabled.key)
            setEnabledValue(Boolean(enabled.value));
            setEnabledSecure(enabled.secure);

            setClientId(clientId.id);
            setClientKey(clientId.key);
            setClientValue(clientId.value || '');
            setClientSecure(clientId.secure);
        }).finally(() => setLoading(false));
    }

    return (
        <Panel title="Sign-in options" startClosed={false}>
            <>
                <div className="flex flex-col gap-2">
                    <div className="flex gap-2">
                        <div className="flex items-baseline">
                            <FaGoogle />
                            <span>oogle</span>
                        </div>
                        <Checkbox value={enabledValue} onChange={(value: boolean) => setEnabledValue(value)} />
                    </div>
                    {enabledValue &&
                        <form onSubmit={onSubmit} className="flex flex-col gap-4">
                            <div className="flex flex-col gap-2">
                                <FormLabel required>Client ID</FormLabel>
                                <Input value={clientValue}
                                    onChange={(e: ChangeEvent<HTMLInputElement>) => setClientValue(e.target.value)} />
                            </div>
                            <div className="flex flex-col gap-2">
                                <FormLabel required>Client Secret</FormLabel>
                                <Input type="password" value={secretValue}
                                    onChange={(e: ChangeEvent<HTMLInputElement>) => setSecretValue(e.target.value)} />
                            </div>
                            <div className="flex mt-5">
                                <Button>Save</Button>
                            </div>
                        </form>
                    }
                </div>

            </>
        </Panel>
    )
}
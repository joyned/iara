import { useEffect, useRef, useState, type ChangeEvent } from "react";
import { toast } from "react-toastify";
import Button from "../components/Button";
import Card from "../components/Card";
import Checkbox from "../components/Checkbox";
import FormLabel from "../components/FormLabel";
import Input from "../components/Input";
import ListItem from "../components/ListItem";
import { Modal } from "../components/Modal";
import Select from "../components/Select";
import { useLoading } from "../providers/LoadingProvider";
import { ApplicationTokenService } from "../services/ApplicationTokenService";
import { PolicyService } from "../services/PolicyService";
import type { ApplicationToken } from "../types/ApplicationToken";
import type { Page } from "../types/Page";
import type { Policy } from "../types/Policy";

export default function ApplicationPageToken() {
    const service = new ApplicationTokenService();
    const policyService = new PolicyService();

    const { setLoading } = useLoading();

    const [tokens, setTokens] = useState<ApplicationToken[]>();

    const [name, setName] = useState<string>('');
    const [expiresAt, setExpiresAt] = useState<Date | undefined>();
    const [neverExpires, setNeverExpires] = useState<boolean>(false);
    const [policy, setPolicy] = useState<Policy>();

    const [policies, setPolicies] = useState<Policy[]>([]);

    const modalRef = useRef<any>(null);
    const tokenModalRef = useRef<any>(null);

    const [token, setToken] = useState<string>();

    const search = () => {
        service.search().then((res: Page<ApplicationToken>) => {
            setTokens(res.content);
        })
    }

    useEffect(() => {
        search()
    }, [])

    const onCreateClick = () => {
        setLoading(true);
        policyService.search({}, 0, 2000).then((res: Page<Policy>) => {
            setName('')
            setExpiresAt(undefined)
            setPolicies(res.content);
            modalRef.current.setOpen(true);
        }).finally(() => setLoading(false));
    }

    const getMinDate = () => {
        return new Date().toISOString().split('T')[0];
    }

    const onCreateNewToken = (e: ChangeEvent<HTMLFormElement>) => {
        e.preventDefault();

        if (!neverExpires && !expiresAt) {
            toast.error('Please, select a valid expiration date.');
            return;
        }

        if (!name || name === '') {
            toast.error('Please, enter a valid name.');
            return;
        }

        if (!policy) {
            toast.error('Please, select a valid policy.');
            return;
        }

        const newToken: ApplicationToken = {
            name: name,
            expiresAt: neverExpires ? undefined : expiresAt,
            policy: policy
        }

        setLoading(true);
        service.persist(newToken).then((res: ApplicationToken) => {
            setToken(res.token);
            modalRef.current.setOpen(false);
            tokenModalRef.current.setOpen(true);
            search()
        }).finally(() => setLoading(false));
    }

    const onDelete = (id: string) => {
        setLoading(true);
        service.delete(id).then(() => {
            search();
        }).finally(() => setLoading(false));
    }

    const copyToClipboard = async () => {
        if (token) {
            try {
                await navigator.clipboard.writeText(token);
                toast.success('Token copied to clipboard.');
            } catch (err) {
                console.error('Failed to copy text:', err);
            }
        }

    }

    return (
        <>
            <Card title="Tokens" subtitle="Manage your Tokens.">
                <>
                    {tokens && tokens.map((token: ApplicationToken) => {
                        return (
                            <ListItem name={token.name} onDelete={() => token.id && onDelete(token.id)}></ListItem>
                        )
                    })}
                    <div className="flex mt-5">
                        <Button type="button" onClick={onCreateClick}>Create</Button>
                    </div>
                </>
            </Card>
            <Modal title="Create token" ref={modalRef} saveText="Create" onSave={onCreateNewToken}>
                <div className="flex flex-col gap-2">
                    <div className="flex flex-col gap-2">
                        <FormLabel htmlFor="token-name" required>Name</FormLabel>
                        <Input id="token-name" name="token-name" onChange={(e: ChangeEvent<HTMLInputElement>) => setName(e.target.value)} />
                    </div>
                    <div className="flex flex-col gap-2">
                        <FormLabel htmlFor="token-policy" required>Policy</FormLabel>
                        <Select id="token-policy" name="token-policy" options={policies}
                            onChange={(e: ChangeEvent<HTMLSelectElement>) => setPolicy(JSON.parse(e.target.value))}></Select>
                    </div>
                    {!neverExpires &&
                        <div className="flex flex-col gap-2">
                            <FormLabel htmlFor="token-expires" required>Expires At</FormLabel>
                            <Input id="token-expires" name="token-expires" type="date" min={getMinDate()}
                                onChange={(e: ChangeEvent<HTMLInputElement>) => setExpiresAt(new Date(e.target.value))} />
                        </div>
                    }
                    <div className="flex flex-col gap-2">
                        <FormLabel htmlFor="token-never-expires">Never Expires</FormLabel>
                        <Checkbox id="token-never-expires" name="token-never-expires" onChange={() => setNeverExpires(!neverExpires)} />
                    </div>
                </div>
            </Modal>

            <Modal title="Token" hasSave={false} ref={tokenModalRef} beforeClose={() => setToken(undefined)} cancelText="Close">
                <div className="flex flex-col gap-5">
                    <span className="p-1 rounded">Please, save this token. When you close this window, you will not be able to get your token.</span>
                    <pre className="overflow-auto bg-stone-700 text-white p-1 rounded">
                        {token}
                    </pre>
                    <div className="flex">
                        <Button type="button" variant="outline" onClick={copyToClipboard}>Copy to clipboard</Button>
                    </div>
                </div>
            </Modal>
        </>
    )

}
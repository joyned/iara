import { useEffect, useRef, useState, type ChangeEvent } from "react";
import { toast } from "react-toastify";
import Button from "../components/Button";
import Checkbox from "../components/Checkbox";
import FormLabel from "../components/FormLabel";
import Input from "../components/Input";
import { Modal } from "../components/Modal";
import Select from "../components/Select";
import TableList from "../components/TableList";
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

    const [tokens, setTokens] = useState<ApplicationToken[]>([]);

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
            toast.error('please, select a valid expiration date.');
            return;
        }

        if (!name || name === '') {
            toast.error('please, enter a valid name.');
            return;
        }

        if (!policy) {
            toast.error('please, select a valid policy.');
            return;
        }

        const newToken: ApplicationToken = {
            name: name,
            expiresAt: neverExpires ? undefined : expiresAt,
            policies: [policy]
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
                toast.success('token copied to clipboard.');
            } catch (err) {
                console.error('failed to copy text:', err);
            }
        }

    }

    return (
        <>
            <TableList title="tokens" data={tokens} dataLabel="name"
                onCreate={() => onCreateClick()}
                onDelete={(id: string) => onDelete(id)} />
            <Modal title="create token" ref={modalRef} saveText="create" onSave={onCreateNewToken}>
                <div className="flex flex-col gap-2">
                    <div className="flex flex-col gap-2">
                        <FormLabel htmlFor="token-name" required>name</FormLabel>
                        <Input id="token-name" name="token-name" onChange={(e: ChangeEvent<HTMLInputElement>) => setName(e.target.value)} />
                    </div>
                    <div className="flex flex-col gap-2">
                        <FormLabel htmlFor="token-policy" required>policy</FormLabel>
                        <Select id="token-policy" name="token-policy" options={policies} value={JSON.stringify(policy)}
                            onChange={(e: ChangeEvent<HTMLSelectElement>) => setPolicy(JSON.parse(e.target.value))}></Select>
                    </div>
                    {!neverExpires &&
                        <div className="flex flex-col gap-2">
                            <FormLabel htmlFor="token-expires" required>expires at</FormLabel>
                            <Input id="token-expires" name="token-expires" type="date" min={getMinDate()}
                                onChange={(e: ChangeEvent<HTMLInputElement>) => setExpiresAt(new Date(e.target.value))} />
                        </div>
                    }
                    <div className="flex flex-col gap-2">
                        <FormLabel htmlFor="token-never-expires">never expires</FormLabel>
                        <Checkbox id="token-never-expires" name="token-never-expires" onChange={() => setNeverExpires(!neverExpires)} />
                    </div>
                </div>
            </Modal>

            <Modal title="token" hasSave={false} ref={tokenModalRef} beforeClose={() => setToken(undefined)} cancelText="close">
                <div className="flex flex-col gap-5">
                    <span className="p-1 rounded">please, save this token. when you close this window, you will not be able to get your token.</span>
                    <pre className="overflow-auto bg-stone-700 text-white p-1 rounded">
                        {token}
                    </pre>
                    <div className="flex">
                        <Button type="button" variant="outline" onClick={copyToClipboard}>copy to clipboard</Button>
                    </div>
                </div>
            </Modal>
        </>
    )

}
import { useEffect, useRef, useState, type ChangeEvent } from "react";
import { toast } from "react-toastify";
import Button from "../components/Button";
import Card from "../components/Card";
import Checkbox from "../components/Checkbox";
import FormLabel from "../components/FormLabel";
import Input from "../components/Input";
import ListItem from "../components/ListItem";
import { Modal } from "../components/Modal";
import { useLoading } from "../providers/LoadingProvider";
import { ApplicationTokenService } from "../services/ApplicationTokenService";
import { UserService } from "../services/UserService";
import type { ApplicationToken } from "../types/ApplicationToken";
import type { Page } from "../types/Page";
import type { User } from "../types/User";
import { uuid } from "../utils/UUID";

export default function UserSettingsPage() {
    const userService = new UserService();
    const applicationTokenService = new ApplicationTokenService();

    const { setLoading } = useLoading();

    const [name, setName] = useState<string>();
    const [email, setEmail] = useState<string>();
    const [isSSO, setIsSSO] = useState<boolean>(false);

    const [tokens, setTokens] = useState<ApplicationToken[]>([]);

    const [tokenName, setTokenName] = useState<string>('');
    const [expiresAt, setExpiresAt] = useState<Date | undefined>();
    const [neverExpires, setNeverExpires] = useState<boolean>(false);
    const [token, setToken] = useState<string>();

    const [oldPassword, setOldPassword] = useState<string>('');
    const [newPassword, setNewPassword] = useState<string>('');
    const [newPasswordRepeat, setNewPasswordRepeat] = useState<string>('');

    const newTokenModalRef = useRef<any>(null);
    const tokenModalRef = useRef<any>(null);

    useEffect(() => {
        setLoading(true);
        userService.me().then((res: User) => {
            setName(res.name);
            setEmail(res.email);
            setIsSSO(res.isSSO);
        }).finally(() => setLoading(false));
    }, [setLoading])

    const onOpenTokens = () => {
        setLoading(true)
        applicationTokenService.userToken().then((res: Page<ApplicationToken>) => {
            setTokens(res.content);
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

        if (!tokenName || tokenName === '') {
            toast.error('Please, enter a valid name.');
            return;
        }

        const newToken: ApplicationToken = {
            name: tokenName,
            expiresAt: neverExpires ? undefined : expiresAt,
        }

        setLoading(true);
        applicationTokenService.createUserToken(newToken).then((res: ApplicationToken) => {
            setToken(res.token);
            newTokenModalRef.current.setOpen(false);
            tokenModalRef.current.setOpen(true);
        }).finally(() => setLoading(false));
    }

    const onDeleteToken = (id: string) => {
        setLoading(true);
        applicationTokenService.deleteUserToken(id).then(() => {
            onOpenTokens();
        }).finally(() => setLoading(false));
    }

    const changePassword = (e: ChangeEvent<HTMLFormElement>) => {
        e.preventDefault();

        if (newPassword !== newPasswordRepeat) {
            toast.error('The new password does not match.');
            return;
        }

        userService.changePassword(oldPassword, newPassword)
            .then(() => toast.success('Password changed.'))

        setNewPasswordRepeat('');
        setNewPassword('');
        setOldPassword('');
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
        <div className="flex flex-col gap-10">
            <Card title="User">
                <div className="flex flex-col gap-3">
                    <div className="flex flex-col gap-2">
                        <FormLabel htmlFor="user-name" required>Name</FormLabel>
                        <Input id="user-name" name="user-name" value={name} disabled />
                    </div>
                    <div className="flex flex-col gap-2">
                        <FormLabel htmlFor="email-name" required>Email</FormLabel>
                        <Input id="email-name" name="email-name" value={email} disabled />
                    </div>
                </div>
            </Card>
            <Card title="Tokens" onOpen={onOpenTokens} closeable>
                <>
                    {tokens && tokens.map((token: ApplicationToken) => {
                        return (
                            <ListItem name={token.name} onDelete={() => token.id && onDeleteToken(token.id)} key={uuid()}></ListItem>
                        )
                    })}
                    <div className="flex mt-5">
                        <Button type="button" onClick={() => newTokenModalRef.current.setOpen(true)}>Create</Button>
                    </div>
                </>
            </Card>
            {!isSSO &&
                <Card title="Password" closeable>
                    <form className="flex flex-col gap-2" onSubmit={changePassword}>
                        <div className="flex flex-col gap-2">
                            <FormLabel htmlFor="current-password" required>Current password</FormLabel>
                            <Input type="password" name="current-password" id="current-password"
                                onChange={(e: ChangeEvent<HTMLInputElement>) => setOldPassword(e.target.value)} />
                        </div>
                        <div className="flex flex-col gap-2">
                            <FormLabel htmlFor="new-password" required>New password</FormLabel>
                            <Input type="password" name="new-password" id="new-password"
                                onChange={(e: ChangeEvent<HTMLInputElement>) => setNewPassword(e.target.value)} />
                        </div>
                        <div className="flex flex-col gap-2">
                            <FormLabel htmlFor="repeat-password" required>Repeat new password</FormLabel>
                            <Input type="password" name="repeat-password" id="repeat-password"
                                onChange={(e: ChangeEvent<HTMLInputElement>) => setNewPasswordRepeat(e.target.value)} />
                        </div>
                        <div className="flex gap-2">
                            <Button type="submit">Change</Button>
                        </div>
                    </form>
                </Card>
            }
            <Modal title="Create token" ref={newTokenModalRef} saveText="Create" onSave={onCreateNewToken}>
                <div className="flex flex-col gap-2">
                    <div className="flex flex-col gap-2">
                        <FormLabel htmlFor="token-name" required>Name</FormLabel>
                        <Input id="token-name" name="token-name" onChange={(e: ChangeEvent<HTMLInputElement>) => setTokenName(e.target.value)} />
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
        </div>

    )
}
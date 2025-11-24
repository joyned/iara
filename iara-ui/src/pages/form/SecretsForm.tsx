import { useEffect, useRef, useState, type ChangeEvent } from "react";
import { TiDeleteOutline } from "react-icons/ti";
import { useNavigate, useParams } from "react-router";
import { toast } from "react-toastify";
import Button from "../../components/Button";
import Checkbox from "../../components/Checkbox";
import { ConfirmDialog } from "../../components/ConfirmDialog";
import FormLabel from "../../components/FormLabel";
import Input from "../../components/Input";
import ListItem from "../../components/ListItem";
import { Modal } from "../../components/Modal";
import TextArea from "../../components/TextArea";
import { useEnvironment } from "../../providers/EnvironmentProvider";
import { useLoading } from "../../providers/LoadingProvider";
import { useNamespace } from "../../providers/NamespaceProvider";
import { SecretService } from "../../services/SecretService";
import type { Page } from "../../types/Page";
import type { Secret } from "../../types/Secret";
import type { SecretVersion } from "../../types/SecretVersion";
import { uuid } from "../../utils/UUID";

export default function SecretsForm() {
    const params = useParams();
    const navigate = useNavigate();
    const service = new SecretService();

    const [id, setId] = useState<string | undefined>();
    const [name, setName] = useState<string>('');
    const [firstValue, setFirstValue] = useState<string>('');
    const [versions, setVersions] = useState<SecretVersion[]>();

    const { setLoading } = useLoading();

    const modalRef = useRef<any>(null);

    const [versionId, setVersionId] = useState<string | undefined>();
    const [version, setVersion] = useState<number>(0);
    const [value, setValue] = useState<string>('');
    const [disablePastVersion, setDisablePastVersion] = useState<boolean>();

    const { environment } = useEnvironment();
    const { namespace } = useNamespace();

    useEffect(() => {
        const id = params.id;
        if (id && id !== 'new') {
            setLoading(true);
            service.search({ id: id }).then((res: Page<Secret>) => {
                const entity = res.content[0];
                setId(entity.id);
                setName(entity.name);
                setVersions(entity.versions);
            }).finally(() => setLoading(false));
        }
    }, [])

    const onFormSubmit = (e: ChangeEvent<HTMLFormElement>) => {
        e.preventDefault();
        if (!id && environment && namespace) {
            const newSecret: Secret = {
                id: undefined,
                name: name,
                environment: environment,
                namespace: namespace,
                versions: [
                    {
                        version: 1,
                        value: firstValue,
                        disabled: false,
                        destroyed: false
                    }
                ]
            }

            setLoading(true);
            service.persist(newSecret).then((res: Secret) => {
                toast.success("Secret saved.")
                navigate(`/secrets/${res.id}`)
                service.search({ id: res.id }).then((res: Page<Secret>) => {
                    const entity = res.content[0];
                    setId(entity.id);
                    setName(entity.name);
                    setVersions(entity.versions);
                }).finally(() => setLoading(false));
            }).finally(() => setLoading(false));
        }
    }

    const onDeleteSecret = () => {
        if (id) {
            setLoading(true);
            service.delete(id).then(() => {
                toast.success("Secret deleted successfully.")
                navigate('/secrets')
            }).finally(() => setLoading(false));
        }
    }

    const onAddVersion = () => {
        const newVersionNumber: number = !versions ? 1 : versions[0].version + 1
        setVersionId(undefined);
        setVersion(newVersionNumber);
        setValue('');
        setDisablePastVersion(false);
        modalRef.current.setOpen(true);
    }

    const onViewVersion = (version: SecretVersion) => {
        if (id && version.id) {
            setLoading(true);
            service.getSecretValue(id, version.id).then((res: string) => {
                setVersionId(version.id);
                setVersion(version.version);
                setValue(res);
                modalRef.current.setOpen(true);
            }).finally(() => setLoading(false));
        }
    }

    const onSaveVersion = () => {
        const newVersionNumber: number = !versions ? 1 : versions[0].version + 1
        const newVersion: SecretVersion = {
            version: newVersionNumber,
            value: value,
            disabled: false,
            destroyed: false
        };

        if (id) {
            setLoading(true);
            service.addSecretVersion(id, newVersion, disablePastVersion || false).then(() => {
                toast.success("New version was added.");
                service.search({ id: id }).then((res: Page<Secret>) => {
                    const entity = res.content[0];
                    setId(entity.id);
                    setName(entity.name);
                    setVersions(entity.versions);
                }).finally(() => setLoading(false));
            }).finally(() => setLoading(false));
        }

        modalRef.current.setOpen(false);
    }

    return (
        <div className="flex flex-col gap-5">
            <h1 className="text-2xl">Secrets</h1>
            <div className="flex flex-col gap-5">
                <form className="flex flex-col gap-5" onSubmit={onFormSubmit}>
                    <div className="flex flex-col gap-2">
                        <FormLabel htmlFor="secret-name" required>Name</FormLabel>
                        <Input id="secret-name" name="secret-name" type="text" value={name}
                            onChange={(e: ChangeEvent<HTMLInputElement>) => setName(e.target.value)} disabled={!!id} />
                    </div>
                    {!id &&
                        <div className="flex flex-col gap-2">
                            <FormLabel htmlFor="secret-name" required>Value </FormLabel>
                            <TextArea id="secret-value" name="secret-value" rows={5} value={firstValue}
                                onChange={(e: ChangeEvent<HTMLTextAreaElement>) => setFirstValue(e.target.value)} />
                        </div>
                    }
                    {id &&
                        <div className="flex flex-col gap-2">
                            <FormLabel htmlFor="secret-name" required>Version</FormLabel>
                            {versions && versions.map((version: SecretVersion) => {
                                return (
                                    <ListItem name={`# ${version.version}`} onClick={() => onViewVersion(version)} key={uuid()}>
                                        {(version.disabled && !version.destroyed) && <TiDeleteOutline className="text-yellow-500 text-3xl" />}
                                        {version.destroyed && <TiDeleteOutline className="text-red-500 text-3xl" />}
                                    </ListItem>
                                )
                            })}
                        </div>
                    }
                    <div className="flex justify-between gap-4">
                        {!id &&
                            <div className="flex gap-4">
                                <Button onClick={() => onSaveVersion()}>Create</Button>
                                <Button variant="outline" onClick={() => navigate('/secrets')} type="button">Back</Button>
                            </div>
                        }
                        {id &&
                            <div className="flex gap-4">
                                <Button onClick={() => onAddVersion()}>Add Version</Button>
                                <Button variant="outline" onClick={() => navigate('/secrets')} type="button">Back</Button>
                            </div>
                        }
                        {id &&
                            <ConfirmDialog onConfirm={onDeleteSecret}>
                                <Button variant="danger" type="button">Delete</Button>
                            </ConfirmDialog>
                        }
                    </div>
                </form>
            </div>
            <Modal ref={modalRef} title={versionId ? `Version #${String(version)}` : 'Add version'} onSave={() => onSaveVersion()}
                cancelText={!versionId ? 'Cancel' : 'Close'} hasSave={!versionId}>
                <div className="flex flex-col gap-5">
                    <div className="flex flex-col gap-2">
                        <FormLabel htmlFor="version-value" required>Value</FormLabel>
                        <TextArea id="version-value" name="version-value" rows={5} value={value}
                            onChange={(e: ChangeEvent<HTMLTextAreaElement>) => setValue(e.target.value)}
                            disabled={!!versionId} />
                    </div>
                    {!versionId &&
                        <div className="flex items-center gap-2">
                            <FormLabel htmlFor="version-disable-past">Disable past version?</FormLabel>
                            <Checkbox checked={disablePastVersion} onChange={(e: ChangeEvent<HTMLInputElement>) => setDisablePastVersion(Boolean(e.target.value))} />
                        </div>
                    }
                </div>
            </Modal>
        </div>
    )
}
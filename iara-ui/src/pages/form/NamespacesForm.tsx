import { useEffect, useRef, useState, type ChangeEvent, type FormEvent } from "react";
import { CiCircleInfo } from "react-icons/ci";
import { useNavigate, useParams } from "react-router";
import { toast } from "react-toastify";
import Button from "../../components/Button";
import Card from "../../components/Card";
import { ConfirmDialog } from "../../components/ConfirmDialog";
import Input from "../../components/Input";
import ListItem from "../../components/ListItem";
import { Modal } from "../../components/Modal";
import { EnvironmentService } from "../../services/EnvironmentService";
import { NamespaceService } from "../../services/NamespaceService";
import type { Environment } from "../../types/Environment";
import type { Namespace } from "../../types/Namespace";
import type { Page } from "../../types/Page";
import FormLabel from "../../components/FormLabel";
import TextArea from "../../components/TextArea";
import { useLoading } from "../../providers/LoadingProvider";

export default function NamespacesForm() {
    const navigate = useNavigate();
    const params = useParams();
    const service = new NamespaceService();
    const envService = new EnvironmentService();

    const [id, setId] = useState<string | undefined>();
    const [name, setName] = useState<string>('');
    const [description, setDescription] = useState<string | undefined>();
    const [environments, setEnvironments] = useState<Environment[] | undefined>([]);

    const modalRef = useRef<any>(null);

    const [envId, setEnvId] = useState<string | undefined>(undefined);
    const [envName, setEnvName] = useState<string>('');
    const [envDescription, setEnvDescription] = useState<string | undefined>();

    const { setLoading } = useLoading();

    useEffect(() => {
        const id = params.id;
        if (id && id !== 'new') {
            setLoading(true);
            service.search({ id: id }).then((res: Page<Namespace>) => {
                setId(res.content[0].id)
                setName(res.content[0].name);
                setDescription(res.content[0].description);
                envService.search({ namespace: res.content[0].id }, 0, 2000).then((r: Page<Environment>) => {
                    setEnvironments(r.content);
                })
            }).finally(() => setLoading(false));
        }
    }, [])

    const onFormSubmit = (event: FormEvent<HTMLFormElement>) => {
        event.preventDefault();

        const nm: Namespace = {
            id: id,
            name: name,
            description: description,
            environments: environments
        }

        if (!environments || environments.length < 1) {
            toast.error("A Namespace should have at least one Environment. Please, add one.");
            return;
        }

        setLoading(true);
        service.persist(nm).then((res: Namespace) => {
            toast.success("Namespace saved.")
            navigate(`/admin/namespaces/${res.id}`)
        }).finally(() => setLoading(false));
    }

    const onEdit = (env: Environment) => {
        setEnvId(env.id);
        setEnvName(env.name);
        setEnvDescription(env.description);
        modalRef.current.setOpen(true);
    }

    const onAdd = () => {
        setEnvId(undefined);
        setEnvName('');
        setEnvDescription(undefined);
        modalRef.current.setOpen(true);
    }

    const onSaveEnv = (e: ChangeEvent<HTMLFormElement>) => {
        e.preventDefault();

        const toSave: Environment = {
            id: envId,
            name: envName,
            description: envDescription,
        }

        if (environments) {
            const hasDuplicateName = environments.some((env: Environment) => env.name === envName && (env.id !== envId || envId === undefined));
            if (hasDuplicateName) {
                toast.error(`There is a environment with ${envName} name. Please, select a different name.`)
                return;
            }
            const envsToUpdate = [...environments];
            const existsIndex = environments.findIndex((env: Environment) => ((env.id === envId && envId !== undefined)))
            if (existsIndex !== -1) {
                envsToUpdate[existsIndex] = toSave;
            } else {
                envsToUpdate.push(toSave)
            }
            setEnvironments(envsToUpdate)
        } else {
            setEnvironments([toSave]);
        }
        toast.warn("Environment saved. Don't forget to save the Namespace to persist.");
        modalRef.current.setOpen(false);
    }

    const onDeleteEnvironment = (environment: Environment) => {
        if (environments) {
            const existsIndex = environments.findIndex((env: Environment) => env.id === environment.id);
            if (existsIndex !== -1) {
                const envsToUpdate = [...environments];
                envsToUpdate.splice(existsIndex, 1);
                setEnvironments(envsToUpdate);
                toast.warn("Environment deleted. Don't forget to save the Namespace to persist.");
            }
        }
    }

    const onDeleteNamespace = () => {
        if (id) {
            setLoading(true);
            service.delete(id).then(() => {
                toast.success("Namespace deleted.")
                navigate('/admin/namespaces')
            }).finally(() => setLoading(false));
        }
    }

    return (
        <>
            <Card title={id !== 'new' ? name : 'Create namespace'}>
                <form className="flex flex-col gap-5" onSubmit={onFormSubmit}>
                    <div className="flex flex-col gap-2">
                        <FormLabel htmlFor="namespace-name" required>Name </FormLabel>
                        <Input id="namespace-name" name="namespace-name" type="text" value={name}
                            onChange={(e: ChangeEvent<HTMLInputElement>) => setName(e.target.value)} />
                    </div>
                    <div className="flex flex-col gap-2">
                        <label htmlFor="namespace-description">Description:</label>
                        <TextArea id="namespace-description" name="namespace-description" value={description}
                            onChange={(e: ChangeEvent<HTMLTextAreaElement>) => setDescription(e.target.value)}
                            rows={5} />
                    </div>
                    <div className="flex flex-col gap-2">
                        <div className="flex justify-between">
                            <FormLabel required>Environments</FormLabel>
                            <Button type="button" onClick={() => onAdd()}>Add Environment</Button>
                        </div>
                        {(environments && environments.length > 0) && environments.map((env: Environment) => {
                            return (
                                <ListItem name={env.name} onClick={() => onEdit(env)}
                                    onDelete={() => onDeleteEnvironment(env)} />
                            )
                        })}
                        <div className="flex justify-end items-center gap-1">
                            <CiCircleInfo />
                            <span className="text-sm">
                                Click to edit
                            </span>
                        </div>
                        <div className="flex justify-between">
                            <div className="flex gap-2">
                                <Button type="submit">Save</Button>
                                <Button type="button" variant="outline" onClick={() => navigate('/admin/namespaces')}>Cancel</Button>
                            </div>
                            <ConfirmDialog onConfirm={onDeleteNamespace}>
                                <Button variant="danger">Delete Namespace</Button>
                            </ConfirmDialog>
                        </div>
                    </div>
                </form>
                <Modal title={envId ? envName : 'Register environment'} onSave={onSaveEnv} saveText={envId ? 'Save' : 'Add'} ref={modalRef}>
                    <div className="flex flex-col gap-2">
                        <label htmlFor="env-name">Name:</label>
                        <Input id="env-name" name="env-name" type="text" value={envName}
                            onChange={(e: ChangeEvent<HTMLInputElement>) => setEnvName(e.target.value)} />
                    </div>
                    <div className="flex flex-col gap-2">
                        <label htmlFor="env-description">Description:</label>
                        <Input id="env-description" name="env-description" type="text" value={envDescription}
                            onChange={(e: ChangeEvent<HTMLInputElement>) => setEnvDescription(e.target.value)} />
                    </div>
                </Modal>
            </Card>
        </>
    )
}
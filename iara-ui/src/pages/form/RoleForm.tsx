import { useEffect, useRef, useState, type ChangeEvent } from "react";
import { useNavigate, useParams } from "react-router";
import { toast } from "react-toastify";
import Button from "../../components/Button";
import { ConfirmDialog } from "../../components/ConfirmDialog";
import FormLabel from "../../components/FormLabel";
import Input from "../../components/Input";
import ListItem from "../../components/ListItem";
import { Modal } from "../../components/Modal";
import Select from "../../components/Select";
import TextArea from "../../components/TextArea";
import { useLoading } from "../../providers/LoadingProvider";
import { PolicyService } from "../../services/PolicyService";
import { RoleService } from "../../services/RoleService";
import type { Page } from "../../types/Page";
import type { Policy } from "../../types/Policy";
import type { Role } from "../../types/Role";
import { uuid } from "../../utils/UUID";

export default function RoleForm() {
    const params = useParams();
    const navigate = useNavigate();
    const service = new RoleService();
    const policyService = new PolicyService();

    const [id, setId] = useState<string | undefined>();
    const [name, setName] = useState<string>('');
    const [description, setDescription] = useState<string | undefined>();
    const [policies, setPolicies] = useState<Policy[]>([]);

    const [policiesOptions, setPoliciesOptions] = useState<Policy[]>([]);
    const [selectedPolicy, setSelectedPolicy] = useState<Policy | undefined>();

    const { setLoading } = useLoading();

    const modalRef = useRef<any>(null);

    useEffect(() => {
        const id = params.id;
        if (id && id !== 'new') {
            setLoading(true);
            service.search({ id: id }).then((res: Page<Role>) => {
                const body = res.content[0];
                setId(body.id);
                setName(body.name);
                setDescription(body.description);
                setPolicies(body.policies);
            }).finally(() => setLoading(false));
        }
    }, [params.id, setLoading])

    const onFormSubmit = (e: ChangeEvent<HTMLFormElement>) => {
        e.preventDefault();

        const role: Role = {
            id: id,
            name: name,
            description: description,
            policies: policies
        }

        if (!role.name || role.name === '') {
            toast.error("Name field are required.")
            return;
        }

        if (!role.policies || role.policies.length === 0) {
            toast.error("At least one policy should be selected.")
            return;
        }

        setLoading(true);
        service.persist(role).then((res: Role) => {
            toast.success("Policy saved.")
            navigate(`/admin/roles/${res.id}`);
        }).finally(() => setLoading(false));
    }

    const onAddPolicy = () => {
        setLoading(true);
        policyService.search({}, 0, 2000).then((res: Page<Policy>) => {
            setPoliciesOptions(res.content.filter((p: Policy) => !policies.some((po: Policy) => po.id === p.id)));
            modalRef.current.setOpen(true);
        }).finally(() => setLoading(false));
    }

    const onAddNewPolicy = (e: ChangeEvent<HTMLFormElement>) => {
        e.preventDefault();
        if (selectedPolicy) {
            const alreadyExists = policies.some((policy: Policy) => policy.id === selectedPolicy.id);
            if (alreadyExists) {
                toast.error("This policy already exists in this Role.")
                return;
            }
            const toUpdate = [...policies];
            toUpdate.push(selectedPolicy);
            setPolicies(toUpdate);
            toast.warn("Policy added. Don't forget to save the role to persist the new policy.")
        }
        modalRef.current.setOpen(false);
    }

    const onRemovePolicy = (policy: Policy) => {
        if (policies) {
            const index = policies.findIndex((p: Policy) => p.id === policy.id);
            if (index !== -1) {
                const toUpdate = [...policies];
                toUpdate.splice(index, 1);
                setPolicies(toUpdate);
            }
        }
    }

    const onDeleteRole = () => {
        if (id) {
            setLoading(true);
            service.delete(id).then(() => {
                toast.success('Role deleted.')
                navigate('/admin/roles');
            }).finally(() => setLoading(false));
        }
    }

    return (
        <div className="flex flex-col gap-5">
            <h1 className="text-2xl">role</h1>
            <form className="flex flex-col gap-5" onSubmit={onFormSubmit}>
                <div className="flex flex-col gap-2">
                    <FormLabel htmlFor="role-name" required>name</FormLabel>
                    <Input id="role-name" name="role-name" type="text" value={name}
                        onChange={(e: ChangeEvent<HTMLInputElement>) => setName(e.target.value)} />
                </div>
                <div className="flex flex-col gap-2">
                    <FormLabel htmlFor="role-description">description</FormLabel>
                    <TextArea id="role-description" name="role-description" value={description}
                        onChange={(e: ChangeEvent<HTMLTextAreaElement>) => setDescription(e.target.value)} />
                </div>
                <div className="flex flex-col gap-2">
                    <div className="flex items-center justify-between">
                        <FormLabel required>policies</FormLabel>
                        <div className="flex">
                            <Button type="button" onClick={onAddPolicy}>add policy</Button>
                        </div>
                    </div>
                    {policies && policies.map((policy: Policy) => {
                        return (
                            <ListItem name={policy.name} onDelete={() => onRemovePolicy(policy)} key={uuid()} />
                        )
                    })}
                </div>
                <div className="flex justify-between">
                    <div className="flex gap-2">
                        <Button>save</Button>
                        <Button variant="outline" type="button" onClick={() => navigate('/admin/roles')}>back</Button>
                    </div>
                    <ConfirmDialog onConfirm={() => onDeleteRole()}>
                        <Button variant="danger" type="button">delete role</Button>
                    </ConfirmDialog>
                </div>
            </form>
            <Modal title="add policy" ref={modalRef} onSave={onAddNewPolicy}>
                <div className="flex flex-col gap-2">
                    <FormLabel required>policy</FormLabel>
                    <Select options={policiesOptions}
                        onChange={(e: ChangeEvent<HTMLSelectElement>) => setSelectedPolicy(JSON.parse(e.target.value))} />
                </div>
            </Modal>
        </div>
    )
}
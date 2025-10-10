import { useEffect, useRef, useState, type ChangeEvent, type FormEvent } from "react";
import { useNavigate, useParams } from "react-router";
import { toast } from "react-toastify";
import Button from "../../components/Button";
import Checkbox from "../../components/Checkbox";
import { ConfirmDialog } from "../../components/ConfirmDialog";
import FormLabel from "../../components/FormLabel";
import Input from "../../components/Input";
import ListItem from "../../components/ListItem";
import { Modal } from "../../components/Modal";
import Select from "../../components/Select";
import { useLoading } from "../../providers/LoadingProvider";
import { RoleService } from "../../services/RoleService";
import { UserService } from "../../services/UserService";
import type { Page } from "../../types/Page";
import type { Role } from "../../types/Role";
import type { User } from "../../types/User";
import { uuid } from "../../utils/UUID";
import Card from "../../components/Card";

export default function UsersForm() {
    const navigate = useNavigate();
    const params = useParams();
    const service = new UserService();
    const roleService = new RoleService();

    const [id, setId] = useState<string | undefined>();
    const [name, setName] = useState<string>('');
    const [email, setEmail] = useState<string>('');
    const [picture, setPicture] = useState<string | undefined>();
    const [roles, setRoles] = useState<Role[]>([]);
    const [isSSO, setIsSSO] = useState<boolean>(false);

    const [rolesOptions, setRolesOptions] = useState<Role[]>([]);
    const [selectedRole, setSelectedRole] = useState<Role>();

    const addRoleModal = useRef<any>(null);

    const { setLoading } = useLoading();

    useEffect(() => {
        const id = params.id;
        if (id && id !== 'new') {
            setLoading(true);
            service.search({ id: id }).then((res: Page<User>) => {
                setId(res.content[0].id)
                setName(res.content[0].name);
                setEmail(res.content[0].email);
                setPicture(res.content[0].picture)
                setRoles(res.content[0].roles);
                setIsSSO(res.content[0].isSSO)
            }).finally(() => setLoading(false));
        }
    }, [params.id, setLoading]);

    const onFormSubmit = (event: FormEvent<HTMLFormElement>) => {
        event.preventDefault();

        const usr: User = {
            id: id,
            name: name,
            email: email,
            picture: picture,
            roles: roles,
            isSSO: isSSO
        }

        setLoading(true);
        service.persist(usr).then((res: User) => {
            toast.success("User saved.")
            navigate(`/admin/users/${res.id}`)
        }).finally(() => setLoading(false));
    }

    const onDeleteUser = () => {
        if (id) {
            setLoading(true);
            service.delete(id).then(() => {
                toast.success("User deleted.")
                navigate('/admin/users')
            }).finally(() => setLoading(false));
        }
    }

    const onUserResetPassword = () => {
        if (id) {
            setLoading(true);
            service.resetPassword(id).then(() => {
                toast.success("User password reseted.")
            }).finally(() => setLoading(false));
        }
    }

    const onAddRoleModal = () => {
        setLoading(true);
        roleService.search({}, 0, 2000).then((res: Page<Role>) => {
            setRolesOptions(res.content.filter((r: Role) => !roles.some((e: Role) => r.id === e.id)));
        }).finally(() => setLoading(false));
        addRoleModal.current.setOpen(true);
    }

    const onAddNewRole = (e: ChangeEvent<HTMLFormElement>) => {
        e.preventDefault();
        if (selectedRole) {
            const alreadyExists = roles.some((role: Role) => role.id === selectedRole.id);
            if (alreadyExists) {
                toast.error("This role already exists in this User.")
                return;
            }
            const toUpdate = [...roles];
            toUpdate.push(selectedRole);
            setRoles(toUpdate);
            toast.warn("Role added. Don't forget to save the user to persist the new role.")
        }
        addRoleModal.current.setOpen(false);
    }

    const onDeleteRole = (role: Role) => {
        const toUpdate = [...roles];

        if (toUpdate.length > 0) {
            const index = toUpdate.findIndex((r: Role) => r.id === role.id);
            if (index !== -1) {
                toUpdate.splice(index, 1);
                setRoles(toUpdate);
                toast.warn("Role removed. Don't forget to save the user to persist the removal.")
            }
        }
    }

    return (
        <div className="flex flex-col gap-5">
            <h1>User</h1>
            <Card>
                <div className="flex flex-col gap-5">
                    <form className="flex flex-col gap-5" onSubmit={onFormSubmit}>
                        <div className="flex flex-col gap-2">
                            <FormLabel htmlFor="user-name" required>Name</FormLabel>
                            <Input id="user-name" name="user-name" type="text" value={name}
                                onChange={(e: ChangeEvent<HTMLInputElement>) => setName(e.target.value)} />
                        </div>
                        <div className="flex flex-col gap-2">
                            <FormLabel htmlFor="user-email" required>Email</FormLabel>
                            <Input id="user-email" name="user-email" value={email} type="email"
                                onChange={(e: ChangeEvent<HTMLInputElement>) => setEmail(e.target.value)} />
                        </div>
                        <div className="flex flex-col gap-2">
                            <FormLabel htmlFor="user-picture">Picture</FormLabel>
                            <Input id="user-picture" name="user-picture" value={picture} type="text"
                                onChange={(e: ChangeEvent<HTMLInputElement>) => setPicture(e.target.value)} />
                        </div>
                        {!isSSO &&
                            <div className="flex flex-col gap-2">
                                <FormLabel>Password</FormLabel>
                                <ConfirmDialog onConfirm={onUserResetPassword}
                                    description="By confirming, the user password will be reset and a new one will be sent by e-mail.">
                                    <Button type="button" variant="outline">Reset password</Button>
                                </ConfirmDialog>
                            </div>
                        }
                        <div className="flex flex-col gap-2">
                            <FormLabel>Single Sign-on User</FormLabel>
                            <Checkbox value={isSSO} onChange={() => setIsSSO(!isSSO)} />
                        </div>
                        <div className="flex flex-col gap-2">
                            <div className="flex justify-between">
                                <FormLabel required>Roles</FormLabel>
                                <Button type="button" onClick={onAddRoleModal}>Add role</Button>
                            </div>
                            {roles && roles.map((role: Role) => {
                                return (
                                    <ListItem name={role.name} key={uuid()} onDelete={() => onDeleteRole(role)}></ListItem>
                                )
                            })}
                        </div>
                        <div className="flex justify-between">
                            <div className="flex gap-2">
                                <Button type="submit">Save</Button>
                                <Button type="button" variant="outline" onClick={() => navigate('/admin/users')}>Back</Button>
                            </div>
                            <ConfirmDialog onConfirm={onDeleteUser}>
                                <Button variant="danger">Delete</Button>
                            </ConfirmDialog>
                        </div>
                    </form>
                    <Modal ref={addRoleModal} title="Add role" saveText="Add" onSave={onAddNewRole}>
                        <div className="flex flex-col gap-2">
                            <FormLabel htmlFor="role-select" required>Roles</FormLabel>
                            <Select options={rolesOptions}
                                onChange={(e: ChangeEvent<HTMLSelectElement>) => setSelectedRole(JSON.parse(e.target.value))} />
                        </div>
                    </Modal>
                </div>
            </Card>
        </div>
    )
}
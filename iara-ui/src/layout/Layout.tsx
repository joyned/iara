import { useEffect, useRef, useState, type ChangeEvent } from "react";
import { FaCaretDown } from "react-icons/fa";
import { Outlet, useLocation, useNavigate } from "react-router";
import { ToastContainer } from "react-toastify";
import Logo from '../assets/logo-name-white.svg?react';
import Nobody from '../assets/nobody.svg?react';
import Button from "../components/Button";
import Dropdown from "../components/Dropdown";
import Loading from "../components/Loading";
import { Modal } from "../components/Modal";
import Select from "../components/Select";
import { useEnvironment } from "../providers/EnvironmentProvider";
import { useLoading } from "../providers/LoadingProvider";
import { useNamespace } from "../providers/NamespaceProvider";
import { EnvironmentService } from "../services/EnvironmentService";
import eventBus from "../services/EventBusService";
import { NamespaceService } from "../services/NamespaceService";
import { UserService } from "../services/UserService";
import type { Environment } from "../types/Environment";
import type { Namespace } from "../types/Namespace";
import type { Page } from "../types/Page";
import type { Role } from "../types/Role";
import type { User } from "../types/User";
import { hasAccessTo } from "../utils/PermissionUtils";
import { CgLogOut } from "react-icons/cg";
import { IoIosSettings } from "react-icons/io";

export default function Layout() {
    const userService = new UserService();
    const { fetch: originalFetch } = window;
    const navigate = useNavigate();
    const location = useLocation();
    const currentPath = location.pathname;
    const envModalRef = useRef<any>(null);

    const namespaceService = new NamespaceService();
    const environmentService = new EnvironmentService();

    const [namespaceOptions, setNamespaceOptions] = useState<Namespace[]>();
    const [environmentOptions, setEnvironmentOptions] = useState<Environment[]>();

    const { namespace, setNamespace } = useNamespace();
    const { environment, setEnvironment } = useEnvironment();

    const { loading } = useLoading();

    const [name, setName] = useState<string>();
    const [picture, setPicture] = useState<string>();

    const [hasNamespace, setHasNamespace] = useState<boolean>(false);
    const [hasUsers, setHasUsers] = useState<boolean>(false);
    const [hasPolicy, setHasPolicy] = useState<boolean>(false);
    const [hasRoles, setHasRoles] = useState<boolean>(false);
    const [hasTokens, setHasTokens] = useState<boolean>(false);
    const [hasGeneral, setHasGeneral] = useState<boolean>(false);

    const [dropdownMenu, _] = useState<any>([
        {
            icon: <IoIosSettings />,
            name: 'Settings',
            to: '/user/settings'
        },
        {
            icon: <CgLogOut />,
            name: 'Logout',
            onClick: () => logout()
        }
    ])

    useEffect(() => {
        if (!namespace) {
            onEnvModalOpen();
        }

        userService.me().then((res: User) => {
            setName(res.name);
            setPicture(res.picture);
            res.roles.forEach((role: Role) => {
                setHasNamespace(prev => prev || hasAccessTo(role, '#NAMESPACES'));
                setHasUsers(prev => prev || hasAccessTo(role, '#USERS'));
                setHasPolicy(prev => prev || hasAccessTo(role, '#POLICY'));
                setHasRoles(prev => prev || hasAccessTo(role, '#ROLES'));
                setHasTokens(prev => prev || hasAccessTo(role, '#TOKENS'));
                setHasGeneral(prev => prev || hasAccessTo(role, '#GENERAL'));
            })
        });
    }, [])

    const onEnvModalOpen = () => {
        if (namespace && namespace.id) {
            searchEnvironments(namespace.id);
        }

        namespaceService.search({}, 0, 2000).then((res: Page<Namespace>) => {
            setNamespaceOptions(res.content);
        }).finally(() => envModalRef.current.setOpen(true));
    }

    const onNamespaceSelect = (namespace: Namespace) => {
        searchEnvironments(namespace.id || '');
        setNamespace(namespace)
        setEnvironment(JSON.parse('{}'))
    }

    const searchEnvironments = (namespaceId: string) => {
        environmentService.search({ namespace: namespaceId }).then((res: Page<Environment>) => {
            setEnvironmentOptions(res.content);
        })
    }

    const beforeSaveModal = (e: ChangeEvent<HTMLFormElement>) => {
        e.preventDefault();
        if (JSON.stringify(environment) === '{}') {
            console.log('Select an Environment')
        } else {
            eventBus.emit('namespaceEnvironmentChange', { namespace: namespace, environment: environment })
            envModalRef.current.setOpen(false);
        }
    }

    const logout = () => {
        localStorage.removeItem('access_token');
        navigate('/login')
    }

    window.fetch = async (...args) => {
        try {
            const response = await originalFetch(...args);

            if (response.status === 401) {
                const body = await response.json();
                if (body.key === "UNAUTHORIZED") {
                    localStorage.removeItem('access_token')
                    navigate('/login')
                }
            }

            return response;
        } catch (error) {
            console.log(error)
            throw error;
        }
    };

    return (
        <>
            <ToastContainer />
            {loading && <Loading />}
            <div className="flex justify-between items-center w-full bg-primary-color p-1 h-[60px]">
                <div className="flex items-center gap-10 text-white">
                    <Logo className="w-[100px] h-[40px] cursor-pointer" onClick={() => navigate('/kv')} />
                    <span className={`cursor-pointer pl-3 pr-3 pt-1 pb-1 rounded-sm hover:bg-primary-darker-color ${currentPath === '/kv' && 'bg-primary-darker-color'}`}
                        onClick={() => navigate('/kv')}>
                        K/V
                    </span>
                    <span className={`cursor-pointer pl-3 pr-3 pt-1 pb-1 rounded-sm hover:bg-primary-darker-color ${currentPath === '/secrets' && 'bg-primary-darker-color'}`}
                        onClick={() => navigate('/secrets')}>
                        Secrets
                    </span>
                    {(hasNamespace || hasUsers || hasPolicy || hasRoles || hasTokens || hasGeneral) &&
                        <>
                            <span className={`pl-3 pr-3 pt-1 pb-1 rounded-sm`}>
                                |
                            </span>
                            {hasNamespace &&
                                <span className={`cursor-pointer pl-3 pr-3 pt-1 pb-1 rounded-sm hover:bg-primary-darker-color ${currentPath === '/admin/namespaces' && 'bg-primary-darker-color'}`}
                                    onClick={() => navigate('/admin/namespaces')}>
                                    Namespaces
                                </span>
                            }
                            {hasUsers &&
                                <span className={`cursor-pointer pl-3 pr-3 pt-1 pb-1 rounded-sm hover:bg-primary-darker-color ${currentPath === '/admin/users' && 'bg-primary-darker-color'}`}
                                    onClick={() => navigate('/admin/users')}>
                                    Users
                                </span>
                            }
                            {hasPolicy &&
                                <span className={`cursor-pointer pl-3 pr-3 pt-1 pb-1 rounded-sm hover:bg-primary-darker-color ${currentPath === '/admin/policies' && 'bg-primary-darker-color'}`}
                                    onClick={() => navigate('/admin/policies')}>
                                    Policies
                                </span>
                            }
                            {hasRoles &&
                                <span className={`cursor-pointer pl-3 pr-3 pt-1 pb-1 rounded-sm hover:bg-primary-darker-color ${currentPath === '/admin/roles' && 'bg-primary-darker-color'}`}
                                    onClick={() => navigate('/admin/roles')}>
                                    Roles
                                </span>
                            }
                            {hasTokens &&
                                <span className={`cursor-pointer pl-3 pr-3 pt-1 pb-1 rounded-sm hover:bg-primary-darker-color ${currentPath === '/admin/tokens' && 'bg-primary-darker-color'}`}
                                    onClick={() => navigate('/admin/tokens')}>
                                    Tokens
                                </span>
                            }
                            {hasGeneral &&
                                <span className={`cursor-pointer pl-3 pr-3 pt-1 pb-1 rounded-sm hover:bg-primary-darker-color ${currentPath === '/admin/general' && 'bg-primary-darker-color'}`}
                                    onClick={() => navigate('/admin/general')}>
                                    General
                                </span>
                            }
                        </>
                    }
                </div>
                <div className="flex gap-4 justify-between text-white min-w-96">
                    <Button onClick={onEnvModalOpen}>Change Namespace / Environment</Button>
                    <Dropdown items={dropdownMenu}>
                        <div className="flex items-center gap-2">
                            {picture ? <img src={picture} alt="user-profile-picture" className="w-[40px]" /> : <Nobody className="w-[40px]" />}
                            <span>{name}</span>
                            <FaCaretDown />
                        </div>
                    </Dropdown>
                </div>
            </div >
            <main className="p-6">
                <div className="flex flex-col gap-5">
                    <div>
                        <Outlet></Outlet>
                    </div>
                </div>
            </main>
            <Modal title="Select your Environment" ref={envModalRef} onSave={beforeSaveModal} >
                <div className="flex flex-col gap-5">
                    <div className="flex flex-col gap-1">
                        <span className="font-medium">Namespace:</span>
                        <Select options={namespaceOptions || []} optionLabel="name" value={JSON.stringify(namespace)}
                            onChange={(e: ChangeEvent<HTMLSelectElement>) => onNamespaceSelect(JSON.parse(e.target.value))} />
                    </div>
                    <div className="flex flex-col gap-1">
                        <div className="font-medium">Environment:</div>
                        <Select options={environmentOptions || []} optionLabel="name" value={JSON.stringify(environment)}
                            onChange={(e: ChangeEvent<HTMLSelectElement>) => setEnvironment(JSON.parse(e.target.value))} />
                    </div>
                </div>
            </Modal>
        </>
    )
}
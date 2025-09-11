import { useEffect, useRef, useState, type ChangeEvent } from "react";
import { CgLogOut } from "react-icons/cg";
import { IoIosSettings } from "react-icons/io";
import { useNavigate } from "react-router";
import { ToastContainer } from "react-toastify";
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
import SideMenu from "./SideMenu";
import TopMenu from "./TopMenu";

export default function Layout() {
    const userService = new UserService();
    const { fetch: originalFetch } = window;
    const navigate = useNavigate();
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
    const [roles, setRoles] = useState<Role[]>([]);

    const [isMobile, setIsMobile] = useState<boolean>(() => {
        return window.innerWidth < 1548;
    })

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
            setRoles(res.roles);
        });

        const handleResize = () => {
            setIsMobile(() => window.innerWidth < 1548);
        };

        window.addEventListener('resize', handleResize);

        return () => {
            window.removeEventListener('resize', handleResize);
        };

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
        localStorage.removeItem('namespace');
        localStorage.removeItem('environment');
        setNamespace(undefined);
        setEnvironment(undefined);
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
            {!isMobile &&
                <TopMenu name={name}
                    picture={picture}
                    roles={roles}
                    onEnvModalOpen={onEnvModalOpen}
                    dropdownMenu={dropdownMenu}
                />
            }
            {isMobile &&
                <SideMenu name={name}
                    picture={picture}
                    roles={roles}
                    onEnvModalOpen={onEnvModalOpen}
                    dropdownMenu={dropdownMenu}
                />
            }
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
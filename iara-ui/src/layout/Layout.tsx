import { useEffect, useRef, useState, type ChangeEvent } from "react";
import { CgLogOut } from "react-icons/cg";
import { GrConfigure } from "react-icons/gr";
import { IoIosSettings } from "react-icons/io";
import { RiExpandLeftLine, RiExpandRightLine } from "react-icons/ri";
import { Outlet, useNavigate } from "react-router";
import { ToastContainer } from "react-toastify";
import LogoName from '../assets/logo-name-white.svg?react';
import Logo from '../assets/logo-white.svg?react';
import Button from "../components/Button";
import Loading from "../components/Loading";
import { Modal } from "../components/Modal";
import Select from "../components/Select";
import Tooltip from "../components/Tooltip";
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
import { hasAccessToBatch } from "../utils/PermissionUtils";
import { uuid } from "../utils/UUID";
import { IaraMenu } from "./IaraMenu";

export default function Layout() {
    const userService = new UserService();
    const navigate = useNavigate();
    const envModalRef = useRef<any>(null);

    const namespaceService = new NamespaceService();
    const environmentService = new EnvironmentService();

    const [namespaceOptions, setNamespaceOptions] = useState<Namespace[]>();
    const [environmentOptions, setEnvironmentOptions] = useState<Environment[]>();

    const { namespace, setNamespace } = useNamespace();
    const { environment, setEnvironment } = useEnvironment();

    const { loading } = useLoading();


    const [isMobile, setIsMobile] = useState<boolean>(() => {
        return window.innerWidth < 1548;
    })

    const [isMenuOpen, setIsMenuOpen] = useState<boolean>(!isMobile);

    const [name, setName] = useState<string>();
    const [roles, setRoles] = useState<Role[]>([]);

    useEffect(() => {
        if (!namespace) {
            onEnvModalOpen();
        }

        userService.me().then((res: User) => {
            setName(res.name);
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

    const onNavigate = (to: string) => {
        navigate(to);
        if (isMobile) {
            setIsMenuOpen(false);
        }
    }

    return (
        <>
            <ToastContainer />
            {loading && <Loading />}
            <div className="relative min-w-screen max-w-screen h-screen">
                <div className={`absolute text-lg z-50 p-1 bg-primary-color text-white rounded left-[55px] top-[5px] cursor-pointer 
                    ${isMenuOpen ? 'left-[235px]' : 'left-[55px]'}`}
                    onClick={() => setIsMenuOpen(!isMenuOpen)}
                    style={{ transition: '300ms cubic-bezier(0.25, 0.8, 0.25, 1)' }}>
                    {isMenuOpen ? <RiExpandLeftLine /> : <RiExpandRightLine />}
                </div>
                <div className={`fixed h-screen bg-primary-color z-40 ${isMenuOpen ? 'w-[250px]' : 'w-[70px]'}`}
                    style={{ transition: '300ms cubic-bezier(0.25, 0.8, 0.25, 1)' }}>
                    <div className="flex flex-col gap-2 justify-between h-full">
                        <div>
                            <div className="flex justify-center items-center h-[120px]">
                                {isMenuOpen ? <LogoName className='h-[100px]' /> : <Logo className='h-[100px]' />}
                            </div>
                            <div className="flex flex-col gap-5 p-3">
                                {IaraMenu.map((item: any) => {
                                    return (
                                        <div key={uuid()}>
                                            {hasAccessToBatch(roles, item.key) &&
                                                <div className={`flex gap-2 text-white p-2 hover:bg-primary-darker-color hover:rounded hover:cursor-pointer ${!isMenuOpen && 'text-2xl'}`}
                                                    onClick={() => onNavigate(item.to)} key={uuid()}>
                                                    {isMenuOpen ?
                                                        <div className="flex items-center gap-4">
                                                            {item.icon}
                                                            {item.name}
                                                        </div>
                                                        :
                                                        <Tooltip text={item.name}>
                                                            {item.icon}
                                                        </Tooltip>
                                                    }
                                                </div>
                                            }
                                        </div>
                                    )
                                })}
                            </div>
                        </div>
                        <div className='mb-10 flex flex-col gap-5 items-center'>
                            {isMenuOpen && <Button onClick={onEnvModalOpen}>Change Namespace / Environment</Button>}
                            {!isMenuOpen && <GrConfigure className='text-2xl text-white' onClick={onEnvModalOpen} />}

                            {isMenuOpen &&
                                <div className="flex">
                                    <span className='text-white'>{name}</span>
                                </div>
                            }
                            <div className={`flex items-center gap-5 ${!isMenuOpen && 'flex-col'}`}>
                                <div className="flex items-center gap-2 text-3xl cursor-pointer" onClick={() => navigate('/user/settings')}>
                                    <IoIosSettings className='text-white' />
                                </div>
                                <div className="flex items-center gap-2 text-3xl cursor-pointer" onClick={logout}>
                                    <CgLogOut className='text-white' />
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <main className={`p-6 ${isMobile ? 'ml-[80px]' : isMenuOpen ? 'ml-[270px]' : 'ml-[80px]'}`}
                    style={{ transition: '300ms cubic-bezier(0.25, 0.8, 0.25, 1)' }}>
                    <Outlet></Outlet>
                </main>
            </div>
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
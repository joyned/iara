import { useEffect, useState } from "react";
import { FaCaretDown, FaFolder, FaIdCard, FaKey, FaUsers } from "react-icons/fa";
import { IoIosSettings, IoMdLogOut, IoMdMenu } from "react-icons/io";
import { MdOutlineGeneratingTokens, MdOutlinePolicy } from "react-icons/md";
import { VscSymbolNamespace } from "react-icons/vsc";
import { Outlet, useLocation, useNavigate } from "react-router";
import { ToastContainer } from "react-toastify";
import Dropdown from "../components/Dropdown";
import Loading from "../components/Loading";
import NamespaceEnvironment from "../components/NamespaceEnvironment";
import { useLoading } from "../providers/LoadingProvider";
import { UserService } from "../services/UserService";
import type { Role } from "../types/Role";
import type { User } from "../types/User";
import { hasAccessToBatch } from "../utils/PermissionUtils";
import { LoginService } from "../services/LoginService";

export default function Layout() {
    const location = useLocation();
    const userService = new UserService();
    const loginService = new LoginService();
    const navigate = useNavigate();

    const { loading } = useLoading();

    const [isMenuOpen, setIsMenuOpen] = useState<boolean>(false);

    const [name, setName] = useState<string>();
    const [roles, setRoles] = useState<Role[]>([]);

    const { fetch: originalFetch } = window;

    const [isMobile, setIsMobile] = useState<boolean>(window.innerWidth < 1150);

    const userDropdown = [
        {
            name: 'settings',
            to: '/user/settings',
            icon: <IoIosSettings />
        },
        {
            name: 'logout',
            onClick: () => logout(),
            icon: <IoMdLogOut />
        }
    ]

    useEffect(() => {
        userService.me().then((res: User) => {
            setName(res.name);
            setRoles(res.roles);
        });

        function handleWindowResize() {
            setIsMobile(window.innerWidth < 1150);
        }

        window.addEventListener('resize', handleWindowResize)

        return () => window.removeEventListener('resize', handleWindowResize);
    }, [])

    window.fetch = async (...args) => {
        const [resource, config] = args;

        const response = await originalFetch(resource, config);
        if (response.status === 401) {
            const body = await response.json();
            if (body.key === 'EXPIRED_JWT' || body.key === 'TOKEN_MISSING') {
                logout();
            }
        }
        return response;
    };


    const logout = () => {
        localStorage.removeItem('access_token');
        localStorage.removeItem('namespace');
        localStorage.removeItem('environment');
        loginService.doLogout().finally(() => {
            navigate('/login')
        })
    }

    const onNavigate = (to: string) => {
        navigate(to);
        setIsMenuOpen(false);
    }

    const hasAccessAnyAdminResource = () => {
        return hasAccessToBatch(roles, '#NAMESPACES') || hasAccessToBatch(roles, '#POLICIES') ||
            hasAccessToBatch(roles, '#USERS') || hasAccessToBatch(roles, '#ROLES') || hasAccessToBatch(roles, '#TOKENS') ||
            hasAccessToBatch(roles, '#GENERAL')
    }

    return (
        <>
            <ToastContainer />
            {loading && <Loading />}
            <div className="flex items-center w-full min-h-13 max-h-13 bg-primary-color">
                <div className="w-full flex justify-between items-center pl-5 pr-5">
                    {isMobile &&
                        <div className="relative">
                            <IoMdMenu className="text-2xl text-white cursor-pointer" onClick={() => setIsMenuOpen(!isMenuOpen)} />
                            <div className={`absolute flex flex-col gap-5 justify-between w-[300px] -left-6 h-full bg-primary-color p-5 shadow ${isMenuOpen ? 'z-50' : 'opacity-0 -z-10'}`}
                                style={{ transition: '300ms cubic-bezier(0.25, 0.8, 0.25, 1)', height: 'calc(100vh - 64px)' }}>
                                <div className="flex flex-col gap-5">
                                    <div className="flex flex-col gap-5 text-white text-md">
                                        <div className="flex gap-4 items-center p-2 pl-4 pr-4 cursor-pointer hover:bg-primary-lighter-color rounded-2xl"
                                            onClick={() => onNavigate('/kv')}>
                                            <FaFolder />
                                            <span>kv</span>
                                        </div>
                                        <div className="flex gap-4 items-center p-2 pl-4 pr-4 cursor-pointer hover:bg-primary-lighter-color rounded-2xl"
                                            onClick={() => onNavigate('/secrets')}>
                                            <FaKey />
                                            <span>secrets</span>
                                        </div>
                                    </div>
                                    {hasAccessAnyAdminResource() && <hr className="text-white" />}
                                    <div className="flex flex-col gap-5 text-white text-md">
                                        {hasAccessToBatch(roles, '#NAMESPACES') &&
                                            <div className="flex gap-4 items-center p-2 pl-4 pr-4 cursor-pointer hover:bg-primary-lighter-color rounded-2xl"
                                                onClick={() => onNavigate('/admin/namespaces')}>
                                                <VscSymbolNamespace />
                                                <span>namespaces</span>
                                            </div>
                                        }
                                        {hasAccessToBatch(roles, '#POLICIES') &&
                                            <div className="flex gap-4 items-center p-2 pl-4 pr-4 cursor-pointer hover:bg-primary-lighter-color rounded-2xl"
                                                onClick={() => onNavigate('/admin/policies')}>
                                                <MdOutlinePolicy />
                                                <span>policy</span>
                                            </div>
                                        }
                                        {hasAccessToBatch(roles, '#USERS') &&
                                            <div className="flex gap-4 items-center p-2 pl-4 pr-4 cursor-pointer hover:bg-primary-lighter-color rounded-2xl"
                                                onClick={() => onNavigate('/admin/users')}>
                                                <FaUsers />
                                                <span>user</span>
                                            </div>
                                        }
                                        {hasAccessToBatch(roles, '#ROLES') &&
                                            <div className="flex gap-4 items-center p-2 pl-4 pr-4 cursor-pointer hover:bg-primary-lighter-color rounded-2xl"
                                                onClick={() => onNavigate('/admin/roles')}>
                                                <FaIdCard />
                                                <span>roles</span>
                                            </div>
                                        }
                                        {hasAccessToBatch(roles, '#TOKENS') &&
                                            <div className="flex gap-4 items-center p-2 pl-4 pr-4 cursor-pointer hover:bg-primary-lighter-color rounded-2xl"
                                                onClick={() => onNavigate('/admin/tokens')}>
                                                <MdOutlineGeneratingTokens />
                                                <span>tokens</span>
                                            </div>
                                        }
                                        {hasAccessToBatch(roles, '#GENERAL') &&
                                            <div className="flex gap-4 items-center p-2 pl-4 pr-4 cursor-pointer hover:bg-primary-lighter-color rounded-2xl"
                                                onClick={() => onNavigate('/admin/general')}>
                                                <IoIosSettings />
                                                <span>general</span>
                                            </div>
                                        }
                                    </div>
                                </div>
                            </div>
                        </div>
                    }
                    <span className="text-title text-white text-4xl cursor-pointer" onClick={() => navigate('/kv')}>IARA</span>
                    {!isMobile &&
                        <div className="flex items-center gap-5">
                            <div className="flex items-center gap-5 text-white">
                                <span className={`pl-5 pr-5 p-1 rounded hover:bg-primary-lighter-color cursor-pointer 
                                ${location.pathname === '/kv' && "bg-primary-lighter-color"}`} onClick={() => onNavigate('/kv')}>
                                    kv
                                </span>
                                <span className={`pl-5 pr-5 p-1 rounded hover:bg-primary-lighter-color cursor-pointer 
                                ${location.pathname === '/secrets' && "bg-primary-lighter-color"}`} onClick={() => onNavigate('/secrets')}>
                                    secrets
                                </span>
                            </div>
                            <span className="text-white">|</span>
                            <div className="flex items-center gap-5 text-white">
                                {hasAccessToBatch(roles, '#NAMESPACES') &&
                                    <div className={`pl-5 pr-5 p-1 rounded hover:bg-primary-lighter-color cursor-pointer 
                                    ${location.pathname === '/admin/namespaces' && "bg-primary-lighter-color"}`}
                                        onClick={() => onNavigate('/admin/namespaces')}>
                                        <span>namespace</span>
                                    </div>
                                }
                                {hasAccessToBatch(roles, '#POLICIES') &&
                                    <div className={`pl-5 pr-5 p-1 rounded hover:bg-primary-lighter-color cursor-pointer 
                                    ${location.pathname === '/admin/policies' && "bg-primary-lighter-color"}`}
                                        onClick={() => onNavigate('/admin/policies')}>
                                        <span>policy</span>
                                    </div>
                                }
                                {hasAccessToBatch(roles, '#USERS') &&
                                    <div className={`pl-5 pr-5 p-1 rounded hover:bg-primary-lighter-color cursor-pointer 
                                    ${location.pathname === '/admin/users' && "bg-primary-lighter-color"}`}
                                        onClick={() => onNavigate('/admin/users')}>
                                        <span>user</span>
                                    </div>
                                }
                                {hasAccessToBatch(roles, '#ROLES') &&
                                    <div className={`pl-5 pr-5 p-1 rounded hover:bg-primary-lighter-color cursor-pointer 
                                    ${location.pathname === '/admin/roles' && "bg-primary-lighter-color"}`}
                                        onClick={() => onNavigate('/admin/roles')}>
                                        <span>roles</span>
                                    </div>
                                }
                                {hasAccessToBatch(roles, '#TOKENS') &&
                                    <div className={`pl-5 pr-5 p-1 rounded hover:bg-primary-lighter-color cursor-pointer 
                                    ${location.pathname === '/admin/tokens' && "bg-primary-lighter-color"}`}
                                        onClick={() => onNavigate('/admin/tokens')}>
                                        <span>tokens</span>
                                    </div>
                                }
                                {hasAccessToBatch(roles, '#GENERAL') &&
                                    <div className={`pl-5 pr-5 p-1 rounded hover:bg-primary-lighter-color cursor-pointer 
                                    ${location.pathname === '/admin/general' && "bg-primary-lighter-color"}`}
                                        onClick={() => onNavigate('/admin/general')}>
                                        <span>general</span>
                                    </div>
                                }
                            </div>
                        </div>
                    }
                    <Dropdown items={userDropdown}>
                        <div className="flex items-center gap-3 text-white">
                            {name}
                            <FaCaretDown />
                        </div>
                    </Dropdown>
                </div>
            </div>

            <div className="m-10">
                <NamespaceEnvironment />
                <Outlet />
            </div>
        </>
    )
}
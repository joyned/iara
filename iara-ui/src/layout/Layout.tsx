import { useEffect, useState } from "react";
import { BiSolidUserCircle } from "react-icons/bi";
import { FaCaretDown, FaFolder, FaIdCard, FaKey, FaUsers } from "react-icons/fa";
import { IoIosSettings, IoMdLogOut, IoMdMenu } from "react-icons/io";
import { MdOutlineGeneratingTokens, MdOutlinePolicy } from "react-icons/md";
import { VscSymbolNamespace } from "react-icons/vsc";
import { Outlet, useLocation, useNavigate } from "react-router";
import { ToastContainer } from "react-toastify";
import Dropdown from "../components/Dropdown";
import Loading from "../components/Loading";
import { useLoading } from "../providers/LoadingProvider";
import { LoginService } from "../services/LoginService";
import { UserService } from "../services/UserService";
import type { Role } from "../types/Role";
import type { User } from "../types/User";
import { hasAccessToBatch } from "../utils/PermissionUtils";
import NamespaceEnvironment from "../components/NamespaceEnvironment";

export default function Layout() {
    const location = useLocation();
    const userService = new UserService();
    const loginService = new LoginService();
    const navigate = useNavigate();

    const { loading } = useLoading();

    const [isMenuOpen, setIsMenuOpen] = useState<boolean>(window.innerWidth > 1150);
    const [isMobile, setIsMobile] = useState<boolean>(window.innerWidth < 1150);

    const [name, setName] = useState<string>();
    const [email, setEmail] = useState<string>();
    const [picture, setPicture] = useState<string>();
    const [roles, setRoles] = useState<Role[]>([]);


    const { fetch: originalFetch } = window;

    const userDropdown = [
        {
            name: 'Settings',
            to: '/user/settings',
            icon: <IoIosSettings />
        },
        {
            name: 'Logout',
            onClick: () => logout(),
            icon: <IoMdLogOut />
        }
    ]

    useEffect(() => {
        userService.me().then((res: User) => {
            setName(res.name);
            setEmail(res.email);
            setPicture(res.picture);
            setRoles(res.roles);
        });

        function handleWindowResize() {
            setIsMenuOpen(window.innerWidth > 1150);
            setIsMobile(window.innerWidth < 1150)
        }

        window.addEventListener('resize', handleWindowResize)

        return () => window.removeEventListener('resize', handleWindowResize);
    }, [])

    const match = (uri: string) => {
        return location.pathname.startsWith(uri)
    }

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
        if (isMobile) {
            setIsMenuOpen(false);
        }
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
            <div className="fixed top-0 flex items-center w-full min-h-13 max-h-13 bg-primary-color">
                <div className="w-full flex justify-between items-center pl-5 pr-5">
                    <div className="relative">
                        <IoMdMenu className="text-2xl text-white cursor-pointer" onClick={() => setIsMenuOpen(!isMenuOpen)} />
                        <div className={`flex flex-col gap-5 justify-between w-[244px] -left-6 h-full bg-primary-color p-5 shadow 
                            ${isMenuOpen ? 'absolute' : 'hidden'}`} style={{ transition: '300ms cubic-bezier(0.25, 0.8, 0.25, 1)', height: 'calc(100vh - 40px)' }}>
                            <div className="flex flex-col gap-5">
                                <div className="flex gap-5">
                                    {picture ?
                                        <div className="w-[48px] h-[48px] rounded-full bg-cover bg-center" style={{ backgroundImage: `url("${picture}")` }} />
                                        :
                                        <BiSolidUserCircle className="text-white text-5xl" />
                                    }
                                    <div className="flex flex-col text-white">
                                        <span className="text-lg">{name}</span>
                                        <span className="text-xs text-light-purple">{email}</span>
                                    </div>
                                </div>
                                <div className="flex flex-col">
                                    <span className="text-light-purple font-bold">Menu</span>
                                </div>
                                <div className="flex flex-col gap-1 text-sm text-light-purple">
                                    <div className={`flex gap-4 items-center p-2 rounded-lg cursor-pointer 
                                                    hover:bg-primary-darker-color hover:text-white 
                                                    ${match('/kv') && 'bg-primary-darker-color text-white'}`}
                                        onClick={() => onNavigate('/kv')}>
                                        <div className="bg-primary-lighter-color p-3 rounded-full">
                                            <FaFolder className="text-sm" />
                                        </div>
                                        <span>KV</span>
                                    </div>
                                    <div className={`flex gap-4 items-center p-2 rounded-lg cursor-pointer 
                                                    hover:bg-primary-darker-color hover:text-white 
                                                    ${match('/secrets') && 'bg-primary-darker-color text-white'}`}
                                        onClick={() => onNavigate('/secrets')}>
                                        <div className="bg-primary-lighter-color p-3 rounded-full">
                                            <FaKey className="text-sm" />
                                        </div>
                                        <span>Secrets</span>
                                    </div>
                                </div>
                                {hasAccessAnyAdminResource() && <hr className="text-white" />}
                                <div className="flex flex-col gap-1 text-white text-sm text-light-purple">
                                    {hasAccessToBatch(roles, '#NAMESPACES') &&
                                        <div className={`flex gap-4 items-center p-2 rounded-lg cursor-pointer 
                                                    hover:bg-primary-darker-color hover:text-white 
                                                    ${match('/admin/namespaces') && 'bg-primary-darker-color text-white'}`}
                                            onClick={() => onNavigate('/admin/namespaces')}>
                                            <div className="bg-primary-lighter-color p-3 rounded-full">
                                                <VscSymbolNamespace className="text-sm" />
                                            </div>
                                            <span>Namespaces</span>
                                        </div>
                                    }
                                    {hasAccessToBatch(roles, '#POLICIES') &&
                                        <div className={`flex gap-4 items-center p-2 rounded-lg cursor-pointer 
                                                    hover:bg-primary-darker-color hover:text-white 
                                                    ${match('/admin/policies') && 'bg-primary-darker-color text-white'}`}
                                            onClick={() => onNavigate('/admin/policies')}>
                                            <div className="bg-primary-lighter-color p-3 rounded-full">
                                                <MdOutlinePolicy className="text-sm" />
                                            </div>
                                            <span>Policy</span>
                                        </div>
                                    }
                                    {hasAccessToBatch(roles, '#USERS') &&
                                        <div className={`flex gap-4 items-center p-2 rounded-lg cursor-pointer 
                                                    hover:bg-primary-darker-color hover:text-white 
                                                    ${match('/admin/users') && 'bg-primary-darker-color text-white'}`}
                                            onClick={() => onNavigate('/admin/users')}>
                                            <div className="bg-primary-lighter-color p-3 rounded-full">
                                                <FaUsers className="text-sm" />
                                            </div>
                                            <span>User</span>
                                        </div>
                                    }
                                    {hasAccessToBatch(roles, '#ROLES') &&
                                        <div className={`flex gap-4 items-center p-2 rounded-lg cursor-pointer 
                                                    hover:bg-primary-darker-color hover:text-white 
                                                    ${match('/admin/roles') && 'bg-primary-darker-color text-white'}`}
                                            onClick={() => onNavigate('/admin/roles')}>
                                            <div className="bg-primary-lighter-color p-3 rounded-full">
                                                <FaIdCard className="text-sm" />
                                            </div>
                                            <span>Roles</span>
                                        </div>
                                    }
                                    {hasAccessToBatch(roles, '#TOKENS') &&
                                        <div className={`flex gap-4 items-center p-2 rounded-lg cursor-pointer 
                                                    hover:bg-primary-darker-color hover:text-white 
                                                    ${match('/admin/tokens') && 'bg-primary-darker-color text-white'}`}
                                            onClick={() => onNavigate('/admin/tokens')}>
                                            <div className="bg-primary-lighter-color p-3 rounded-full">
                                                <MdOutlineGeneratingTokens className="text-sm" />
                                            </div>
                                            <span>Tokens</span>
                                        </div>
                                    }
                                    {hasAccessToBatch(roles, '#GENERAL') &&
                                        <div className={`flex gap-4 items-center p-2 rounded-lg cursor-pointer 
                                                    hover:bg-primary-darker-color hover:text-white 
                                                    ${match('/admin/general') && 'bg-primary-darker-color text-white'}`}
                                            onClick={() => onNavigate('/admin/general')}>
                                            <div className="bg-primary-lighter-color p-3 rounded-full">
                                                <IoIosSettings className="text-sm" />
                                            </div>
                                            <span>General</span>
                                        </div>
                                    }
                                </div>
                            </div>
                        </div>
                    </div>
                    <span className="text-title text-white text-4xl cursor-pointer" onClick={() => navigate('/kv')}>IARA</span>

                    <Dropdown items={userDropdown}>
                        <div className="flex items-center gap-3 text-white">
                            {name}
                            <FaCaretDown />
                        </div>
                    </Dropdown>
                </div>
            </div>

            <div className={`m-10 mt-[92px] ${isMobile && ''} ${isMenuOpen ? 'ml-[280px]' : ''}`}>
                <NamespaceEnvironment />
                <Outlet />
            </div>
        </>
    )
}
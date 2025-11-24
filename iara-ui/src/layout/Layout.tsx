import { useEffect, useState } from "react";
import { BiMenu } from "react-icons/bi";
import { FaAngleDown, FaAngleUp, FaFolder, FaIdCard, FaKey, FaUsers } from "react-icons/fa";
import { FaCircleUser } from "react-icons/fa6";
import { IoIosSettings, IoMdLogOut } from "react-icons/io";
import { MdAdminPanelSettings, MdOutlineGeneratingTokens, MdOutlinePolicy } from "react-icons/md";
import { VscSymbolNamespace } from "react-icons/vsc";
import { Outlet, useLocation, useNavigate } from "react-router";
import { ToastContainer } from "react-toastify";
import Loading from "../components/Loading";
import NamespaceEnvironment from "../components/NamespaceEnvironment";
import { useLoading } from "../providers/LoadingProvider";
import { LoginService } from "../services/LoginService";
import { UserService } from "../services/UserService";
import type { Role } from "../types/Role";
import type { User } from "../types/User";
import { hasAccessToBatch } from "../utils/PermissionUtils";

export default function Layout() {
    const location = useLocation();
    const userService = new UserService();
    const loginService = new LoginService();
    const navigate = useNavigate();

    const { loading } = useLoading();

    const [isMenuOpen, setIsMenuOpen] = useState<boolean>(window.innerWidth > 1150);
    const [_, setIsMobile] = useState<boolean>(window.innerWidth < 1150);
    const [userMenuOpen, setUserMenuOpen] = useState<boolean>(false);
    const [adminSubmenu, setAdminSubmenu] = useState<boolean>(false);

    const [name, setName] = useState<string>();
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

    const hasAccessAnyAdminResource = () => {
        return hasAccessToBatch(roles, '#NAMESPACES') || hasAccessToBatch(roles, '#POLICIES') ||
            hasAccessToBatch(roles, '#USERS') || hasAccessToBatch(roles, '#ROLES') || hasAccessToBatch(roles, '#TOKENS') ||
            hasAccessToBatch(roles, '#GENERAL')
    }

    return (
        <main>
            <ToastContainer />
            {loading && <Loading />}
            <header className="fixed top-0 w-full h-12 max-h-12 bg-primary-color z-[100]">
                <div className="h-full w-full flex justify-between items-center pl-5 pr-5">
                    <div className="flex items-center justify-center gap-5 text-white">
                        <BiMenu className="text-2xl cursor-pointer"
                            style={{ color: 'white' }}
                            onClick={() => setIsMenuOpen(!isMenuOpen)} />
                        <h1 className="text-title text-2xl">IARA KV</h1>
                    </div>
                    <div className="relative flex items-center justify-center gap-2 text-white cursor-pointer"
                        onClick={() => setUserMenuOpen(!userMenuOpen)}>
                        {!picture && <FaCircleUser className="text-2xl" style={{ color: 'white' }} />}
                        {picture && <div className="w-8 h-8 rounded-full bg-cover bg-center" style={{ backgroundImage: `url("${picture}")` }} />}
                        <span>{name}</span>
                        <FaAngleDown style={{ color: 'white' }} />
                        {userMenuOpen &&
                            <div className="absolute -bottom-[100px] -right-2 bg-white text-color border border-gray-200 rounded">
                                {userDropdown.map((val: any) => {
                                    return (
                                        <div className="flex gap-2 px-5 py-3 cursor-pointer hover:bg-gray-100"
                                            onClick={() => val.onClick && val.onClick() || val.to && navigate(val.to)}>
                                            {val.icon}
                                            <span>{val.name}</span>
                                        </div>
                                    )
                                })}
                            </div>
                        }
                    </div>
                </div>
            </header>

            <aside className={`fixed w-[280px] top-12 h-full bg-light-gray border-r border-r-stone-300 transition ${isMenuOpen ? 'opacity-100 z-50' : 'opacity-0 -z-50'}`}>
                <div className={`flex items-center justify-between border-b border-b-stone-300 h-[45px] px-3 cursor-pointer hover:bg-white ${match('/kv') && 'bg-white'}`}
                    onClick={() => navigate('/kv')}>
                    <div className="flex items-center gap-2">
                        <FaFolder className="text-lg" />
                        <span>KV</span>
                    </div>
                </div>
                <div className={`flex items-center justify-between border-b border-b-stone-300 h-[45px] px-3 cursor-pointer hover:bg-white ${match('/secrets') && 'bg-white'}`}
                    onClick={() => navigate('/secrets')}>
                    <div className="flex items-center gap-2">
                        <FaKey className="text-lg" />
                        <span>Secrets</span>
                    </div>
                </div>

                <div className={`flex items-center justify-between border-b border-b-stone-300 h-[45px] px-3 cursor-pointer hover:bg-white}`}
                    onClick={() => setAdminSubmenu(!adminSubmenu)}>
                    <div className="flex items-center gap-2">
                        <MdAdminPanelSettings className="text-lg" />
                        <span>Admin</span>
                    </div>
                    {adminSubmenu ? <FaAngleUp /> : <FaAngleDown />}
                </div>
                {adminSubmenu && hasAccessAnyAdminResource() &&
                    <>
                        {hasAccessToBatch(roles, '#NAMESPACES') &&
                            <div className={`pl-7 bg-gray-200 flex items-center justify-between border-b border-b-stone-300 h-[45px] px-3 cursor-pointer hover:bg-white ${match('/admin/namespaces') && 'bg-white'}`}
                                onClick={() => navigate('/admin/namespaces')}>
                                <div className="flex items-center gap-2">
                                    <VscSymbolNamespace className="text-lg" />
                                    <span>Namespaces</span>
                                </div>
                            </div>
                        }
                        {hasAccessToBatch(roles, '#POLICIES') &&
                            <div className={`pl-7 bg-gray-200 flex items-center justify-between border-b border-b-stone-300 h-[45px] px-3 cursor-pointer hover:bg-white ${match('/admin/policies') && 'bg-white'}`}
                                onClick={() => navigate('/admin/policies')}>
                                <div className="flex items-center gap-2">
                                    <MdOutlinePolicy className="text-lg" />
                                    <span>Policy</span>
                                </div>
                            </div>
                        }
                        {hasAccessToBatch(roles, '#USERS') &&
                            <div className={`pl-7 bg-gray-200 flex items-center justify-between border-b border-b-stone-300 h-[45px] px-3 cursor-pointer hover:bg-white ${match('/admin/users') && 'bg-white'}`}
                                onClick={() => navigate('/admin/users')}>
                                <div className="flex items-center gap-2">
                                    <FaUsers className="text-lg" />
                                    <span>User</span>
                                </div>
                            </div>
                        }
                        {hasAccessToBatch(roles, '#ROLES') &&
                            <div className={`pl-7 bg-gray-200 flex items-center justify-between border-b border-b-stone-300 h-[45px] px-3 cursor-pointer hover:bg-white ${match('/admin/roles') && 'bg-white'}`}
                                onClick={() => navigate('/admin/roles')}>
                                <div className="flex items-center gap-2">
                                    <FaIdCard className="text-lg" />
                                    <span>Roles</span>
                                </div>
                            </div>
                        }
                        {hasAccessToBatch(roles, '#TOKENS') &&
                            <div className={`pl-7 bg-gray-200 flex items-center justify-between border-b border-b-stone-300 h-[45px] px-3 cursor-pointer hover:bg-white ${match('/admin/tokens') && 'bg-white'}`}
                                onClick={() => navigate('/admin/tokens')}>
                                <div className="flex items-center gap-2">
                                    <MdOutlineGeneratingTokens className="text-lg" />
                                    <span>Tokens</span>
                                </div>
                            </div>
                        }
                        {hasAccessToBatch(roles, '#GENERAL') &&
                            <div className={`pl-7 bg-gray-200 flex items-center justify-between border-b border-b-stone-300 h-[45px] px-3 cursor-pointer hover:bg-white ${match('/admin/general') && 'bg-white'}`}
                                onClick={() => navigate('/admin/general')}>
                                <div className="flex items-center gap-2">
                                    <IoIosSettings className="text-lg" />
                                    <span>General</span>
                                </div>
                            </div>
                        }
                    </>
                }
            </aside>
            <div id="content" className={`z-50 h-screen pt-15 px-7 transition ${isMenuOpen && 'ml-[280px]'}`}>
                <NamespaceEnvironment />
                <Outlet />
            </div>
        </main>
    )
}
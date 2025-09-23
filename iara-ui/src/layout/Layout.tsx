import { useEffect, useState } from "react";
import { CgLogOut } from "react-icons/cg";
import { FaFolder, FaIdCard, FaKey, FaUsers } from "react-icons/fa";
import { IoIosSettings, IoMdMenu } from "react-icons/io";
import { MdOutlineGeneratingTokens, MdOutlinePolicy } from "react-icons/md";
import { VscSymbolNamespace } from "react-icons/vsc";
import { Outlet, useNavigate } from "react-router";
import { ToastContainer } from "react-toastify";
import LogoWhite from '../assets/logo-name-white.svg?react';
import Nobody from '../assets/nobody.svg?react';
import Footer from "../components/Footer";
import Loading from "../components/Loading";
import NamespaceEnvironment from "../components/NamespaceEnvironment";
import { useLoading } from "../providers/LoadingProvider";
import { UserService } from "../services/UserService";
import type { Role } from "../types/Role";
import type { User } from "../types/User";
import { hasAccessToBatch } from "../utils/PermissionUtils";

export default function Layout() {
    const userService = new UserService();
    const navigate = useNavigate();

    const { loading } = useLoading();

    const [isMenuOpen, setIsMenuOpen] = useState<boolean>(false);

    const [picture, setPicture] = useState<string>();
    const [name, setName] = useState<string>();
    const [roles, setRoles] = useState<Role[]>([]);

    const { fetch: originalFetch } = window;


    useEffect(() => {
        userService.me().then((res: User) => {
            setName(res.name);
            setRoles(res.roles);
            setPicture(res.picture);
        });

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
        navigate('/login')
    }

    const onNavigate = (to: string) => {
        navigate(to);
        setIsMenuOpen(false);
    }

    return (
        <>
            <ToastContainer />
            {loading && <Loading />}
            <div className="w-full min-h-16 max-h-16 bg-primary-color">
                <div className="flex justify-between items-center p-2 pl-5 pr-5">
                    <IoMdMenu className="text-2xl text-white cursor-pointer" onClick={() => setIsMenuOpen(!isMenuOpen)} />
                    <LogoWhite className='h-[50px] cursor-pointer' onClick={() => navigate('/kv')} />
                    <div className="flex gap-3 text-white">
                        {name}
                        <IoIosSettings className='text-2xl text-white cursor-pointer' onClick={() => navigate('/user/settings')} />
                        <CgLogOut className='text-2xl text-white cursor-pointer' onClick={logout} />
                    </div>
                </div>
            </div>

            <div className="m-10">
                <NamespaceEnvironment />
                <Outlet />
            </div>

            <Footer />

            {isMenuOpen &&
                <div className="fixed top-16 w-[300px] h-full bg-primary-color z-50 p-5 shadow"
                    style={{ transition: '300ms cubic-bezier(0.25, 0.8, 0.25, 1)' }}>
                    <div className="flex flex-col gap-5">
                        <div className="flex flex-col gap-2">
                            <div className="flex gap-5 p-5 text-white items-center border rounded-2xl">
                                {picture ? <img src={picture} alt="user-picture" className="w-10" /> : <Nobody className="w-10" />}
                                <div className="flex flex-col gap-2">
                                    {name}
                                </div>
                            </div>
                        </div>
                        <div className="flex flex-col gap-5 text-white text-md">
                            <div className="flex gap-4 items-center p-2 pl-4 pr-4 cursor-pointer hover:bg-primary-lighter-color rounded-2xl"
                                onClick={() => onNavigate('/kv')}>
                                <FaFolder />
                                <span>Key/Value</span>
                            </div>
                            <div className="flex gap-4 items-center p-2 pl-4 pr-4 cursor-pointer hover:bg-primary-lighter-color rounded-2xl"
                                onClick={() => onNavigate('/secrets')}>
                                <FaKey />
                                <span>Secret</span>
                            </div>
                        </div>
                        <hr className="text-white" />
                        <div className="flex flex-col gap-5 text-white text-md">
                            {hasAccessToBatch(roles, '#NAMESPACES') &&
                                <div className="flex gap-4 items-center p-2 pl-4 pr-4 cursor-pointer hover:bg-primary-lighter-color rounded-2xl"
                                    onClick={() => onNavigate('/admin/namespaces')}>
                                    <VscSymbolNamespace />
                                    <span>Namespace</span>
                                </div>
                            }
                            {hasAccessToBatch(roles, '#POLICIES') &&
                                <div className="flex gap-4 items-center p-2 pl-4 pr-4 cursor-pointer hover:bg-primary-lighter-color rounded-2xl"
                                    onClick={() => onNavigate('/admin/policies')}>
                                    <MdOutlinePolicy />
                                    <span>Policy</span>
                                </div>
                            }
                            {hasAccessToBatch(roles, '#USERS') &&
                                <div className="flex gap-4 items-center p-2 pl-4 pr-4 cursor-pointer hover:bg-primary-lighter-color rounded-2xl"
                                    onClick={() => onNavigate('/admin/users')}>
                                    <FaUsers />
                                    <span>User</span>
                                </div>
                            }
                            {hasAccessToBatch(roles, '#ROLES') &&
                                <div className="flex gap-4 items-center p-2 pl-4 pr-4 cursor-pointer hover:bg-primary-lighter-color rounded-2xl"
                                    onClick={() => onNavigate('/admin/roles')}>
                                    <FaIdCard />
                                    <span>Roles</span>
                                </div>
                            }
                            {hasAccessToBatch(roles, '#TOKENS') &&
                                <div className="flex gap-4 items-center p-2 pl-4 pr-4 cursor-pointer hover:bg-primary-lighter-color rounded-2xl"
                                    onClick={() => onNavigate('/admin/tokens')}>
                                    <MdOutlineGeneratingTokens />
                                    <span>Tokens</span>
                                </div>
                            }
                            {hasAccessToBatch(roles, '#GENERAL') &&
                                <div className="flex gap-4 items-center p-2 pl-4 pr-4 cursor-pointer hover:bg-primary-lighter-color rounded-2xl"
                                    onClick={() => onNavigate('/admin/general')}>
                                    <IoIosSettings />
                                    <span>General</span>
                                </div>
                            }
                        </div>
                    </div>
                </div>
            }
        </>
    )
}
import { FaCaretDown } from 'react-icons/fa';
import { Outlet, useNavigate } from 'react-router';
import Logo from '../assets/logo-name-white.svg?react';
import Nobody from '../assets/nobody.svg?react';
import Button from '../components/Button';
import Dropdown from '../components/Dropdown';
import type { Role } from '../types/Role';
import { hasAccessToBatch } from '../utils/PermissionUtils';
import { uuid } from '../utils/UUID';
import { IaraMenu } from './IaraMenu';

interface Props {
    onEnvModalOpen: () => void;
    dropdownMenu: any;
    name?: string;
    picture?: string;
    roles: Role[];
}

export default function TopMenu(props: Props) {
    const navigate = useNavigate();
    const currentPath = location.pathname;

    return (
        <>
            <div className="flex justify-between items-center w-full bg-primary-color p-1 h-[60px]">
                <div className="flex items-center gap-10 text-white">
                    <Logo className="w-[100px] h-[40px] cursor-pointer" onClick={() => navigate('/kv')} />
                    {IaraMenu.map((item: any) => {
                        return (
                            <div key={uuid()}>
                                {hasAccessToBatch(props.roles, item.key) &&
                                    <div className={`cursor-pointer pl-3 pr-3 pt-1 pb-1 rounded-sm hover:bg-primary-darker-color ${currentPath === '/admin/namespaces' && 'bg-primary-darker-color'}`}
                                        onClick={() => navigate(item.to)} key={uuid()}>
                                        {item.name}
                                    </div>
                                }
                            </div>
                        )
                    })}
                </div>
                <div className="flex gap-4 justify-between text-white min-w-96">
                    <Button onClick={props.onEnvModalOpen}>Change Namespace / Environment</Button>
                    <Dropdown items={props.dropdownMenu}>
                        <div className="flex items-center gap-2">
                            {props.picture ? <img src={props.picture} alt="user-profile-picture" className="w-[40px]" /> : <Nobody className="w-[40px]" />}
                            <span>{props.name}</span>
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
        </>
    )
}
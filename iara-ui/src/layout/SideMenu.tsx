import { Outlet, useNavigate } from 'react-router';
import LogoName from '../assets/logo-name-white.svg?react';
import Logo from '../assets/logo-white.svg?react';
import Nobody from '../assets/nobody.svg?react';
import Button from '../components/Button';
import { IaraMenu } from './IaraMenu';
import type { Role } from '../types/Role';
import { hasAccessToBatch } from '../utils/PermissionUtils';
import { uuid } from '../utils/UUID';
import { MdOutlineCloseFullscreen } from 'react-icons/md';
import { useEffect, useState } from 'react';
import { GrConfigure } from 'react-icons/gr';

interface Props {
    onEnvModalOpen: () => void;
    dropdownMenu: any;
    name?: string;
    picture?: string;
    roles: Role[];
}

export default function SideMenu(props: Props) {
    const navigate = useNavigate();

    const [isMenuOpen, setIsMenuOpen] = useState<boolean>(false);

    useEffect(() => {
    }, [])

    return (
        <div className="min-w-screen max-w-screen h-screen">
            <div className={`fixed h-screen bg-primary-color z-50 ${isMenuOpen ? 'w-[250px]' : 'w-[70px]'}`}
                style={{ transition: '300ms cubic-bezier(0.25, 0.8, 0.25, 1)' }}>
                <div className="flex flex-col gap-2 justify-between h-full">
                    <div>
                        {isMenuOpen &&
                            <div className='flex justify-end pr-4 pt-2 text-white'>
                                <MdOutlineCloseFullscreen className='cursor-pointer' onClick={() => setIsMenuOpen(!isMenuOpen)} />
                            </div>
                        }
                        <div className="flex justify-center items-center h-[120px]">
                            {isMenuOpen ? <LogoName className='h-[100px]' /> : <Logo className='h-[100px]' />}
                        </div>
                        <div className="flex flex-col gap-5 p-3">
                            {IaraMenu.map((item: any) => {
                                return (
                                    <div key={uuid()}>
                                        {hasAccessToBatch(props.roles, item.key) &&
                                            <div className={`flex gap-2 text-white p-2 hover:bg-primary-darker-color hover:rounded hover:cursor-pointer ${!isMenuOpen && 'text-2xl'}`}
                                                onClick={() => {
                                                    navigate(item.to);
                                                    setIsMenuOpen(false);
                                                }} key={uuid()}>
                                                {isMenuOpen ? item.name : item.icon}
                                            </div>
                                        }
                                    </div>
                                )
                            })}
                        </div>
                    </div>
                    <div className='mb-10 flex flex-col gap-5 items-center'>
                        {isMenuOpen && <Button onClick={props.onEnvModalOpen}>Change Namespace / Environment</Button>}
                        {!isMenuOpen && <GrConfigure className='text-2xl text-white' onClick={props.onEnvModalOpen} />}
                        <div className="flex items-center gap-2">
                            {props.picture ? <img src={props.picture} alt="user-profile-picture" className="w-[40px]" /> : <Nobody className="w-[40px]" />}
                            {isMenuOpen && <span className='text-white'>{props.name}</span>}
                        </div>
                        {!isMenuOpen &&
                            <div className='flex justify-center text-white'>
                                <MdOutlineCloseFullscreen className='cursor-pointer text-2xl'
                                    onClick={() => setIsMenuOpen(!isMenuOpen)} />
                            </div>
                        }
                    </div>
                </div>
            </div>
            <main className={`p-6 ml-[80px]`}>
                <Outlet></Outlet>
            </main>
        </div>
    )
}
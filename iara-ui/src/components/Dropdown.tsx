import { useState, type ReactNode } from "react";
import { useNavigate } from "react-router";
import { uuid } from "../utils/UUID";

interface Item {
    icon?: ReactNode;
    name: string;
    to?: string;
    onClick?: () => void;
}

interface Props {
    items: Item[];
    children: ReactNode;
    side?: 'down' | 'top' | 'left' | 'right'
}

export default function Dropdown(props: Props) {
    const navigate = useNavigate();
    const [isOpen, setIsOpen] = useState<boolean>(false);
    const [variant, setVariant] = useState<string>('');

    const handleClick = (item: Item) => {
        if (item.to) {
            navigate(item.to);
        } else if (item.onClick) {
            item.onClick();
        }
    }

    const getSideVariant = (variant: string = '') => {
        switch (variant) {
            case 'top':
                return !isOpen ? '-top-[200%] -right-10/12' : '-top-[200%] -right-[360%]';
            default:
                return '';
        }
    }

    const handleMouseEnter = () => {
        setVariant(() => getSideVariant(props.side));
        setIsOpen(true);
    }

    return (
        <div className="relative" onMouseEnter={() => handleMouseEnter()} onMouseLeave={() => setIsOpen(false)}>
            <div className="cursor-pointer">
                {props.children}
            </div>
            <div className={`fade-in absolute flex-col gap-2 w-fit bg-primary-color p-2 rounded z-50 ${isOpen ? 'block' : 'hidden'} ${variant}`}>
                {props.items.map((item: Item) => {
                    return (
                        <div className="flex gap-2 items-center p-4" key={uuid()}>
                            <div className="text-2xl text-white">
                                {item.icon && item.icon}
                            </div>
                            <span className="w-full cursor-pointer text-white"
                                onClick={() => handleClick(item)}>{item.name}</span>
                        </div>
                    )
                })}
            </div>
        </div >
    )
}
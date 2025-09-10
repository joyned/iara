import { useEffect, useState, type ReactNode } from "react";
import { useNavigate } from "react-router";

interface Item {
    icon?: ReactNode;
    name: string;
    to?: string;
    onClick?: () => void;
}

interface Props {
    items: Item[];
    children: ReactNode;
}

export default function Dropdown(props: Props) {
    const navigate = useNavigate();
    const [isOpen, setIsOpen] = useState<boolean>(false);

    useEffect(() => {
    })

    const handleClick = (item: Item) => {
        if (item.to) {
            navigate(item.to);
        } else if (item.onClick) {
            item.onClick();
        }
    }
    return (
        <div className="relative" onMouseEnter={() => setIsOpen(true)} onMouseLeave={() => setIsOpen(false)}>
            <div className="cursor-pointer">
                {props.children}
            </div>
            <div className={`fade-in absolute flex flex-col gap-2 w-fit bg-primary-color p-2 rounded z-50 right-0 ${!isOpen && 'hidden'}`}>
                {props.items.map((item: Item) => {
                    return (
                        <div className="flex gap-2 items-center p-4">
                            <div className="text-2xl">
                                {item.icon && item.icon}
                            </div>
                            <span className="w-full cursor-pointer"
                                onClick={() => handleClick(item)}>{item.name}</span>
                        </div>
                    )
                })}
            </div>
        </div >
    )
}
import { useState, type ReactNode } from "react";

interface Props {
    text: string;
    children: ReactNode;
}

export default function Tooltip(props: Props) {
    const [isOpen, setIsOpen] = useState<boolean>(false);
    return (
        <>
            <div className="relative" onMouseEnter={() => setIsOpen(true)}
                onMouseLeave={() => setIsOpen(false)}>
                {props.children}
                {isOpen &&
                    <div className="absolute bg-primary-color p-3 text-sm rounded 
                        border border-primary-darker-color z-50 shadow top-0 left-10/12">
                        <span>{props.text}</span>
                    </div>
                }
            </div>
        </>
    )
}
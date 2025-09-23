import { useState, type ReactNode } from "react";
import { FaCaretDown, FaCaretUp } from "react-icons/fa";

interface Props {
    title: string;
    startClosed: boolean;
    children: ReactNode;
}

export default function Panel(props: Props) {
    const [isOpen, setIsOpen] = useState<boolean>(!props.startClosed);

    return (
        <>
            <div className="flex flex-col gap-5" >
                <div className="flex justify-between border-b p-3 cursor-pointer" onClick={() => setIsOpen(!isOpen)}>
                    <h1>{props.title}</h1>
                    {isOpen ? <FaCaretUp /> : <FaCaretDown />}
                </div>
                <div style={{ transition: '300ms cubic-bezier(0.25, 0.8, 0.25, 1)' }}>
                    {isOpen && props.children}
                </div>
            </div>
        </>
    )
}
import type { InputHTMLAttributes, ReactNode } from "react";

interface Props extends InputHTMLAttributes<HTMLInputElement> {
    icon?: ReactNode;
}

export default function Input(props: Props) {
    return (
        <div className={`flex items-center gap-2 p-1.5 pl-3 pr-3 border 
        border-stone-400 rounded-sm relative ${props.className} ${props.disabled && 'bg-gray-200'}`}>
            {props.icon && props.icon}
            <input className="border-none" {...props} style={{ width: '100%' }} />
        </div>
    )
}
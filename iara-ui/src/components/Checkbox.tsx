import type { InputHTMLAttributes } from "react";

interface Props extends InputHTMLAttributes<HTMLInputElement> {
    label?: string;
}


export default function Checkbox(props: Props) {
    return (
        <label className="flex items-center gap-2 w-fit cursor-pointer" htmlFor={props.name}>
            <input className="checkbox" type="checkbox" name={props.name} id={props.id} checked={props.checked} />
            {props.label}
        </label>
    )
}
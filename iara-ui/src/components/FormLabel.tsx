import type { LabelHTMLAttributes } from "react";

interface Props extends LabelHTMLAttributes<HTMLLabelElement> {
    required?: boolean;
}

export default function FormLabel(props: Props) {
    return (
        <label {...props}>
            {props.children}
            {props.required && <span className="text-red-500"> *</span>}
        </label>
    )
}
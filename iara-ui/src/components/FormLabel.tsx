import type { LabelHTMLAttributes } from "react";

interface Props extends LabelHTMLAttributes<HTMLLabelElement> {
    required?: boolean;
}

export default function FormLabel(props: Props) {
    const { className } = props;

    const baseClass = "";
    const combinedClass = `${baseClass} ${className}`;

    return (
        <label {...props} className={combinedClass}>
            {props.children}
            {props.required && <span className="text-red-500"> *</span>}
        </label>
    )
}
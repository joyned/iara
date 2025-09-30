import type { TextareaHTMLAttributes } from "react";

interface Props extends TextareaHTMLAttributes<HTMLTextAreaElement> {
    resize?: boolean
}

export default function TextArea(props: Props) {
    return (
        <>
            <textarea {...props}
                style={{ resize: props.resize ? 'vertical' : 'none' }}
                className="p-1.5 pl-3 pr-3 border border-stone-400 rounded-sm" />
        </>
    )
}
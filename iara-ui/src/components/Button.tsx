import type { ButtonHTMLAttributes } from "react";

interface Props extends ButtonHTMLAttributes<HTMLButtonElement> {
    variant?: 'default' | 'outline' | 'danger';
    className?: string;
}

export default function Button(props: Props) {
    const { variant = 'default', className, ...rest } = props;

    const getVariant = (variant: string) => {
        switch (variant) {
            case 'outline':
                return 'bg-transparent border border-gray-400';
            case 'danger':
                return 'bg-red-600 text-white border-red-600'
            default:
                return 'border-primary-color bg-gray-500 text-white';
        }
    }

    const baseClasses = 'rounded p-2 min-w-[150px] transition-colors duration-200 cursor-pointer';
    const variantClasses = getVariant(variant);

    const combinedClasses = `${baseClasses} ${variantClasses} ${className || ''}`
        .replace(/\s+/g, ' ')
        .trim();

    return (
        <>
            <button className={combinedClasses} {...rest}>
                {props.children}
            </button>
        </>
    )
}
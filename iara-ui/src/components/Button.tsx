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
                return 'bg-transparent border';
            case 'danger':
                return 'bg-red-600 text-white border-red-600'
            default:
                return 'border-primary-color bg-primary-color text-white hover:bg-primary-darker-color';
        }
    }

    const baseClasses = 'rounded-sm p-2 cursor-pointer transition-colors duration-200';
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
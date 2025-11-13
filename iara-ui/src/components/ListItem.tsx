import type { ReactNode } from "react";
import { BsTrash } from "react-icons/bs";
import { MdKeyboardArrowRight } from "react-icons/md";
import { ConfirmDialog } from "./ConfirmDialog";

interface Props {
    icon?: ReactNode;
    name: string;
    onClick?: () => void;
    onDelete?: () => void;
    children?: ReactNode;
}

export default function ListItem(props: Props) {
    return (
        <div className="flex justify-between items-center border-b-1 cursor-pointer hover:bg-primary-lighter-color text-sm hover:bg-gray-100"
            style={{ borderBottomColor: 'var(--primary-lighter-color)' }}>
            <span className="w-full flex items-center gap-4 p-4" onClick={props.onClick}>
                {props.icon && props.icon}
                {props.name}
            </span>
            <div className="flex gap-4 mr-5">
                {props.children && props.children}
                {props.onDelete &&
                    <ConfirmDialog onConfirm={props.onDelete}>
                        <BsTrash className="text-red-500 z-50" />
                    </ConfirmDialog>
                }
                {props.onClick && <MdKeyboardArrowRight onClick={props.onClick} />}
            </div>
        </div>
    )
}
import { forwardRef, useImperativeHandle, useState, type ChangeEvent, type ReactElement } from "react";
import { CgClose } from "react-icons/cg";
import { v1 } from "uuid";
import Button from "./Button";

interface Props {
    title: string;
    saveText?: string;
    cancelText?: string;
    onSave?: (e: ChangeEvent<HTMLFormElement>) => void;
    beforeClose?: () => void;
    children: ReactElement | ReactElement[];
    hasSave?: boolean;
}

export const Modal = forwardRef(({ title, saveText = "save", cancelText = "cancel", onSave, beforeClose, children, hasSave = true }: Props, ref) => {
    const [modalId] = useState<string>(v1());
    const [open, setOpen] = useState<boolean>(false);

    useImperativeHandle(ref, () => ({
        setOpen(value: boolean) {
            setOpen(value);
        },

        get modalId(): string {
            return modalId;
        }

    }))

    const onClose = () => {
        if (beforeClose) {
            beforeClose();
        }
        setOpen(false);
    }

    return (
        <>
            {open &&
                <div className="absolute flex justify-center items-center w-full h-full top-0 left-0 z-50 bg-low-opacity">
                    <div className="p-2 w-[700px] bg-white rounded max-h-10/12 overflow-auto">
                        <div className="flex justify-between items-center border-b border-b-stone-400 pb-3 pt-1">
                            <h1>{title}</h1>
                            <CgClose className="cursor-pointer" onClick={onClose} />
                        </div>
                        <form id={modalId} onSubmit={onSave} className="p-3">
                            {children}
                        </form>
                        <div className="mt-3">
                            <div className="flex justify-end gap-2">
                                {hasSave && <Button type="submit" form={modalId}>{saveText}</Button>}
                                <Button variant="outline" onClick={onClose}>{cancelText}</Button>
                            </div>
                        </div>
                    </div>
                </div>
            }
        </>
    )
})
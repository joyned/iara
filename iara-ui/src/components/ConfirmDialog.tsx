import { forwardRef, useImperativeHandle, useState, type FC, type ReactNode } from "react";
import { MdClose } from "react-icons/md";
import { uuid } from "../utils/UUID";
import Button from "./Button";

interface Props {
    title?: string;
    description?: string;
    onConfirm: () => void;
    onCancel?: () => void;
    children: ReactNode
}


export const ConfirmDialog: FC<Props> = forwardRef((props: Props, ref) => {
    const [modalId] = useState<string>(uuid());
    const [open, setOpen] = useState<boolean>(false);

    useImperativeHandle(ref, () => ({
        setOpen(value: boolean) {
            setOpen(value);
        },

        get modalId(): string {
            return modalId;
        }

    }));

    const handleConfirm = () => {
        props.onConfirm();
        setOpen(false);
    }

    const handleCancel = () => {
        if (props.onCancel) {
            props.onCancel();
        }

        setOpen(false);
    }

    return (
        <>
            <div onClick={() => setOpen(true)}>
                {props.children}
            </div>
            {open &&
                <div style={{ background: '#000000a8' }}
                    className="w-full h-full flex justify-center items-center absolute top-0 left-0 z-50">
                    <div className="rounded w-1/3 p-5 bg-white" >
                        <div className="flex flex-col gap-3">
                            <div className="flex justify-between items-center">
                                <h1 className="text-lg font-bold">
                                    {props.title || "Confirm"}
                                </h1>
                                <MdClose className="text-2xl cursor-pointer"
                                    onClick={() => setOpen(false)} />
                            </div>
                            <div className="mt-3">
                                <span className="">
                                    {props.description || "Are you sure you want to delete?"}
                                </span>
                            </div>
                            <div className="flex gap-2 justify-end">
                                <Button variant="danger"
                                    onClick={() => handleConfirm()} >
                                    Confirm
                                </Button>
                                <Button variant="outline"
                                    onClick={() => handleCancel()} >
                                    Cancel
                                </Button>
                            </div>
                        </div>
                    </div>
                </div>
            }
        </>
    )
})


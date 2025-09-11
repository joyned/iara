import { useEffect, useState, type ReactNode } from "react";
import { FaCaretDown, FaCaretUp } from "react-icons/fa";

interface Props {
    title?: string;
    subtitle?: string;
    closeable?: boolean;
    onOpen?: () => void;
    children: ReactNode;
}


export default function Card(props: Props) {
    const [isOpen, setIsOpen] = useState<boolean>(true);
    const [closeable, setCloseable] = useState<boolean>(false);

    useEffect(() => {
        if (props.closeable) {
            setCloseable(props.closeable);
        }
    }, [props.closeable])

    const handleOpen = () => {
        if (closeable) {
            setIsOpen(!isOpen);
            if (isOpen && props.onOpen) {
                props.onOpen();
            }
        }
    }

    return (
        <>
            <div className="flex shadow-2xl border border-stone-200 rounded-sm w-full">
                <div className="p-4 w-full">
                    {props.title &&
                        <div className="flex justify-between items-center"
                            style={{ cursor: closeable ? 'pointer' : '' }}
                            onClick={handleOpen}>
                            <h1>{props.title}</h1>
                            {closeable &&
                                <>
                                    {isOpen ?
                                        <FaCaretDown className="cursor-pointer" onClick={() => setIsOpen(true)} />
                                        :
                                        <FaCaretUp className="cursor-pointer" onClick={() => setIsOpen(false)} />
                                    }
                                </>
                            }
                        </div>
                    }
                    {!(closeable && isOpen) &&
                        <>
                            {props.subtitle &&
                                <div className="flex justify-between">
                                    <span className="text-sm">{props.subtitle}</span>
                                </div>
                            }
                            <div className="mt-5">
                                {props.children}
                            </div>
                        </>
                    }
                </div>
            </div>
        </>
    )
}

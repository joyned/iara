import { MdArrowBackIos, MdArrowForwardIos } from "react-icons/md";

interface Props {
    totalPages: number;
    page: number;
    onPage?: (page: number) => void;
}

export default function Pageable(props: Props) {
    const disablePrev = () => {
        return props.page === 0;
    }

    const disableNext = () => {
        return props.totalPages === props.page + 1;
    }

    return (
        <div className="w-[100px]">
            < div className="w-full" >
                <div className="flex justify-between text-stone-700">
                    <MdArrowBackIos className={`m-2 ${disablePrev() ? 'cursor-not-allowed text-stone-400' : 'cursor-pointer'}`}
                        onClick={() => {
                            if (!disablePrev() && props.onPage) {
                                props.onPage(props.page - 1);
                            }
                        }} />
                    <MdArrowForwardIos className={`m-2 ${disableNext() ? 'cursor-not-allowed text-stone-400' : 'cursor-pointer'}`}
                        onClick={() => {
                            if (!disableNext() && props.onPage) {
                                props.onPage(props.page + 1);
                            }
                        }} />
                </div>
            </div >
        </div >
    )
}
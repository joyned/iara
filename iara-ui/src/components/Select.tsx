import type { SelectHTMLAttributes } from "react";
import { uuid } from "../utils/UUID";

interface Props extends SelectHTMLAttributes<HTMLSelectElement> {
    options: any[];
    optionlabel?: string;
}

export default function Select(props: Props) {
    return (
        <select {...props} className="p-3 pl-3 pr-3  bg-light-gray rounded-sm">
            <option defaultChecked>Select an option...</option>
            {props.options && props.options.length > 0 && props.options.map((opt: any) => {
                return (
                    <option value={JSON.stringify(opt)} key={uuid()}>{opt[props.optionlabel || 'name']}</option>
                )
            })
            }
        </select>
    )
}

interface Props {
    value?: boolean;
    onChange?: (value: boolean) => void;
    id?: string;
    name?: string;
}


export default function Checkbox(props: Props) {
    return (
        <div className="checkbox-wrapper-2">
            <input className="sc-gJwTLC ikxBAC" type="checkbox" checked={props.value} id={props.id} name={props.name}
                onChange={() => props.onChange && props.onChange(!props.value)}></input>
        </div>
    )
}
import type { ChangeEvent } from "react";
import Button from "./Button";
import Input from "./Input";
import ListItem from "./ListItem";
import { uuid } from "../utils/UUID";
import Pageable from "./Pageable";

interface Props {
    title: string;
    data: any[];
    dataLabel: string;
    onSearch?: (value: string) => void;
    onCreate?: () => void;
    onEdit?: (id: string) => void;
    onDelete?: (id: string) => void;
    totalPages?: number;
    page?: number;
    onPage?: (page: number) => void;
}

export default function TableList(props: Props) {
    return (
        <>
            <div className="flex flex-col gap-5">
                <h1 className="text-2xl">{props.title}</h1>
                <div className="flex justify-between">
                    <Input placeholder="search" onChange={(e: ChangeEvent<HTMLInputElement>) => props.onSearch && props.onSearch(e.target.value)} />
                    <Button type="button" className="w-[150px]" onClick={() => props.onCreate && props.onCreate()}>create</Button>
                </div>
                <div className="flex flex-col gap-4">
                    {props.data.map((d: any) => {
                        return (
                            <ListItem name={d[props.dataLabel]} onClick={() => props.onEdit && props.onEdit(d.id)} key={uuid()}
                                onDelete={props.onDelete && (() => props.onDelete && props.onDelete(d.id))} />
                        )
                    })}
                </div>
                <div className="flex justify-end">
                    {props.data.length > 0 && <Pageable totalPages={props.totalPages || 0} page={props.page || 0}
                        onPage={(page: number) => props.onPage && props.onPage(page)}></Pageable>}
                </div>
            </div>
        </>
    )
}
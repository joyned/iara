import type { ChangeEvent } from "react";
import { uuid } from "../utils/UUID";
import Button from "./Button";
import Card from "./Card";
import Input from "./Input";
import ListItem from "./ListItem";
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
        <div className="flex flex-col gap-5">
            <div className="flex justify-between items-center">
                <h1 className="text-1xl">{props.title}</h1>
                <Button type="button" variant="outline" onClick={() => props.onCreate && props.onCreate()}>Create</Button>
            </div>
            <Card>
                <div className="flex flex-col gap-5">
                    <div className="flex justify-between">
                        <Input placeholder="Search" onChange={(e: ChangeEvent<HTMLInputElement>) => props.onSearch && props.onSearch(e.target.value)} />
                    </div>
                    <div className="flex flex-col gap-4">
                        {props.data.length > 0 && props.data.map((d: any) => {
                            return (
                                <ListItem name={d[props.dataLabel]} onClick={() => props.onEdit && props.onEdit(d.id)} key={uuid()}
                                    onDelete={props.onDelete && (() => props.onDelete && props.onDelete(d.id))} />
                            )
                        })}
                        {props.data.length === 0 && <span className="text-white text-sm">No data found.</span>}
                    </div>
                    <div className="flex justify-end">
                        {props.data.length > 0 && <Pageable totalPages={props.totalPages || 0} page={props.page || 0}
                            onPage={(page: number) => props.onPage && props.onPage(page)}></Pageable>}
                    </div>
                </div>
            </Card>
        </div>
    )
}
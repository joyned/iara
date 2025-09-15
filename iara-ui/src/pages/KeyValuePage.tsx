import { useEffect, useState } from "react";
import { HiOutlineNewspaper } from "react-icons/hi";
import { MdKeyboardArrowRight } from "react-icons/md";
import { useNavigate } from "react-router";
import Button from "../components/Button";
import Card from "../components/Card";
import NamespaceEnvironment from "../components/NamespaceEnvironment";
import Pageable from "../components/Pageable";
import { useEnvironment } from "../providers/EnvironmentProvider";
import { useLoading } from "../providers/LoadingProvider";
import { useNamespace } from "../providers/NamespaceProvider";
import eventBus from "../services/EventBusService";
import { KeyValueService } from "../services/KeyValueService";
import type { KeyValue } from "../types/KeyValue";
import type { Page } from "../types/Page";
import { uuid } from "../utils/UUID";

export default function KeyValuePage() {
    const navigate = useNavigate();
    const kvService = new KeyValueService();

    const [keyValues, setKeyValues] = useState<KeyValue[]>([]);
    const [page, setPage] = useState<number>(0);
    const [totalPages, setTotalPages] = useState<number>(1);

    const { namespace } = useNamespace();
    const { environment } = useEnvironment();

    const { setLoading } = useLoading();

    const search = (params?: Partial<KeyValue>, page?: number) => {
        if (!params) {
            params = { namespace: namespace, environment: environment };
        }

        setLoading(true);
        kvService.search(params, page).then((res: Page<KeyValue>) => {
            setKeyValues(res.content);
            setPage(res.pageable.pageNumber);
            setTotalPages(res.totalPages);
        }).finally(() => setLoading(false));
    }

    useEffect(() => {
        search({ namespace: namespace, environment: environment });

        const listenToNamespaceEnvChange = (data: any) => {
            search({ namespace: data.namespace, environment: data.environment });
        }

        eventBus.on('namespaceEnvironmentChange', listenToNamespaceEnvChange);

        return () => {
            eventBus.off('namespaceEnvironmentChange', listenToNamespaceEnvChange);
        };
    }, [environment, namespace])


    return (
        <>
            <NamespaceEnvironment />
            <Card title={'K/V Entries'}>
                <div className="flex flex-col gap-4">
                    {keyValues.map((kv: KeyValue) => {
                        return (
                            <div className="flex justify-between items-center border-b border-b-gray-200 p-4 cursor-pointer hover:bg-gray-100"
                                onClick={() => navigate(`/kv/${kv.id}`)} key={uuid()}>
                                <span className="flex items-center gap-4">
                                    <HiOutlineNewspaper />
                                    {kv.key}
                                </span>
                                <MdKeyboardArrowRight />
                            </div>
                        )
                    })}
                </div>
                <div className="flex justify-between mt-5">
                    <div className="flex gap-2">
                        <Button type="button" onClick={() => navigate(`/kv/new`)}>Create</Button>
                    </div>
                    {keyValues.length > 0 && <Pageable totalPages={totalPages} page={page} onPage={(page: number) => search(undefined, page)}></Pageable>}
                </div>
            </Card>
        </>
    )
}
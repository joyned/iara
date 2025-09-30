import { useEffect, useRef, useState, type ChangeEvent } from "react";
import { useEnvironment } from "../providers/EnvironmentProvider";
import { useNamespace } from "../providers/NamespaceProvider";
import { EnvironmentService } from "../services/EnvironmentService";
import eventBus from "../services/EventBusService";
import { NamespaceService } from "../services/NamespaceService";
import type { Environment } from "../types/Environment";
import type { Namespace } from "../types/Namespace";
import type { Page } from "../types/Page";
import { Modal } from "./Modal";
import Select from "./Select";

export default function NamespaceEnvironment() {
    const { namespace, setNamespace } = useNamespace();
    const { environment, setEnvironment } = useEnvironment();

    const envModalRef = useRef<any>(null);

    const namespaceService = new NamespaceService();
    const environmentService = new EnvironmentService();

    const [namespaceOptions, setNamespaceOptions] = useState<Namespace[]>();
    const [environmentOptions, setEnvironmentOptions] = useState<Environment[]>();

    const onEnvModalOpen = () => {
        if (namespace && namespace.id) {
            searchEnvironments(namespace.id);
        }

        namespaceService.search({}, 0, 2000).then((res: Page<Namespace>) => {
            setNamespaceOptions(res.content);
        }).finally(() => envModalRef.current.setOpen(true));
    }

    const searchEnvironments = (namespaceId: string) => {
        environmentService.search({ namespace: namespaceId }).then((res: Page<Environment>) => {
            setEnvironmentOptions(res.content);
        })
    }

    const beforeSaveModal = (e: ChangeEvent<HTMLFormElement>) => {
        e.preventDefault();
        if (JSON.stringify(environment) === '{}') {
            console.log('Select an Environment')
        } else {
            eventBus.emit('namespaceEnvironmentChange', { namespace: namespace, environment: environment })
            envModalRef.current.setOpen(false);
        }
    }

    const onNamespaceSelect = (namespace: Namespace) => {
        searchEnvironments(namespace.id || '');
        setNamespace(namespace)
        setEnvironment(JSON.parse('{}'))
    }

    useEffect(() => {
        if (!namespace) {
            onEnvModalOpen();
        }
    }, [])

    return (
        <div className="mb-5">
            {(namespace && JSON.stringify(environment) !== '{}') && (
                <>
                    <div className="w-fit flex items-center bg-primary-color rounded-lg text-white cursor-pointer"
                        onClick={() => onEnvModalOpen()}>
                        <span className="font-semibold p-2 pl-7 pr-7 border-r border-white">{namespace.name}</span>
                        <span className="font-semibold p-2 pl-7 pr-7 border-l border-white">{environment?.name}</span>
                    </div>
                </>
            )}
            {JSON.stringify(environment) === '{}' && (
                <span className="w-fit flex items-center gap-1 bg-red-500 p-2 rounded text-white cursor-pointer" onClick={() => onEnvModalOpen()}>
                    please, select an Environment
                </span>
            )}
            <Modal title="select your environment" ref={envModalRef} onSave={beforeSaveModal} saveText="apply">
                <div className="flex flex-col gap-5">
                    <div className="flex flex-col gap-1">
                        <span className="font-medium">namespace:</span>
                        <Select options={namespaceOptions || []} optionlabel="name" value={JSON.stringify(namespace)}
                            onChange={(e: ChangeEvent<HTMLSelectElement>) => onNamespaceSelect(JSON.parse(e.target.value))} />
                    </div>
                    <div className="flex flex-col gap-1">
                        <div className="font-medium">environment:</div>
                        <Select options={environmentOptions || []} optionlabel="name" value={JSON.stringify(environment)}
                            onChange={(e: ChangeEvent<HTMLSelectElement>) => setEnvironment(JSON.parse(e.target.value))} />
                    </div>
                </div>
            </Modal>
        </div>
    )
}
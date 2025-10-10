import { useEffect, useRef, useState, type ChangeEvent } from "react";
import { useEnvironment } from "../providers/EnvironmentProvider";
import { useLoading } from "../providers/LoadingProvider";
import { useNamespace } from "../providers/NamespaceProvider";
import { EnvironmentService } from "../services/EnvironmentService";
import eventBus from "../services/EventBusService";
import { NamespaceService } from "../services/NamespaceService";
import type { Environment } from "../types/Environment";
import type { Namespace } from "../types/Namespace";
import type { Page } from "../types/Page";
import { Modal } from "./Modal";
import Select from "./Select";
import FormLabel from "./FormLabel";

export default function NamespaceEnvironment() {
    const { setLoading } = useLoading();
    const { namespace, setNamespace } = useNamespace();
    const { environment, setEnvironment } = useEnvironment();

    const envModalRef = useRef<any>(null);

    const namespaceService = new NamespaceService();
    const environmentService = new EnvironmentService();

    const [namespaceOptions, setNamespaceOptions] = useState<Namespace[]>();
    const [environmentOptions, setEnvironmentOptions] = useState<Environment[]>();

    const [selectedNamespace, setSelectedNamespace] = useState<Namespace | undefined>();
    const [selectedEnvironment, setSelectedEnvironment] = useState<Environment | undefined>();

    const onEnvModalOpen = () => {
        setLoading(true);
        if (namespace && namespace.id) {
            searchEnvironments(namespace.id);
        }

        namespaceService.search({}, 0, 2000).then((res: Page<Namespace>) => {
            setNamespaceOptions(res.content);
        }).finally(() => {
            envModalRef.current.setOpen(true);
            setLoading(false);
        });
    }

    const searchEnvironments = (namespaceId: string) => {
        environmentService.search({ namespace: namespaceId }).then((res: Page<Environment>) => {
            setEnvironmentOptions(res.content);
        })
    }

    const beforeSaveModal = (e: ChangeEvent<HTMLFormElement>) => {
        e.preventDefault();
        if (JSON.stringify(selectedEnvironment) === '{}') {
            console.log('Select an Environment')
        } else {
            setNamespace(selectedNamespace);
            setEnvironment(selectedEnvironment);
            eventBus.emit('namespaceEnvironmentChange', { namespace: namespace, environment: environment })
            envModalRef.current.setOpen(false);
        }
    }

    const onNamespaceSelect = (namespace: Namespace) => {
        searchEnvironments(namespace.id || '');
        setSelectedNamespace(namespace)
        setEnvironment(JSON.parse('{}'))
    }

    useEffect(() => {
        if (!namespace) {
            onEnvModalOpen();
        } else {
            setSelectedNamespace(namespace);
            setSelectedEnvironment(environment);
        }
    }, [])

    return (
        <div className="mb-5">
            {(namespace && JSON.stringify(environment) !== '{}') && (
                <>
                    <div className="w-fit flex items-center bg-primary-color rounded text-white cursor-pointer"
                        onClick={() => onEnvModalOpen()}>
                        <span className="text-md p-2 pl-7 pr-7 border-r border-white">{namespace.name}</span>
                        <span className="text-md p-2 pl-7 pr-7 border-l border-white">{environment?.name}</span>
                    </div>
                </>
            )}
            {(selectedNamespace && !selectedEnvironment) && (
                <span className="w-fit flex items-center gap-1 bg-red-500 p-2 rounded text-white cursor-pointer" onClick={() => onEnvModalOpen()}>
                    Please, select an environment
                </span>
            )}
            {!selectedNamespace && (
                <span className="w-fit flex items-center gap-1 bg-red-500 p-2 rounded text-white cursor-pointer" onClick={() => onEnvModalOpen()}>
                    Please, select an namespace
                </span>
            )}
            <Modal title="Select your environment" ref={envModalRef} onSave={beforeSaveModal} beforeClose={() => beforeSaveModal} saveText="Apply">
                <div className="flex flex-col gap-5">
                    <div className="flex flex-col gap-2">
                        <FormLabel className="font-medium" required>Namespace</FormLabel>
                        <Select options={namespaceOptions || []} optionlabel="name" value={JSON.stringify(selectedNamespace)}
                            onChange={(e: ChangeEvent<HTMLSelectElement>) => onNamespaceSelect(JSON.parse(e.target.value))} />
                    </div>
                    <div className="flex flex-col gap-2">
                        <FormLabel className="font-medium" required>Environment</FormLabel>
                        <Select options={environmentOptions || []} optionlabel="name" value={JSON.stringify(selectedEnvironment)}
                            onChange={(e: ChangeEvent<HTMLSelectElement>) => setSelectedEnvironment(JSON.parse(e.target.value))} />
                    </div>
                </div>
            </Modal>
        </div>
    )
}
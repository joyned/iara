import { IoArrowForward } from "react-icons/io5";
import { useEnvironment } from "../providers/EnvironmentProvider";
import { useNamespace } from "../providers/NamespaceProvider"

export default function NamespaceEnvironment() {
    const { namespace } = useNamespace();
    const { environment } = useEnvironment();

    return (
        <div className="mb-5">
            {(namespace && JSON.stringify(environment) !== '{}') && (
                <div className="w-fit flex items-center gap-1 bg-primary-color p-2 rounded text-white">
                    <span className="font-semibold">{namespace.name}</span>
                    <IoArrowForward />
                    <span className="font-semibold">{environment?.name}</span>
                </div>
            )}
            {JSON.stringify(environment) === '{}' && (
                <span className="w-fit flex items-center gap-1 bg-red-500 p-2 rounded text-white">
                    Please, select an Environment
                </span>
            )}
        </div>
    )
}
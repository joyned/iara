import { createContext, useContext, useEffect, useState, type ReactNode } from "react";
import type { Namespace } from "../types/Namespace";

interface NamespaceType {
    namespace: Namespace | undefined;
    setNamespace: (namespace: Namespace | undefined) => void;
}

const NamespaceContext = createContext<NamespaceType>({
    namespace: { name: '' },
    setNamespace: () => { }
})

export const useNamespace = () => {
    const context = useContext(NamespaceContext);
    if (!context) {
        throw new Error('useNamepace should be used inside NamespaceProvider');
    }
    return context;
}

interface NamespaceProviderProps {
    children: ReactNode;
    initialValue?: string;
}

export const NamespaceProvider: React.FC<NamespaceProviderProps> = ({
    children,
}) => {
    const [namespace, setNamespaceState] = useState<Namespace | undefined>();
    const setNamespace = (newValue: Namespace | undefined) => {
        setNamespaceState(newValue);
    };

    const contextValue: NamespaceType = {
        namespace: namespace,
        setNamespace,
    };

    useEffect(() => {
    }, [])

    return (
        <NamespaceContext.Provider value={contextValue}>
            {children}
        </NamespaceContext.Provider>
    );
};

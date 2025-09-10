import { createContext, useContext, useEffect, useState, type ReactNode } from "react";
import type { Environment } from "../types/Environment";

interface EnvironmentType {
    environment: Environment;
    setEnvironment: (environment: Environment) => void;
}

const EnvironmentContext = createContext<EnvironmentType>({
    environment: { name: '' },
    setEnvironment: () => { }
})

export const useEnvironment = () => {
    const context = useContext(EnvironmentContext);
    if (!context) {
        throw new Error('useNamepace should be used inside EnvironmentProvider');
    }
    return context;
}

interface EnvironmentProviderProps {
    children: ReactNode;
    initialValue?: string;
}

export const EnvironmentProvider: React.FC<EnvironmentProviderProps> = ({
    children,
}) => {
    const [environment, setEnvironmentState] = useState<Environment>(JSON.parse(atob(localStorage.getItem('environment') || '') || '{}'));
    const setEnvironment = (newValue: Environment) => {
        setEnvironmentState(newValue);
        localStorage.setItem('environment', btoa(JSON.stringify(newValue)));
    };

    const contextValue: EnvironmentType = {
        environment: environment,
        setEnvironment,
    };

    useEffect(() => {
    }, [])

    return (
        <EnvironmentContext.Provider value={contextValue}>
            {children}
        </EnvironmentContext.Provider>
    );
};

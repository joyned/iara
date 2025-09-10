import { createContext, useContext, useState, type ReactElement, type SetStateAction } from "react";

const LoadingContext = createContext({
    loading: false,
    setLoading: (_value: SetStateAction<boolean>) => { }
})

export function LoadingProvider(props: { children: ReactElement | ReactElement[] }) {
    const [loading, setLoading] = useState(false);
    const value = { loading, setLoading };
    return (
        <LoadingContext.Provider value={value}>{props.children}</LoadingContext.Provider>
    );
}

export function useLoading() {
    const context = useContext(LoadingContext);
    if (!context) {
        throw new Error("useLoading must be used within LoadingProvider");
    }
    return context;
}
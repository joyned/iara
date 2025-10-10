import SensitiveCheckForm from "./form/SensitiveCheckForm";
import SSOForm from "./form/SSOForm";

export default function GeneralSettings() {
    return (
        <>
            <div className="flex flex-col gap-10">
                <SSOForm />
                <SensitiveCheckForm />
            </div>
        </>
    )
}

import Card from "../components/Card";
import SSOForm from "./form/SSOForm";

export default function GeneralSettings() {
    return (
        <>
            <div className="flex flex-col gap-10">
                <Card title="Servers">
                    <></>
                </Card>
                <SSOForm />
            </div>
        </>
    )
}

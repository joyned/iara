import { useEffect, useRef, useState, type ChangeEvent, type FormEvent } from "react";
import { useNavigate, useParams } from "react-router";
import { toast } from "react-toastify";
import Button from "../../components/Button";
import Card from "../../components/Card";
import { ConfirmDialog } from "../../components/ConfirmDialog";
import FormLabel from "../../components/FormLabel";
import Input from "../../components/Input";
import ListItem from "../../components/ListItem";
import { Modal } from "../../components/Modal";
import YamlEditor from "../../components/YamlEditor";
import { useEnvironment } from "../../providers/EnvironmentProvider";
import { useLoading } from "../../providers/LoadingProvider";
import { useNamespace } from "../../providers/NamespaceProvider";
import { KeyValueService } from "../../services/KeyValueService";
import type { KeyValue } from "../../types/KeyValue";
import type { KeyValueHistory } from "../../types/KeyValueHistory";
import type { Page } from "../../types/Page";
import { uuid } from "../../utils/UUID";

export default function KeyValueForm() {
    const service = new KeyValueService();
    const params = useParams();
    const navigate = useNavigate();

    const { namespace } = useNamespace();
    const { environment } = useEnvironment();

    const [isNew, setIsNew] = useState<boolean>(true);
    const [id, setId] = useState<string | undefined>();
    const [key, setKey] = useState<string>('');
    const [value, setValue] = useState<string>('');

    const [history, setHistory] = useState<KeyValueHistory[]>([]);
    const [selectedHistory, setSelectedHistory] = useState<KeyValueHistory>();

    const modalRef = useRef<any>(null);

    const { setLoading } = useLoading();

    useEffect(() => {
        const id = params.key;
        if (id) {
            if (id !== 'new') {
                setIsNew(false);
                setLoading(true);
                service.search({ id: id }).then((res: Page<KeyValue>) => {
                    const data = res.content[0]
                    setId(data.id)
                    setKey(data.key);
                    setValue(data.value || '')
                }).finally(() => setLoading(false));

                service.history(id).then((res: KeyValueHistory[]) => setHistory(res));
            }
        }
    }, [params.key, setLoading])

    const onSubmit = (event: FormEvent<HTMLFormElement>) => {
        event.preventDefault();

        if (!environment || !namespace) {
            return;
        }

        const kv: KeyValue = {
            id: id,
            key: key,
            value: value,
            environment: environment,
            namespace: namespace
        }

        setLoading(true);
        service.persist(kv).then((res: KeyValue) => {
            toast.success("K/V saved.")
            navigate(`/kv/${res.id}`)
            setId(res.id)
            setKey(res.key);
            setValue(res.value || '')
            setIsNew(false)
        }).finally(() => setLoading(false));
    }

    const onDelete = () => {
        if (id) {
            setLoading(true);
            service.delete(id).then(() => {
                navigate('/kv');
            }).finally(() => setLoading(false));
        }
    }

    const onOpenHistory = (h: KeyValueHistory) => {
        setSelectedHistory(h);
        modalRef.current.setOpen(true);
    }

    return (
        <div className="flex flex-col gap-5">
            <h1 className="text-2xl">KV Entry</h1>

            <Card>
                <form className="flex flex-col gap-5" onSubmit={onSubmit}>
                    <div className="flex flex-col gap-2">
                        <FormLabel htmlFor="kv-key" required>Key</FormLabel>
                        <Input name="kv-key" id="kv-key" type="text" value={key}
                            onChange={(e: ChangeEvent<HTMLInputElement>) => setKey(e.target.value)} disabled={!isNew} />
                    </div>
                    <div className="flex flex-col gap-2">
                        <FormLabel htmlFor="kv-value" required>Value</FormLabel>
                        <YamlEditor rows={10} resize={false} value={value} id="kv-value" name="kv-value"
                            onChange={(e: ChangeEvent<HTMLTextAreaElement>) => setValue(e.target.value)} />
                    </div>
                    <div className="flex justify-between gap-4">
                        <div className="flex gap-4">
                            <Button type="submit">Save</Button>
                            <Button variant="outline" onClick={() => navigate('/kv')} type="button">Back</Button>
                        </div>
                        {id &&
                            <ConfirmDialog onConfirm={onDelete}>
                                <Button className="w-[150px]" variant="danger" type="button">Delete</Button>
                            </ConfirmDialog>
                        }
                    </div>
                </form>
            </Card>

            {history.length > 0 &&
                <Card title="History">
                    {history.map((h: KeyValueHistory) => {
                        return <>
                            <ListItem name={new Date(h.updatedAt).toLocaleString()} onClick={() => onOpenHistory(h)} key={uuid()} />
                        </>
                    })}
                    <Modal ref={modalRef} title={`History - ${new Date(selectedHistory?.updatedAt || '').toLocaleString()}`}
                        cancelText="Close" hasSave={false}
                        saveText="Close" onSave={(e: ChangeEvent<HTMLFormElement>) => {
                            e.preventDefault();
                            modalRef.current.setOpen(false);
                        }}>
                        <div className="flex flex-col gap-2 text-white">
                            <YamlEditor rows={10} resize={false} value={selectedHistory?.value} disabled />
                            <div className="flex gap-2">
                                <span className="font-semibold">Author:</span>
                                <span>{selectedHistory?.user}</span>
                            </div>
                        </div>
                    </Modal>
                </Card>
            }
        </div>
    )
}
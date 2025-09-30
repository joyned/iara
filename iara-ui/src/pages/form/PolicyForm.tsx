import { useEffect, useRef, useState, type ChangeEvent } from "react";
import { BsInfoCircle } from "react-icons/bs";
import { useNavigate, useParams } from "react-router";
import { toast } from "react-toastify";
import Button from "../../components/Button";
import { ConfirmDialog } from "../../components/ConfirmDialog";
import FormLabel from "../../components/FormLabel";
import Input from "../../components/Input";
import { Modal } from "../../components/Modal";
import PolicyRulesInfo from "../../components/PolicyRulesInfo";
import RuleTextArea from "../../components/RuleTextArea";
import TextArea from "../../components/TextArea";
import { useLoading } from "../../providers/LoadingProvider";
import { PolicyService } from "../../services/PolicyService";
import type { Page } from "../../types/Page";
import type { Policy } from "../../types/Policy";

export default function PolicyForm() {
    const navigate = useNavigate();
    const params = useParams();
    const service = new PolicyService();

    const { setLoading } = useLoading();

    const [id, setId] = useState<string | undefined>(undefined);
    const [name, setName] = useState<string>('');
    const [description, setDescription] = useState<string | undefined>();
    const [rule, setRule] = useState<string>('');

    const modalRef = useRef<any>(null);

    useEffect(() => {
        const id = params.id;
        if (id && id !== 'new') {
            setLoading(true);
            service.search({ id: id }).then((res: Page<Policy>) => {
                const body = res.content[0];
                setId(body.id);
                setName(body.name);
                setDescription(body.description);
                setRule(body.rule);
            }).finally(() => setLoading(false));
        }
    }, [params.id, setLoading]);

    const onFormSubmit = (e: ChangeEvent<HTMLFormElement>) => {
        e.preventDefault();

        const policy: Policy = {
            id: id,
            name: name,
            description: description,
            rule: rule
        };

        setLoading(true);
        service.persist(policy).then((res: Policy) => {
            toast.success('Policy saved.');
            navigate(`/admin/policies/${res.id}`)
        }).finally(() => setLoading(false))
    }

    const onDeletePolicy = () => {
        if (id) {
            setLoading(true);
            service.delete(id).then(() => {
                toast.success('Policy deleted sucessfully.');
                navigate('/admin/policies');
            }).finally(() => setLoading(false));
        }
    }

    return (
        <div className="flex flex-col gap-5">
            <h1 className="text-2xl">policy</h1>
            <form className="flex flex-col gap-5" onSubmit={onFormSubmit}>
                <div className="flex flex-col gap-2">
                    <FormLabel htmlFor="policy-name" required>name</FormLabel>
                    <Input id="policy-name" name="policy-name" type="text" value={name}
                        onChange={(e: ChangeEvent<HTMLInputElement>) => setName(e.target.value)} />
                </div>
                <div className="flex flex-col gap-2">
                    <FormLabel htmlFor="policy-description">description</FormLabel>
                    <TextArea id="policy-description" name="policy-description" value={description}
                        onChange={(e: ChangeEvent<HTMLTextAreaElement>) => setDescription(e.target.value)}
                        rows={5} />
                </div>
                <div className="flex flex-col gap-2">
                    <FormLabel htmlFor="policy-description" required>rule</FormLabel>
                    <RuleTextArea id="policy-description" name="policy-description" value={rule}
                        onChange={(e: ChangeEvent<HTMLTextAreaElement>) => setRule(e.target.value)}
                        rows={5} />
                    <div className="flex gap-2 items-center w-fit cursor-pointer" onClick={() => modalRef.current.setOpen(true)}>
                        <BsInfoCircle />
                        <span>how to write?</span>
                    </div>
                </div>
                <div className="flex justify-between">
                    <div className="flex gap-2">
                        <Button>save</Button>
                        <Button type="button" variant="outline" onClick={() => navigate('/admin/policies')}>cancel</Button>
                    </div>
                    <div className="flex">
                        <ConfirmDialog onConfirm={onDeletePolicy}>
                            <Button type="button" variant="danger">delete policy</Button>
                        </ConfirmDialog>
                    </div>
                </div>
            </form>
            <Modal title="How to Write Access Control Policies" ref={modalRef} hasSave={false}>
                <PolicyRulesInfo />
            </Modal>
        </div>
    )
}
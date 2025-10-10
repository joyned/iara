import { useState, type ChangeEvent } from "react";
import { BsTrash } from "react-icons/bs";
import { IoInformationCircleOutline } from "react-icons/io5";
import Button from "../../components/Button";
import Card from "../../components/Card";
import Checkbox from "../../components/Checkbox";
import FormLabel from "../../components/FormLabel";
import Input from "../../components/Input";
import Tooltip from "../../components/Tooltip";

export default function SensitiveCheckForm() {

    const [enabled, setEnabled] = useState<boolean>(true);
    const [patterns, setPatterns] = useState<string[]>([]);

    const [newPattern, setNewPattern] = useState<string>('');
    const [isAdding, setIsAdding] = useState<boolean>(patterns.length === 0);

    const onAddNewPattern = () => {
        const toUpdate = [...patterns];
        toUpdate.push(newPattern);
        setPatterns(toUpdate);
        setNewPattern('');
        setIsAdding(false);
    }

    const onRemovePattern = (index: number) => {
        const toUpdate = [...patterns];
        toUpdate.splice(index, 1);
        setPatterns(toUpdate);
    }

    return (
        <Card title="Sentive Checker" closeable>
            <>
                <div className="flex flex-col gap-2 text-white">
                    <div className="flex justify-between">
                        <div className="flex gap-2">
                            <div className="flex items-baseline">
                                <span>Enable</span>
                            </div>
                            <Checkbox value={enabled} onChange={(value: boolean) => setEnabled(value)} />
                        </div>
                        <Tooltip text="this feature analyse all configuration that has been saved in KV to check if there are any sensitive data.">
                            <span className="flex gap-2 items-center">
                                <IoInformationCircleOutline />
                                About
                            </span>
                        </Tooltip>
                    </div>
                    {enabled &&
                        <div className="flex flex-col gap-5">
                            <div className="flex flex-col gap-2">
                                {patterns.length > 0 && <FormLabel required>Patterns to watch</FormLabel>}
                                {patterns.length > 0 && patterns.map((pattern: string, index: number) => {
                                    return (
                                        <div className="flex justify-between items-center bg-primary-darker-color text-white p-2 rounded">
                                            <pre>
                                                {index + 1} - {pattern}
                                            </pre>
                                            <BsTrash className="text-red-500 z-50 cursor-pointer" onClick={() => onRemovePattern(index)} />
                                        </div>
                                    )
                                })}
                                <div className="flex">
                                    {!isAdding && <Button type="button" variant="outline" onClick={() => setIsAdding(true)}>add new pattern</Button>}
                                    {isAdding &&
                                        <div className="flex flex-col gap-2 w-full">
                                            <FormLabel required>New pattern</FormLabel>
                                            <Input className="w-full" value={newPattern}
                                                onChange={(e: ChangeEvent<HTMLInputElement>) => setNewPattern(e.target.value)} />
                                            <div className="flex justify-end">
                                                <Button type="button" variant="outline" onClick={onAddNewPattern}>add</Button>
                                            </div>
                                        </div>
                                    }
                                </div>
                            </div>
                            <div className="flex">
                                <Button>save</Button>
                            </div>
                        </div>
                    }
                </div>
            </>
        </Card>
    )
}
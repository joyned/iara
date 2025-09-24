import { useEffect, useRef, type ChangeEvent, type TextareaHTMLAttributes } from "react";
import { uuid } from "../utils/UUID";

interface Props extends TextareaHTMLAttributes<HTMLTextAreaElement> {
    resize?: boolean
    value: string;
}

export default function RuleTextArea(props: Props) {
    const keywords: any = {
        'ALLOW': 'rule-keyword',
        'IN': 'rule-keyword',
        'AT': 'rule-keyword',
        'AND': 'rule-keyword',
        'READ': 'rule-action',
        'WRITE': 'rule-action',
        'SECRET': 'rule-resource',
        'KV': 'rule-resource',
    };

    const textareaRef = useRef<HTMLTextAreaElement>(null);
    const preRef = useRef<any>(null);

    useEffect(() => {
        preRef.current.innerHTML = props.value;
        applyCustomColors();
    }, [props.value])

    const onWrite = (e: ChangeEvent<HTMLTextAreaElement>) => {
        props.onChange && props.onChange(e);
    }

    const applyCustomColors = () => {
        if (!preRef.current) return;

        let html = props.value;
        Object.keys(keywords).forEach((kw: string) => {
            const regex = new RegExp(`\\b${kw}\\b`, 'g');
            html = html.replace(regex, `<span class="${keywords[kw]}">${kw}</span>`);
        })

        preRef.current.innerHTML = html;
    }

    const handleScroll = () => {
        if (textareaRef.current && preRef.current) {
            preRef.current.scrollTop = textareaRef.current.scrollTop;
            preRef.current.scrollLeft = textareaRef.current.scrollLeft;
        }
    }

    return (
        <>
            <div className="relative w-full">
                <div className="relative border border-gray-100 rounded font-monaco text-sm">
                    <textarea id={uuid()}
                        ref={textareaRef}
                        value={props.value}
                        className="relative w-full h-[300px] p-2.5 font-monaco border-none bg-transparent text-transparent resize-none z-20 caret-white whitespace-pre-wrap wrap-break-word"
                        spellCheck="false"
                        onChange={onWrite}
                        onScroll={handleScroll}
                        style={{
                            fontFamily: 'monospace',
                            fontSize: '14px',
                            lineHeight: '1.5'
                        }}
                    />
                    <pre ref={preRef}
                        className="absolute top-0 left-0 w-full h-[300px] p-2.5 border-none bg-code-block text-white overflow-auto whitespace-pre-wrap wrap-break-word z-10"
                        style={{
                            fontFamily: 'monospace',
                            fontSize: '14px',
                            lineHeight: '1.5',
                            margin: '0'
                        }}
                    />
                </div>
            </div>
        </>
    )
}
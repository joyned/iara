import { GoDotFill } from "react-icons/go";

export default function PolicyRulesInfo() {
    return (
        <div className="flex flex-col gap-2 text-white">
            <span>
                The policies you provided define permissions for different resources within a system.
                They follow a strict syntax to grant specific access levels. Here’s a breakdown of the structure and meaning:
            </span>
            <span className="font-semibold">
                Basic Syntax Structure
            </span>
            <span className="flex items-center gap-2">
                Every policy statement must begin with <pre className="code-block">ALLOW</pre> and follows this general pattern:
            </span>
            <pre className="code-block">
                ALLOW [PERMISSION] [IN RESOURCE_TYPE] AT [TARGET]
            </pre>
            <hr />
            <span className="flex items-center gap-2 font-semibold">
                1. Defining Permissions: <pre className="code-block">READ</pre>, <pre className="code-block">WRITE</pre>, or
                <pre className="code-block">READ AND WRITE</pre>
            </span>
            <span>
                The permission level defines what actions are allowed on the target resource.
            </span>
            <span className="flex items-center gap-2">
                <GoDotFill className="w-[10px]" /><pre className="code-block">READ</pre> Allows viewing/retrieving the resource.
            </span>
            <span className="flex items-center gap-2">
                <GoDotFill className="w-[10px]" /><pre className="code-block">WRITE</pre> Allows creating, updating, or deleting the resource.
            </span>
            <span className="flex items-center gap-2">
                <GoDotFill className="w-[10px]" /><pre className="code-block">READ AND WRITE</pre> Grants full read and modify access.
            </span>
            <hr />
            <span className="flex items-center gap-2 font-semibold">
                2. Granting Access to Admin Resources (<pre className="code-block">#</pre> resources)
            </span>
            <span>
                <span className="inline">To grant permissions for high-level administrative resources, you use the </span>
                <pre className="inline code-block">AT</pre>
                <span className="inline"> keyword directly followed by the resource name, which always starts with a hash symbol </span>
                <pre className="inline code-block">#</pre>.
            </span>
            <span className="font-semibold">
                Format:
            </span>
            <pre className="code-block">ALLOW [PERMISSION] AT #[RESOURCE_NAME]</pre>
            <span className="font-semibold">
                Example from your policies:
            </span>
            <span className="flex items-center gap-2">
                <GoDotFill className="w-[10px]" /> <pre className="code-block">ALLOW READ AND WRITE AT #*</pre>
            </span>
            <span className="flex gap-2 ">
                <span className="font-semibold">Meaning:</span>
                <span className="">
                    <span className="inline">This grants full read and write access to all </span>
                    <pre className="inline code-block">*</pre>
                    <span className="inline"> administrative resources </span>
                    <pre className="inline code-block">#</pre>,
                    <span className="inline"> such as NAMESPACES, USERS, POLICIES, etc.</span>
                </span>
            </span>
            <span className="flex items-center gap-2 font-semibold">
                Available Admin Resources:
            </span>
            <span className="flex items-center gap-2">
                <pre className="code-block">NAMESPACES</pre>, <pre className="code-block">USERS</pre>, <pre className="code-block">POLICIES</pre>,
                <pre className="code-block">ROLES</pre>, <pre className="code-block">TOKENS</pre>, <pre className="code-block">GENERAL</pre>
            </span>
            <hr />
            <span className="flex items-center gap-2 font-semibold">
                3. Granting Access to Namespaces/Environments (<pre className="code-block">@</pre> resources)
            </span>
            <span className="gap-2">
                <span className="inline">To grant permissions for a specific namespace or environment (e.g., DEV, QA, UAT, PRD),
                    you must specify the type of resource within that namespace </span>
                <pre className="inline code-block">KV</pre>
                <span className="inline"> or </span>
                <pre className="inline code-block">SECRET</pre>
                <span className="inline"> using the </span>
                <pre className="inline code-block">IN</pre>
                <span className="inline"> keyword. The target always starts with the at symbol </span>
                <pre className="inline code-block">@</pre>.
            </span>
            <span className="font-semibold">
                Format:
            </span>
            <pre className="code-block">ALLOW [PERMISSION] IN [RESOURCE_TYPE] AT @[NAMESPACE/ENVIRONMENT]</pre>
            <span className="font-semibold">
                Example from your policies:
            </span>
            <span className="flex items-center gap-2">
                <GoDotFill className="w-[10px]" /> <pre className="code-block">ALLOW READ AND WRITE IN KV AT @*</pre>
            </span>
            <span className="flex gap-2 ">
                <span className="font-semibold">Meaning:</span>
                <span className="flex items-center gap-2 flex-wrap">
                    Meaning: Grants full read and write access to the Key-Value (KV) store in all namespaces/environments (@*).
                </span>
            </span>
            <span className="flex items-center gap-2">
                <GoDotFill className="w-[10px]" /> <pre className="code-block">ALLOW READ IN SECRET AT @Developer/UAT</pre>
            </span>
            <span className="flex gap-2 ">
                <span className="font-semibold">Meaning:</span>
                <span className="flex items-center gap-2 flex-wrap">
                    Grants read-only access to Secrets in the UAT environment within the Developer namespace.
                </span>
            </span>
            <span className="flex items-center gap-2">
                <GoDotFill className="w-[10px]" /> <pre className="code-block">ALLOW READ AND WRITE IN KV AT @Developer/DEV</pre>
            </span>
            <span className="flex gap-2 ">
                <span className="font-semibold">Meaning:</span>
                <span className="flex items-center gap-2 flex-wrap">
                    Grants full read and write access to the Key-Value (KV) store only in the DEV environment within the Developer namespace.
                </span>
            </span>
            <hr />
            <span className="flex items-center gap-2">In summary, your policies establish a clear and secure structure:</span>
            <span>
                <span className="inline font-semibold">1.</span>
                <span className="inline"> Full admin access to everything </span>
                <pre className="inline code-block">#*</pre>
            </span>
            <span>
                <span className="inline font-semibold">2.</span>
                <span className="inline"> Broad read/write access to all KV stores and Secrets across all namespaces </span>
                <pre className="inline code-block">@*</pre>
            </span>
            <span>
                <span className="inline font-semibold">3.</span>
                <span className="inline"> Environment-specific permissions that become more restrictive (from Read/Write to Read-only) as you move from development </span>
                <pre className="inline code-block">DEV/QA</pre>
                <span className="inline"> to staging </span>
                <pre className="inline code-block">UAT</pre>
                <span className="inline"> and production </span>
                <pre className="inline code-block">PRD</pre>
            </span>
        </div>
    )
} 
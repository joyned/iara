import { FaHockeyPuck, FaIdCard, FaUsers } from "react-icons/fa"
import { IoIosSettings } from "react-icons/io"
import { MdOutlineGeneratingTokens, MdOutlinePolicy } from "react-icons/md"
import { VscGistSecret, VscSymbolNamespace } from "react-icons/vsc"

const IaraMenu = [
    {
        key: undefined,
        name: 'K/V',
        to: '/kv',
        icon: <FaHockeyPuck />
    },
    {
        key: undefined,
        name: 'Secrets',
        to: '/secrets',
        icon: <VscGistSecret />
    },
    {
        key: '#NAMESPACES',
        name: 'Namespaces',
        to: '/admin/namespaces',
        icon: <VscSymbolNamespace />
    },
    {
        key: '#USERS',
        name: 'Users',
        to: '/admin/users',
        icon: <FaUsers />
    },
    {
        key: '#POLICIES',
        name: 'Policy',
        to: '/admin/policies',
        icon: <MdOutlinePolicy />
    },
    {
        key: '#ROLES',
        name: 'Roles',
        to: '/admin/roles',
        icon: <FaIdCard />
    },
    {
        key: '#TOKENS',
        name: 'Tokens',
        to: '/admin/tokens',
        icon: <MdOutlineGeneratingTokens />
    },
    {
        key: '#GENERAL',
        name: 'General',
        to: '/admin/general',
        icon: <IoIosSettings />
    }
]

export { IaraMenu }

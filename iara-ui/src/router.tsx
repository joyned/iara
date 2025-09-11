import { createBrowserRouter, Navigate } from "react-router";
import Layout from "./layout/Layout";
import KeyValueForm from "./pages/form/KeyValueForm";
import NamespacesForm from "./pages/form/NamespacesForm";
import PolicyForm from "./pages/form/PolicyForm";
import RoleForm from "./pages/form/RoleForm";
import SecretsForm from "./pages/form/SecretsForm";
import UsersForm from "./pages/form/UsersForm";
import KeyValuePage from "./pages/KeyValuePage";
import LoginPage from "./pages/LoginPage";
import NamespacesPage from "./pages/NamespacesPage";
import PolicyPage from "./pages/PolicyPage";
import RolesPage from "./pages/RolesPage";
import SecretsPage from "./pages/SecretsPage";
import UsersPage from "./pages/UsersPage";
import GeneralSettings from "./pages/GeneralSettingsPage";
import GuardComponent from "./guard/GuardComponent";
import ApplicationPageToken from "./pages/ApplicationTokenPage";
import UserSettingsPage from "./pages/UserSettingsPage";

export const router = createBrowserRouter([
    {
        path: "/login",
        element: <LoginPage />,
    },
    {
        path: "/",
        element: <Layout />,
        children: [
            {
                path: "",
                index: true,
                element: <Navigate to={"/kv"} />
            },
            {
                path: "kv",
                element: <GuardComponent destiny={<KeyValuePage />} />
            },
            {
                path: "kv/:key",
                element: <KeyValueForm />
            },
            {
                path: "secrets",
                element: <SecretsPage />
            },
            {
                path: "secrets/:id",
                element: <SecretsForm />
            },
            {
                path: "user/settings",
                element: <UserSettingsPage />
            },
            {
                path: "admin/general",
                element: <GeneralSettings />
            },
            {
                path: "admin/namespaces",
                element: <NamespacesPage />
            },
            {
                path: "admin/namespaces/:id",
                element: <NamespacesForm />
            },
            {
                path: "admin/users",
                element: <UsersPage />
            },
            {
                path: "admin/users/:id",
                element: <UsersForm />
            },
            {
                path: "admin/policies",
                element: <PolicyPage />
            },
            {
                path: "admin/policies/:id",
                element: <PolicyForm />
            },
            {
                path: "admin/roles",
                element: <RolesPage />
            },
            {
                path: "admin/roles/:id",
                element: <RoleForm />
            },
            {
                path: "admin/tokens",
                element: <ApplicationPageToken />
            }
        ]
    }
]);

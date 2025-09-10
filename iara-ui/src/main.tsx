import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import { RouterProvider } from 'react-router'
import './index.css'
import { EnvironmentProvider } from './providers/EnvironmentProvider'
import { NamespaceProvider } from './providers/NamespaceProvider'
import { router } from './router'
import { LoadingProvider } from './providers/LoadingProvider'


createRoot(document.getElementById('root')!).render(
  <StrictMode>
    <NamespaceProvider>
      <EnvironmentProvider>
        <LoadingProvider>
          <RouterProvider router={router} ></RouterProvider>
        </LoadingProvider>
      </EnvironmentProvider>
    </NamespaceProvider>
  </StrictMode>,
)

import { ReactKeycloakProvider, useKeycloak } from "@react-keycloak/web";
import Keycloak from "keycloak-js";
import type { ReactNode } from "react";

export const keycloakContext = new Keycloak({
  url: "http://auth.ucode.com",
  realm: "leetcode",
  clientId: "leetcode_frontend",
});

export interface KeycloakWrapperProps {
  children: ReactNode | ReactNode[];
}

export const keycloakContextInit = keycloakContext.init({
  onLoad: "check-sso",
  checkLoginIframe: false,
  flow: "standard",
  pkceMethod: "S256",
  enableLogging: true,
});

export const KeycloakWrapper = ({ children }: KeycloakWrapperProps) => {
  return (
    <ReactKeycloakProvider authClient={keycloakContext}>
      {children}
    </ReactKeycloakProvider>
  );
};

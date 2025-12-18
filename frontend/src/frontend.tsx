import { createRoot } from "react-dom/client";
import { App } from "./App";
import {
  keycloakContextInit,
  KeycloakWrapper,
} from "./features/KeycloakWrapper";

async function start() {
  await keycloakContextInit;

  const root = createRoot(document.getElementById("root")!);
  root.render(
    <KeycloakWrapper>
      <App />
    </KeycloakWrapper>,
  );
}

if (document.readyState === "loading") {
  document.addEventListener("DOMContentLoaded", start);
} else {
  start();
}

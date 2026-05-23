import { useState } from "react";
import { I18nProvider } from "../shared/i18n/I18nProvider";
import { AppLayout } from "./AppLayout";
import { AppStateProvider } from "./AppState";
import type { AppRoute } from "./routes";

function AppInner() {
  const [route, setRoute] = useState<AppRoute>("home");

  return (
    <AppLayout activeRoute={route} onNavigate={setRoute}>
      <section className="page">
        <h1>青岛汇 H5</h1>
        <p>Operational client shell</p>
      </section>
    </AppLayout>
  );
}

export function App() {
  return (
    <AppStateProvider>
      <I18nProvider>
        <AppInner />
      </I18nProvider>
    </AppStateProvider>
  );
}

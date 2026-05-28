import { createContext, useContext, type ReactNode } from "react";
import { useAppState } from "../../app/AppState";
import { getMessage, type MessageKey } from "./messages";

const I18nContext = createContext<{ t: (key: MessageKey) => string } | null>(null);

export function I18nProvider({ children }: { children: ReactNode }) {
  const { language } = useAppState();

  return <I18nContext.Provider value={{ t: (key) => getMessage(language, key) }}>{children}</I18nContext.Provider>;
}

export function useI18n() {
  const value = useContext(I18nContext);

  if (!value) {
    throw new Error("useI18n must be used inside I18nProvider");
  }

  return value;
}

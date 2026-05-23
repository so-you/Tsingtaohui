import { ClipboardList, Home, PackageSearch, QrCode, UserRound } from "lucide-react";
import type { ReactNode } from "react";
import { useI18n } from "../shared/i18n/I18nProvider";
import type { MessageKey } from "../shared/i18n/messages";
import type { AppRoute } from "./routes";

interface AppLayoutProps {
  activeRoute: AppRoute;
  children: ReactNode;
  onNavigate: (route: AppRoute) => void;
}

const tabs: Array<{ route: AppRoute; labelKey: MessageKey; icon: typeof Home }> = [
  { route: "home", labelKey: "tabs.home", icon: Home },
  { route: "goods", labelKey: "tabs.goods", icon: PackageSearch },
  { route: "orders", labelKey: "tabs.orders", icon: ClipboardList },
  { route: "scan", labelKey: "tabs.scan", icon: QrCode },
  { route: "mine", labelKey: "tabs.mine", icon: UserRound }
];

export function AppLayout({ activeRoute, onNavigate, children }: AppLayoutProps) {
  const { t } = useI18n();

  return (
    <div className="app-shell">
      <div className="app-content">{children}</div>
      <nav className="bottom-tabs" aria-label="Primary">
        {tabs.map((tab) => {
          const Icon = tab.icon;
          const isActive = activeRoute === tab.route;

          return (
            <button key={tab.route} type="button" className={isActive ? "tab tab-active" : "tab"} onClick={() => onNavigate(tab.route)}>
              <Icon size={20} />
              <span>{t(tab.labelKey)}</span>
            </button>
          );
        })}
      </nav>
    </div>
  );
}

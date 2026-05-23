import { Languages, ShipWheel, UserRound } from "lucide-react";
import { useAppState } from "../../app/AppState";
import { StatusBadge } from "../../shared/components/StatusBadge";
import { useI18n } from "../../shared/i18n/I18nProvider";
import { formatDateTime } from "../../shared/lib/format";

export function ProfilePage() {
  const { currentShip, language, orders, setLanguage } = useAppState();
  const { t } = useI18n();
  const activeOrderCount = orders.filter((order) => !["COMPLETED", "CANCELLED"].includes(order.status)).length;

  return (
    <section className="page profile-page">
      <header className="page-header">
        <div>
          <p className="eyebrow">Crew</p>
          <h1>{t("tabs.mine")}</h1>
        </div>
        <StatusBadge tone="green">{activeOrderCount} orders</StatusBadge>
      </header>

      <section className="profile-panel">
        <div className="panel-title">
          <Languages size={20} />
          <h2>{t("profile.language")}</h2>
        </div>
        <div className="segmented-control" role="group" aria-label={t("profile.language")}>
          <button className={language === "zh" ? "segment-active" : ""} onClick={() => setLanguage("zh")} type="button">
            中文
          </button>
          <button className={language === "en" ? "segment-active" : ""} onClick={() => setLanguage("en")} type="button">
            English
          </button>
        </div>
      </section>

      <section className="profile-panel">
        <div className="panel-title">
          <ShipWheel size={20} />
          <h2>{language === "zh" ? "当前船舶" : "Current Ship"}</h2>
        </div>
        <div className="detail-grid">
          <span>{language === "zh" ? "船名" : "Ship"}</span>
          <strong>{currentShip?.shipName ?? "-"}</strong>
          <span>IMO / MMSI</span>
          <strong>{[currentShip?.imo, currentShip?.mmsi].filter(Boolean).join(" / ") || "-"}</strong>
          <span>{language === "zh" ? "泊位 / 锚地" : "Berth / Anchorage"}</span>
          <strong>{currentShip?.berthOrAnchorage ?? "-"}</strong>
          <span>{language === "zh" ? "代理人" : "Agent"}</span>
          <strong>{currentShip?.shippingAgentName ?? "-"}</strong>
          <span>{language === "zh" ? "位置更新" : "Location Updated"}</span>
          <strong>{formatDateTime(currentShip?.locationUpdatedAt)}</strong>
        </div>
      </section>

      <section className="profile-panel">
        <div className="panel-title">
          <UserRound size={20} />
          <h2>{language === "zh" ? "收货信息" : "Receiving"}</h2>
        </div>
        <div className="detail-grid">
          <span>{language === "zh" ? "默认收货人" : "Default Consignee"}</span>
          <strong>Alex Chen</strong>
          <span>Cabin No.</span>
          <strong>C-203</strong>
          <span>{language === "zh" ? "联系方式" : "Contact"}</span>
          <strong>+86 532 8000 1001</strong>
        </div>
      </section>
    </section>
  );
}

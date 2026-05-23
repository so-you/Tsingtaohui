import { PackageSearch, QrCode, ShipWheel, UserRound } from "lucide-react";
import { useAppState } from "../../app/AppState";
import type { AppRoute } from "../../app/routes";
import { Button } from "../../shared/components/Button";
import { StatusBadge } from "../../shared/components/StatusBadge";
import { products } from "../../shared/data/mockData";
import { useI18n } from "../../shared/i18n/I18nProvider";
import { formatCurrency } from "../../shared/lib/format";
import { ShipContextPanel } from "../ship/ShipContextPanel";

export function HomePage({ onNavigate }: { onNavigate: (route: AppRoute) => void }) {
  const { currentShip, orders } = useAppState();
  const { t } = useI18n();
  const activeOrders = orders.filter((order) => !["COMPLETED", "CANCELLED"].includes(order.status));
  const firstActiveOrder = activeOrders[0];

  return (
    <section className="page home-page">
      <ShipContextPanel ship={currentShip} />

      <div className="metric-grid">
        <div className="metric-tile">
          <strong>{activeOrders.length}</strong>
          <span>{t("home.activeOrders")}</span>
        </div>
        <div className="metric-tile">
          <strong>{products.filter((product) => product.inventory.availableQty > 0).length}</strong>
          <span>{t("home.availableSkus")}</span>
        </div>
      </div>

      <div className="entry-grid">
        <Button onClick={() => onNavigate("goods")} variant="secondary">
          <PackageSearch size={18} /> {t("tabs.goods")}
        </Button>
        <Button onClick={() => onNavigate("orders")} variant="secondary">
          <ShipWheel size={18} /> {t("tabs.orders")}
        </Button>
        <Button onClick={() => onNavigate("scan")} variant="secondary">
          <QrCode size={18} /> {t("tabs.scan")}
        </Button>
        <Button onClick={() => onNavigate("mine")} variant="secondary">
          <UserRound size={18} /> {t("tabs.mine")}
        </Button>
      </div>

      {firstActiveOrder ? (
        <button className="active-order-card" type="button" onClick={() => onNavigate("orders")}>
          <span>
            <strong>{firstActiveOrder.id}</strong>
            <small>{firstActiveOrder.items.length} items · {formatCurrency(firstActiveOrder.totalPrice)}</small>
          </span>
          <StatusBadge tone="blue">{firstActiveOrder.status}</StatusBadge>
        </button>
      ) : null}
    </section>
  );
}

import { useMemo, useState } from "react";
import { useAppState } from "../../app/AppState";
import { EmptyState } from "../../shared/components/EmptyState";
import { StatusBadge } from "../../shared/components/StatusBadge";
import { useI18n } from "../../shared/i18n/I18nProvider";
import { formatCurrency, formatDateTime } from "../../shared/lib/format";
import type { Order } from "../../shared/types/domain";
import { getOrderStatusLabel, orderStatusMeta } from "./OrderTimeline";

type OrderFilter = "all" | "active" | "completed";

function matchesFilter(order: Order, filter: OrderFilter): boolean {
  if (filter === "completed") {
    return order.status === "COMPLETED";
  }

  if (filter === "active") {
    return !["COMPLETED", "CANCELLED"].includes(order.status);
  }

  return true;
}

export function OrdersPage({ onOpenOrder }: { onOpenOrder: (orderId: string) => void }) {
  const { language, orders } = useAppState();
  const { t } = useI18n();
  const [filter, setFilter] = useState<OrderFilter>("all");
  const visibleOrders = useMemo(() => orders.filter((order) => matchesFilter(order, filter)), [filter, orders]);
  const filters: Array<{ value: OrderFilter; label: string }> = [
    { value: "all", label: language === "zh" ? "全部" : "All" },
    { value: "active", label: language === "zh" ? "进行中" : "Active" },
    { value: "completed", label: language === "zh" ? "已完成" : "Completed" }
  ];

  return (
    <section className="page orders-page">
      <header className="page-header">
        <div>
          <p className="eyebrow">Orders</p>
          <h1>{t("orders.title")}</h1>
        </div>
      </header>

      <div className="category-row" role="tablist" aria-label={language === "zh" ? "订单筛选" : "Order filters"}>
        {filters.map((item) => (
          <button
            className={filter === item.value ? "category-chip category-chip-active" : "category-chip"}
            key={item.value}
            onClick={() => setFilter(item.value)}
            type="button"
          >
            {item.label}
          </button>
        ))}
      </div>

      {visibleOrders.length === 0 ? (
        <EmptyState title="暂无订单" detail="当前筛选条件下没有订单。" />
      ) : (
        <div className="order-list">
          {visibleOrders.map((order) => (
            <button className="order-card" key={order.id} onClick={() => onOpenOrder(order.id)} type="button">
              <span>
                <strong>{order.id}</strong>
                <small>{formatDateTime(order.createdAt)} · {formatCurrency(order.totalPrice)}</small>
              </span>
              <StatusBadge tone={orderStatusMeta[order.status].tone}>{getOrderStatusLabel(order.status, language)}</StatusBadge>
            </button>
          ))}
        </div>
      )}
    </section>
  );
}

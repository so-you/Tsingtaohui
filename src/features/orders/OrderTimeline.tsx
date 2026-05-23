import type { Language, Order, OrderStatus, TradeMode } from "../../shared/types/domain";
import { formatDateTime } from "../../shared/lib/format";

type StatusTone = "blue" | "green" | "amber" | "red" | "gray";

export const orderStatusFlow: OrderStatus[] = [
  "PENDING_CONFIRM",
  "CONFIRMED",
  "WAREHOUSE_PROCESSING",
  "PENDING_OUTBOUND",
  "OUTBOUND",
  "PENDING_LOADING",
  "IN_DELIVERY",
  "PENDING_RECEIPT",
  "COMPLETED"
];

export const orderStatusMeta: Record<OrderStatus, { labelZh: string; labelEn: string; tone: StatusTone }> = {
  PENDING_CONFIRM: { labelZh: "待确认", labelEn: "Pending Confirm", tone: "amber" },
  CONFIRMED: { labelZh: "已确认", labelEn: "Confirmed", tone: "blue" },
  WAREHOUSE_PROCESSING: { labelZh: "仓库处理中", labelEn: "Warehouse Processing", tone: "blue" },
  PENDING_OUTBOUND: { labelZh: "待出库", labelEn: "Pending Outbound", tone: "blue" },
  OUTBOUND: { labelZh: "已出库", labelEn: "Outbound", tone: "blue" },
  PENDING_LOADING: { labelZh: "待装机", labelEn: "Pending Loading", tone: "blue" },
  IN_DELIVERY: { labelZh: "配送中", labelEn: "In Delivery", tone: "blue" },
  PENDING_RECEIPT: { labelZh: "待签收", labelEn: "Pending Receipt", tone: "amber" },
  COMPLETED: { labelZh: "已完成", labelEn: "Completed", tone: "green" },
  CANCELLED: { labelZh: "已取消", labelEn: "Cancelled", tone: "gray" },
  EXCEPTION: { labelZh: "异常", labelEn: "Exception", tone: "red" }
};

export function getOrderStatusLabel(status: OrderStatus, language: Language): string {
  const meta = orderStatusMeta[status];
  return language === "zh" ? meta.labelZh : meta.labelEn;
}

export function getTradeModeLabel(mode: TradeMode, language: Language): string {
  if (mode === "AUTO_TRADE") {
    return language === "zh" ? "自动交易" : "Auto-Trade";
  }

  return language === "zh" ? "撮合订单" : "Matching Order";
}

export function OrderTimeline({ order, language }: { order: Order; language: Language }) {
  const activeIndex = orderStatusFlow.indexOf(order.status);
  const statuses: OrderStatus[] =
    activeIndex >= 0 ? orderStatusFlow.slice(0, activeIndex + 1) : ["PENDING_CONFIRM", order.status];

  return (
    <ol className="order-timeline" aria-label={language === "zh" ? "订单进度" : "Order progress"}>
      {statuses.map((status, index) => {
        const isCurrent = status === order.status;
        const reachedAt = status === "PENDING_CONFIRM" ? order.createdAt : status === "COMPLETED" ? order.completedAt : undefined;

        return (
          <li className={isCurrent ? "timeline-item timeline-current" : "timeline-item"} key={`${status}-${index}`}>
            <span className="timeline-dot" />
            <span>
              <strong>{getOrderStatusLabel(status, language)}</strong>
              <small>{reachedAt ? formatDateTime(reachedAt) : isCurrent ? formatDateTime(order.estimatedArrival) : "-"}</small>
            </span>
          </li>
        );
      })}
    </ol>
  );
}

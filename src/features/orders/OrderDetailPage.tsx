import { ArrowLeft, RefreshCcw } from "lucide-react";
import { useAppState } from "../../app/AppState";
import { Button } from "../../shared/components/Button";
import { EmptyState } from "../../shared/components/EmptyState";
import { StatusBadge } from "../../shared/components/StatusBadge";
import { useI18n } from "../../shared/i18n/I18nProvider";
import { formatCurrency, formatDateTime, formatMeasure } from "../../shared/lib/format";
import { advanceOrderStatus, confirmReceipt } from "./orderLogic";
import { getOrderStatusLabel, getTradeModeLabel, OrderTimeline, orderStatusMeta } from "./OrderTimeline";
import { ReceiptConfirm } from "./ReceiptConfirm";

interface OrderDetailPageProps {
  orderId: string;
  onBack: () => void;
}

export function OrderDetailPage({ orderId, onBack }: OrderDetailPageProps) {
  const { language, orders, updateOrder } = useAppState();
  const { t } = useI18n();
  const order = orders.find((candidate) => candidate.id === orderId);

  if (!order) {
    return <EmptyState title="未找到订单" detail="请返回订单列表重新选择。" />;
  }

  const selectedOrder = order;

  function handleRefresh() {
    updateOrder(advanceOrderStatus(selectedOrder));
  }

  function handleConfirm(method: NonNullable<typeof selectedOrder.receiptMethod>) {
    updateOrder(confirmReceipt(selectedOrder, method, new Date().toISOString()));
  }

  return (
    <section className="page order-detail-page">
      <header className="page-header">
        <div>
          <p className="eyebrow">Order</p>
          <h1>{t("orders.detail")}</h1>
        </div>
        <Button onClick={onBack} variant="ghost">
          <ArrowLeft size={18} /> 返回
        </Button>
      </header>

      <article className="order-hero">
        <div>
          <strong>{order.id}</strong>
          <span>{formatDateTime(order.createdAt)}</span>
        </div>
        <StatusBadge tone={orderStatusMeta[selectedOrder.status].tone}>{getOrderStatusLabel(selectedOrder.status, language)}</StatusBadge>
      </article>

      <div className="detail-actions">
        <Button onClick={handleRefresh} variant="secondary">
          <RefreshCcw size={18} /> {t("common.refresh")}
        </Button>
      </div>

      <section className="detail-panel">
        <h2>配送信息</h2>
        <div className="detail-grid">
          <span>船舶</span>
          <strong>{selectedOrder.ship.shipName}</strong>
          <span>泊位 / 锚地</span>
          <strong>{selectedOrder.ship.berthOrAnchorage}</strong>
          <span>收货人</span>
          <strong>{selectedOrder.consigneeName}</strong>
          <span>Cabin No.</span>
          <strong>{selectedOrder.cabinNo}</strong>
          <span>交易模式</span>
          <strong>{getTradeModeLabel(selectedOrder.tradeMode, language)}</strong>
          <span>预计到达</span>
          <strong>{formatDateTime(selectedOrder.estimatedArrival)}</strong>
        </div>
      </section>

      <section className="detail-panel">
        <h2>商品明细</h2>
        <div className="order-item-list">
          {selectedOrder.items.map((item) => (
            <div className="order-item-row" key={item.skuCode}>
              <span>
                <strong>{language === "zh" ? item.nameZh : item.nameEn}</strong>
                <small>{item.skuCode} · x{item.quantity}</small>
              </span>
              <strong>{formatCurrency(item.unitPrice * item.quantity)}</strong>
            </div>
          ))}
        </div>
        <div className="summary-panel">
          <span>金额</span>
          <strong>{formatCurrency(selectedOrder.totalPrice)}</strong>
          <span>重量</span>
          <strong>{formatMeasure(selectedOrder.totalWeightKg, "kg")}</strong>
          <span>体积</span>
          <strong>{formatMeasure(selectedOrder.totalVolumeM3, "m3")}</strong>
        </div>
      </section>

      <section className="detail-panel">
        <h2>订单进度</h2>
        <OrderTimeline order={selectedOrder} language={language} />
      </section>

      <ReceiptConfirm order={selectedOrder} onConfirm={handleConfirm} />
    </section>
  );
}

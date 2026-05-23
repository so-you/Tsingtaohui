import { useEffect, useMemo, useState } from "react";
import { useAppState } from "../../app/AppState";
import { Button } from "../../shared/components/Button";
import { Field } from "../../shared/components/Field";
import { products } from "../../shared/data/mockData";
import { formatCurrency, formatMeasure } from "../../shared/lib/format";
import { createOrderFromCart } from "../orders/orderLogic";
import { updateShipLocation } from "../ship/shipLogic";
import { calculateCartTotals } from "./cartLogic";

export interface OrderConfirmPageProps {
  onOrderCreated: (orderId: string) => void;
}

export function OrderConfirmPage({ onOrderCreated }: OrderConfirmPageProps) {
  const { cartItems, clearCart, currentShip, saveOrder, setCurrentShip } = useAppState();
  const [berthOrAnchorage, setBerthOrAnchorage] = useState(currentShip?.berthOrAnchorage ?? "");
  const [consigneeName, setConsigneeName] = useState("");
  const [cabinNo, setCabinNo] = useState("");
  const [contactInfo, setContactInfo] = useState("");
  const [expectedDeliveryTime, setExpectedDeliveryTime] = useState("");
  const [remark, setRemark] = useState("");
  const [errors, setErrors] = useState<Record<string, string>>({});
  const [createdOrderId, setCreatedOrderId] = useState<string | null>(null);
  const totals = useMemo(() => calculateCartTotals(cartItems, products), [cartItems]);

  useEffect(() => {
    if (currentShip && !berthOrAnchorage) {
      setBerthOrAnchorage(currentShip.berthOrAnchorage);
    }
  }, [berthOrAnchorage, currentShip]);

  function handleSubmit() {
    const nextErrors: Record<string, string> = {};

    if (cartItems.length === 0) nextErrors.cart = "购物车为空";
    if (!currentShip) nextErrors.ship = "请确认船舶信息";
    if (!consigneeName.trim()) nextErrors.consigneeName = "请填写收货人";
    if (!cabinNo.trim()) nextErrors.cabinNo = "请填写 Cabin No.";

    setErrors(nextErrors);

    if (Object.keys(nextErrors).length > 0 || !currentShip) {
      return;
    }

    const ship =
      berthOrAnchorage.trim() && berthOrAnchorage.trim() !== currentShip.berthOrAnchorage
        ? updateShipLocation(currentShip, berthOrAnchorage.trim(), new Date().toISOString())
        : currentShip;

    setCurrentShip(ship);

    const order = createOrderFromCart({
      cartItems,
      products,
      ship,
      consigneeName: consigneeName.trim(),
      cabinNo: cabinNo.trim(),
      contactInfo: contactInfo.trim() || undefined,
      expectedDeliveryTime: expectedDeliveryTime || undefined,
      remark: remark.trim() || undefined,
      now: new Date().toISOString()
    });

    saveOrder(order);
    clearCart();
    setCreatedOrderId(order.id);
    onOrderCreated(order.id);
  }

  return (
    <section className="page order-confirm-page">
      <header className="page-header">
        <div>
          <p className="eyebrow">Checkout</p>
          <h1>订单确认</h1>
        </div>
        {createdOrderId ? <span className="success-banner">订单已创建</span> : null}
      </header>

      {errors.cart || errors.ship ? (
        <ul className="validation-list">
          {errors.cart ? <li>{errors.cart}</li> : null}
          {errors.ship ? <li>{errors.ship}</li> : null}
        </ul>
      ) : null}

      <div className="cart-list">
        {cartItems.map((item) => {
          const product = products.find((candidate) => candidate.id === item.productId);
          return product ? (
            <article className="cart-line" key={item.productId}>
              <div>
                <strong>{product.nameZh}</strong>
                <span>{item.quantity} x {formatCurrency(product.price)}</span>
              </div>
              <strong>{formatCurrency(product.price * item.quantity)}</strong>
            </article>
          ) : null;
        })}
      </div>

      <div className="summary-panel">
        <span>金额</span>
        <strong>{formatCurrency(totals.totalPrice)}</strong>
        <span>重量</span>
        <strong>{formatMeasure(totals.totalWeightKg, "kg")}</strong>
        <span>体积</span>
        <strong>{formatMeasure(totals.totalVolumeM3, "m3")}</strong>
      </div>

      <div className="form-panel">
        <Field label="泊位 / 锚地" value={berthOrAnchorage} onChange={(event) => setBerthOrAnchorage(event.target.value)} />
        <Field label="船舶代理人" value={currentShip?.shippingAgentName ?? ""} readOnly />
        <Field label="收货人" error={errors.consigneeName} value={consigneeName} onChange={(event) => setConsigneeName(event.target.value)} />
        <Field label="Cabin No." error={errors.cabinNo} value={cabinNo} onChange={(event) => setCabinNo(event.target.value)} />
        <Field label="联系方式" value={contactInfo} onChange={(event) => setContactInfo(event.target.value)} />
        <Field label="期望送达时间" type="datetime-local" value={expectedDeliveryTime} onChange={(event) => setExpectedDeliveryTime(event.target.value)} />
        <label className="field">
          <span className="field-label">备注</span>
          <textarea className="field-input text-area" value={remark} onChange={(event) => setRemark(event.target.value)} />
        </label>
      </div>

      <div className="sticky-actions">
        <Button onClick={handleSubmit}>提交订单</Button>
      </div>
    </section>
  );
}

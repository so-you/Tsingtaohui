import { useMemo } from "react";
import { useAppState } from "../../app/AppState";
import { Button } from "../../shared/components/Button";
import { EmptyState } from "../../shared/components/EmptyState";
import { QuantityStepper } from "../../shared/components/QuantityStepper";
import { products } from "../../shared/data/mockData";
import { formatCurrency, formatMeasure } from "../../shared/lib/format";
import { calculateCartTotals, validateCartForSubmission } from "./cartLogic";

export function CartPage({ onCheckout }: { onCheckout: () => void }) {
  const { cartItems, updateCartQuantity, removeFromCart } = useAppState();
  const totals = useMemo(() => calculateCartTotals(cartItems, products), [cartItems]);
  const validationErrors = useMemo(() => validateCartForSubmission(cartItems, products), [cartItems]);

  if (cartItems.length === 0) {
    return <EmptyState title="购物车为空" detail="请先从商品列表添加保税仓商品。" />;
  }

  return (
    <section className="page cart-page">
      <h1>购物车</h1>
      <div className="cart-list">
        {cartItems.map((item) => {
          const product = products.find((candidate) => candidate.id === item.productId);

          if (!product) {
            return null;
          }

          return (
            <article className="cart-line" key={item.productId}>
              <div>
                <strong>{product.nameZh}</strong>
                <span>{formatCurrency(product.price)} · {product.specification}</span>
              </div>
              <QuantityStepper value={item.quantity} onChange={(quantity) => updateCartQuantity(item.productId, quantity)} />
              <button className="link-button" type="button" onClick={() => removeFromCart(item.productId)}>
                移除
              </button>
            </article>
          );
        })}
      </div>
      {validationErrors.length > 0 ? (
        <ul className="validation-list">
          {validationErrors.map((error) => (
            <li key={`${error.code}-${error.productId ?? "cart"}`}>{error.messageZh}</li>
          ))}
        </ul>
      ) : null}
      <div className="summary-panel">
        <span>合计</span>
        <strong>{formatCurrency(totals.totalPrice)}</strong>
        <span>重量</span>
        <strong>{formatMeasure(totals.totalWeightKg, "kg")}</strong>
        <span>体积</span>
        <strong>{formatMeasure(totals.totalVolumeM3, "m3")}</strong>
      </div>
      <Button onClick={onCheckout}>继续确认订单</Button>
    </section>
  );
}

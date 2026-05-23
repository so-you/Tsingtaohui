import { useAppState } from "../../app/AppState";
import { Button } from "../../shared/components/Button";
import { StatusBadge } from "../../shared/components/StatusBadge";
import { products } from "../../shared/data/mockData";
import { formatCurrency, formatMeasure } from "../../shared/lib/format";

export interface ProductDetailPageProps {
  onBack: () => void;
  onOpenCart: () => void;
  productId: string;
}

export function ProductDetailPage({ productId, onBack, onOpenCart }: ProductDetailPageProps) {
  const { addToCart, language } = useAppState();
  const product = products.find((candidate) => candidate.id === productId) ?? products[0];
  const name = language === "zh" ? product.nameZh : product.nameEn;
  const description = language === "zh" ? product.descriptionZh : product.descriptionEn;
  const restriction = language === "zh" ? product.restrictionNoteZh : product.restrictionNoteEn;
  const canAdd = product.inventory.availableQty > 0 && product.droneDeliverable;

  function handleBuyNow() {
    addToCart(product.id);
    onOpenCart();
  }

  return (
    <section className="page product-detail-page">
      <button className="link-button" type="button" onClick={onBack}>
        Back
      </button>
      <div className={`product-detail-art product-art-${product.imageTone}`}>
        <span>{product.skuCode}</span>
      </div>
      <div className="detail-panel">
        <div className="product-card-heading">
          <h1>{name}</h1>
          <strong>{formatCurrency(product.price)}</strong>
        </div>
        <p>{description}</p>
        <div className="detail-grid">
          <span>Spec</span>
          <strong>{product.specification}</strong>
          <span>Weight</span>
          <strong>{formatMeasure(product.weightKg, "kg")}</strong>
          <span>Volume</span>
          <strong>{formatMeasure(product.volumeM3, "m3")}</strong>
          <span>Stock</span>
          <strong>{product.inventory.availableQty}</strong>
        </div>
        <div className="inline-statuses">
          <StatusBadge tone={product.droneDeliverable ? "blue" : "amber"}>{product.droneDeliverable ? "Drone deliverable" : "Manual check"}</StatusBadge>
          {restriction ? <StatusBadge tone="amber">{restriction}</StatusBadge> : null}
        </div>
        <div className="sticky-actions">
          <Button disabled={!canAdd} onClick={() => addToCart(product.id)} variant="secondary">
            加入购物车
          </Button>
          <Button disabled={!canAdd} onClick={handleBuyNow}>
            立即下单
          </Button>
        </div>
      </div>
    </section>
  );
}

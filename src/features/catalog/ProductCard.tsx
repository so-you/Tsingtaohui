import { Button } from "../../shared/components/Button";
import { StatusBadge } from "../../shared/components/StatusBadge";
import { formatCurrency, formatMeasure } from "../../shared/lib/format";
import type { Language, Product } from "../../shared/types/domain";

interface ProductCardProps {
  language: Language;
  onAddToCart: (productId: string) => void;
  onOpen: (productId: string) => void;
  product: Product;
}

export function ProductCard({ language, product, onAddToCart, onOpen }: ProductCardProps) {
  const name = language === "zh" ? product.nameZh : product.nameEn;
  const description = language === "zh" ? product.descriptionZh : product.descriptionEn;
  const canAdd = product.inventory.availableQty > 0 && product.droneDeliverable;

  return (
    <article className="product-card">
      <button className={`product-art product-art-${product.imageTone}`} type="button" onClick={() => onOpen(product.id)}>
        <span>{product.skuCode}</span>
      </button>
      <div className="product-card-body">
        <div className="product-card-heading">
          <button className="link-button product-title" type="button" onClick={() => onOpen(product.id)}>
            {name}
          </button>
          <strong>{formatCurrency(product.price)}</strong>
        </div>
        <p>{description}</p>
        <div className="product-meta">
          <span>{product.specification}</span>
          <span>{formatMeasure(product.weightKg, "kg")}</span>
          <span>{formatMeasure(product.volumeM3, "m3")}</span>
        </div>
        <div className="product-actions">
          <StatusBadge tone={product.inventory.availableQty > 0 ? "green" : "red"}>
            {product.inventory.availableQty > 0 ? `Stock ${product.inventory.availableQty}` : "Out of stock"}
          </StatusBadge>
          <StatusBadge tone={product.droneDeliverable ? "blue" : "amber"}>{product.droneDeliverable ? "Drone" : "Manual"}</StatusBadge>
          <Button disabled={!canAdd} onClick={() => onAddToCart(product.id)} variant={canAdd ? "primary" : "secondary"}>
            {canAdd ? "加入购物车" : product.inventory.availableQty === 0 ? "缺货" : "需确认"}
          </Button>
        </div>
      </div>
    </article>
  );
}

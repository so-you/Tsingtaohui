import type { CartItem, Product } from "../../shared/types/domain";

export interface CartTotals {
  totalPrice: number;
  totalWeightKg: number;
  totalVolumeM3: number;
}

export type CartValidationCode = "EMPTY_CART" | "MISSING_PRODUCT" | "OUT_OF_STOCK" | "NOT_DRONE_DELIVERABLE";

export interface CartValidationError {
  code: CartValidationCode;
  messageZh: string;
  messageEn: string;
  productId?: string;
}

const roundTo = (value: number, decimals: number): number => Number(value.toFixed(decimals));

export function calculateCartTotals(items: CartItem[], products: Product[]): CartTotals {
  const productById = new Map(products.map((product) => [product.id, product]));

  const totals = items.reduce(
    (current, item) => {
      const product = productById.get(item.productId);

      if (!product) {
        return current;
      }

      return {
        totalPrice: current.totalPrice + product.price * item.quantity,
        totalWeightKg: current.totalWeightKg + product.weightKg * item.quantity,
        totalVolumeM3: current.totalVolumeM3 + product.volumeM3 * item.quantity
      };
    },
    { totalPrice: 0, totalWeightKg: 0, totalVolumeM3: 0 }
  );

  return {
    totalPrice: roundTo(totals.totalPrice, 2),
    totalWeightKg: roundTo(totals.totalWeightKg, 2),
    totalVolumeM3: roundTo(totals.totalVolumeM3, 3)
  };
}

export function validateCartForSubmission(items: CartItem[], products: Product[]): CartValidationError[] {
  if (items.length === 0) {
    return [
      {
        code: "EMPTY_CART",
        messageZh: "购物车为空，请先添加商品。",
        messageEn: "Your cart is empty. Please add products before submitting."
      }
    ];
  }

  const productById = new Map(products.map((product) => [product.id, product]));
  const errors: CartValidationError[] = [];

  for (const item of items) {
    const product = productById.get(item.productId);

    if (!product) {
      errors.push({
        code: "MISSING_PRODUCT",
        productId: item.productId,
        messageZh: "商品不存在或已下架。",
        messageEn: "The product does not exist or has been removed."
      });
      continue;
    }

    if (product.inventory.availableQty < item.quantity) {
      errors.push({
        code: "OUT_OF_STOCK",
        productId: item.productId,
        messageZh: `${product.nameZh} 库存不足。`,
        messageEn: `${product.nameEn} does not have enough stock.`
      });
    }

    if (!product.droneDeliverable) {
      errors.push({
        code: "NOT_DRONE_DELIVERABLE",
        productId: item.productId,
        messageZh: `${product.nameZh} 暂不支持无人机配送。`,
        messageEn: `${product.nameEn} is not available for drone delivery.`
      });
    }
  }

  return errors;
}

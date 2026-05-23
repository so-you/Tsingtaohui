import { calculateCartTotals } from "../cart/cartLogic";
import type {
  CartItem,
  CustomsSyncStatus,
  DeliveryStatus,
  Order,
  OrderItem,
  OrderStatus,
  Product,
  ShipContext,
  TradeMode,
  WarehouseStatus
} from "../../shared/types/domain";

const AUTO_TRADE_MAX_WEIGHT_KG = 20;
const AUTO_TRADE_MAX_VOLUME_M3 = 0.08;

interface CreateOrderInput {
  cartItems: CartItem[];
  products: Product[];
  ship: ShipContext;
  consigneeName: string;
  cabinNo: string;
  now: string;
  contactInfo?: string;
  expectedDeliveryTime?: string;
  remark?: string;
}

interface OperationalStatuses {
  warehouseStatus: WarehouseStatus;
  deliveryStatus: DeliveryStatus;
  customsSyncStatus: CustomsSyncStatus;
}

const statusFlow: OrderStatus[] = [
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

const operationalStatusByOrderStatus: Record<OrderStatus, OperationalStatuses> = {
  PENDING_CONFIRM: {
    warehouseStatus: "NOT_STARTED",
    deliveryStatus: "NOT_CREATED",
    customsSyncStatus: "SYNC_SUCCESS"
  },
  CONFIRMED: {
    warehouseStatus: "NOT_STARTED",
    deliveryStatus: "NOT_CREATED",
    customsSyncStatus: "SYNC_SUCCESS"
  },
  WAREHOUSE_PROCESSING: {
    warehouseStatus: "PICKING",
    deliveryStatus: "NOT_CREATED",
    customsSyncStatus: "SYNC_SUCCESS"
  },
  PENDING_OUTBOUND: {
    warehouseStatus: "PACKED",
    deliveryStatus: "NOT_CREATED",
    customsSyncStatus: "SYNC_SUCCESS"
  },
  OUTBOUND: {
    warehouseStatus: "OUTBOUND_DONE",
    deliveryStatus: "NOT_CREATED",
    customsSyncStatus: "SYNC_SUCCESS"
  },
  PENDING_LOADING: {
    warehouseStatus: "OUTBOUND_DONE",
    deliveryStatus: "PENDING_LOADING",
    customsSyncStatus: "SYNC_SUCCESS"
  },
  IN_DELIVERY: {
    warehouseStatus: "OUTBOUND_DONE",
    deliveryStatus: "IN_DELIVERY",
    customsSyncStatus: "SYNC_SUCCESS"
  },
  PENDING_RECEIPT: {
    warehouseStatus: "OUTBOUND_DONE",
    deliveryStatus: "ARRIVED",
    customsSyncStatus: "SYNC_SUCCESS"
  },
  COMPLETED: {
    warehouseStatus: "OUTBOUND_DONE",
    deliveryStatus: "SIGNED",
    customsSyncStatus: "SYNC_SUCCESS"
  },
  CANCELLED: {
    warehouseStatus: "NOT_STARTED",
    deliveryStatus: "NOT_CREATED",
    customsSyncStatus: "SYNC_NONE"
  },
  EXCEPTION: {
    warehouseStatus: "NOT_STARTED",
    deliveryStatus: "FAILED",
    customsSyncStatus: "SYNC_FAILED"
  }
};

const roundTo = (value: number, decimals: number): number => Number(value.toFixed(decimals));

function getProductById(products: Product[]): Map<string, Product> {
  return new Map(products.map((product) => [product.id, product]));
}

function hasShipRoutingContext(ship: ShipContext): boolean {
  return Boolean(ship.berthOrAnchorage.trim() && ship.shippingAgentId.trim() && ship.shippingAgentName.trim());
}

function formatOrderDatePart(now: string): string {
  const dateMatch = /^(\d{4})-(\d{2})-(\d{2})/.exec(now);

  if (dateMatch) {
    return `${dateMatch[1]}${dateMatch[2]}${dateMatch[3]}`;
  }

  const parsedDate = new Date(now);

  if (Number.isNaN(parsedDate.getTime())) {
    return "00000000";
  }

  return parsedDate.toISOString().slice(0, 10).replace(/-/g, "");
}

function createDeterministicOrderSuffix(input: CreateOrderInput): string {
  const seed = JSON.stringify({
    now: input.now,
    shipId: input.ship.shipId,
    cabinNo: input.cabinNo,
    consigneeName: input.consigneeName,
    cartItems: input.cartItems
  });
  let hash = 0;

  for (const character of seed) {
    hash = (hash * 31 + character.charCodeAt(0)) % 10000;
  }

  return hash.toString().padStart(4, "0");
}

function createOrderItems(items: CartItem[], products: Product[]): OrderItem[] {
  const productById = getProductById(products);

  return items.map((item) => {
    const product = productById.get(item.productId);

    if (!product) {
      throw new Error(`Missing product: ${item.productId}`);
    }

    return {
      productId: product.id,
      skuCode: product.skuCode,
      nameZh: product.nameZh,
      nameEn: product.nameEn,
      quantity: item.quantity,
      unitPrice: product.price,
      unitWeightKg: product.weightKg,
      unitVolumeM3: product.volumeM3
    };
  });
}

export function decideTradeMode(items: CartItem[], products: Product[], ship: ShipContext): TradeMode {
  if (items.length === 0 || !hasShipRoutingContext(ship)) {
    return "MATCHING_ORDER";
  }

  const productById = getProductById(products);
  let totalVolumeM3 = 0;

  for (const item of items) {
    const product = productById.get(item.productId);

    if (!product || product.inventory.availableQty < item.quantity || !product.droneDeliverable) {
      return "MATCHING_ORDER";
    }

    if (roundTo(product.weightKg, 2) > AUTO_TRADE_MAX_WEIGHT_KG) {
      return "MATCHING_ORDER";
    }

    totalVolumeM3 += product.volumeM3 * item.quantity;
  }

  if (roundTo(totalVolumeM3, 3) > AUTO_TRADE_MAX_VOLUME_M3) {
    return "MATCHING_ORDER";
  }

  return "AUTO_TRADE";
}

export function createOrderFromCart(input: CreateOrderInput): Order {
  const items = createOrderItems(input.cartItems, input.products);
  const totals = calculateCartTotals(input.cartItems, input.products);
  const status: OrderStatus = "PENDING_CONFIRM";

  return {
    id: `ORD-${formatOrderDatePart(input.now)}-${createDeterministicOrderSuffix(input)}`,
    ship: { ...input.ship },
    items,
    totalPrice: totals.totalPrice,
    totalWeightKg: totals.totalWeightKg,
    totalVolumeM3: totals.totalVolumeM3,
    tradeMode: decideTradeMode(input.cartItems, input.products, input.ship),
    status,
    ...operationalStatusByOrderStatus[status],
    consigneeName: input.consigneeName,
    cabinNo: input.cabinNo,
    contactInfo: input.contactInfo,
    expectedDeliveryTime: input.expectedDeliveryTime,
    remark: input.remark,
    createdAt: input.now
  };
}

export function advanceOrderStatus(order: Order): Order {
  const currentIndex = statusFlow.indexOf(order.status);

  if (currentIndex === -1 || currentIndex === statusFlow.length - 1) {
    return { ...order };
  }

  const status = statusFlow[currentIndex + 1];

  return {
    ...order,
    status,
    ...operationalStatusByOrderStatus[status]
  };
}

export function confirmReceipt(order: Order, receiptMethod: NonNullable<Order["receiptMethod"]>, now: string): Order {
  if (order.status !== "PENDING_RECEIPT") {
    throw new Error("Order is not pending receipt");
  }

  return {
    ...order,
    status: "COMPLETED",
    ...operationalStatusByOrderStatus.COMPLETED,
    completedAt: now,
    receiptMethod
  };
}

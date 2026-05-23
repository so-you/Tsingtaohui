export type Language = "zh" | "en";

export type ShipLocationSource =
  | "QR_CODE"
  | "USER_BINDING"
  | "SHIPXY"
  | "MARINE_TRAFFIC"
  | "MANUAL_EDIT";

export type TradeMode = "AUTO_TRADE" | "MATCHING_ORDER";

export type OrderStatus =
  | "PENDING_CONFIRM"
  | "CONFIRMED"
  | "WAREHOUSE_PROCESSING"
  | "PENDING_OUTBOUND"
  | "OUTBOUND"
  | "PENDING_LOADING"
  | "IN_DELIVERY"
  | "PENDING_RECEIPT"
  | "COMPLETED"
  | "CANCELLED"
  | "EXCEPTION";

export type WarehouseStatus = "NOT_STARTED" | "PICKING" | "PACKED" | "OUTBOUND_READY" | "OUTBOUND_DONE";
export type DeliveryStatus = "NOT_CREATED" | "PENDING_LOADING" | "IN_DELIVERY" | "ARRIVED" | "SIGNED" | "FAILED";
export type CustomsSyncStatus = "SYNC_NONE" | "SYNCING" | "SYNC_SUCCESS" | "SYNC_FAILED" | "RETRYING" | "MANUAL_RESOLVED";

export interface Category {
  id: string;
  parentId?: string;
  nameZh: string;
  nameEn: string;
}

export interface InventorySnapshot {
  availableQty: number;
  lockedQty: number;
}

export interface Product {
  id: string;
  skuCode: string;
  categoryId: string;
  nameZh: string;
  nameEn: string;
  descriptionZh: string;
  descriptionEn: string;
  specification: string;
  price: number;
  weightKg: number;
  volumeM3: number;
  imageTone: "teal" | "blue" | "green" | "amber" | "gray";
  source: "BONDED_WAREHOUSE" | "PORT_SHOP";
  merchantId?: string;
  droneDeliverable: boolean;
  inventory: InventorySnapshot;
  restrictionNoteZh?: string;
  restrictionNoteEn?: string;
}

export interface ShippingAgent {
  id: string;
  nameZh: string;
  nameEn: string;
  contact: string;
}

export interface ShipContext {
  shipId: string;
  shipName: string;
  imo?: string;
  mmsi?: string;
  port: string;
  berthOrAnchorage: string;
  targetGps?: string;
  shippingAgentId: string;
  shippingAgentName: string;
  locationSource: ShipLocationSource;
  locationUpdatedAt: string;
  tokenExpiresAt: string;
  manuallyEditedAt?: string;
}

export interface CartItem {
  productId: string;
  quantity: number;
}

export interface OrderItem {
  productId: string;
  skuCode: string;
  nameZh: string;
  nameEn: string;
  quantity: number;
  unitPrice: number;
  unitWeightKg: number;
  unitVolumeM3: number;
}

export interface OrderTimelineItem {
  status: OrderStatus;
  labelZh: string;
  labelEn: string;
  reachedAt?: string;
}

export interface Order {
  id: string;
  ship: ShipContext;
  items: OrderItem[];
  totalPrice: number;
  totalWeightKg: number;
  totalVolumeM3: number;
  tradeMode: TradeMode;
  status: OrderStatus;
  warehouseStatus: WarehouseStatus;
  deliveryStatus: DeliveryStatus;
  customsSyncStatus: CustomsSyncStatus;
  consigneeName: string;
  cabinNo: string;
  contactInfo?: string;
  expectedDeliveryTime?: string;
  remark?: string;
  createdAt: string;
  estimatedArrival?: string;
  completedAt?: string;
  receiptMethod?: "CODE" | "PACKAGE_SCAN";
}

export interface ScanCode {
  code: string;
  kind: "product" | "order" | "package";
  targetId: string;
}

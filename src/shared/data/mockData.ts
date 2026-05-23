import type { Category, Order, Product, ScanCode, ShipContext, ShippingAgent } from "../types/domain";

export const shippingAgents: ShippingAgent[] = [
  { id: "agent-qdh-01", nameZh: "青岛远洋船代", nameEn: "Qingdao Ocean Agency", contact: "+86 532 8000 1001" },
  { id: "agent-qdh-02", nameZh: "汇港船务代理", nameEn: "Harbor Link Agency", contact: "+86 532 8000 1002" }
];

export const demoShipTokens: Partial<Record<string, ShipContext | null>> & {
  "demo-ship-token": ShipContext;
  "invalid-token": null;
} = {
  "demo-ship-token": {
    shipId: "ship-tsingtao-star",
    shipName: "TSINGTAO STAR",
    imo: "IMO9876543",
    mmsi: "413000888",
    port: "Qingdao Port",
    berthOrAnchorage: "Berth B12",
    targetGps: "36.0860,120.3200",
    shippingAgentId: "agent-qdh-01",
    shippingAgentName: "Qingdao Ocean Agency",
    locationSource: "QR_CODE",
    locationUpdatedAt: "2026-05-23T09:00:00+08:00",
    tokenExpiresAt: "2026-05-30T09:00:00+08:00"
  },
  "invalid-token": null
};

const createDemoShipSnapshot = (): ShipContext => ({ ...demoShipTokens["demo-ship-token"] });

export const categories: Category[] = [
  { id: "food", nameZh: "食品饮料", nameEn: "Food & Beverage" },
  { id: "daily", nameZh: "日用补给", nameEn: "Daily Supplies" },
  { id: "parts", nameZh: "维修备件", nameEn: "Spare Parts" },
  { id: "food-water", parentId: "food", nameZh: "饮用水", nameEn: "Drinking Water" },
  { id: "food-meal", parentId: "food", nameZh: "即食食品", nameEn: "Ready Meals" },
  { id: "daily-clean", parentId: "daily", nameZh: "清洁用品", nameEn: "Cleaning" }
];

export const products: Product[] = [
  {
    id: "prod-water",
    skuCode: "BW-WATER-24",
    categoryId: "food-water",
    nameZh: "保税饮用水 24 瓶",
    nameEn: "Bonded Drinking Water 24 Pack",
    descriptionZh: "适合船员日常补给的箱装饮用水。",
    descriptionEn: "Boxed drinking water for crew daily supply.",
    specification: "550ml x 24",
    price: 68,
    weightKg: 13.2,
    volumeM3: 0.032,
    imageTone: "teal",
    source: "BONDED_WAREHOUSE",
    droneDeliverable: true,
    inventory: { availableQty: 40, lockedQty: 4 }
  },
  {
    id: "prod-meal",
    skuCode: "BW-MEAL-12",
    categoryId: "food-meal",
    nameZh: "即食餐包 12 份",
    nameEn: "Ready Meal Set 12 Packs",
    descriptionZh: "常温储存，适合应急补给。",
    descriptionEn: "Shelf-stable meal packs for emergency supply.",
    specification: "12 packs",
    price: 156,
    weightKg: 6.8,
    volumeM3: 0.024,
    imageTone: "green",
    source: "BONDED_WAREHOUSE",
    droneDeliverable: true,
    inventory: { availableQty: 18, lockedQty: 2 }
  },
  {
    id: "prod-cleaner",
    skuCode: "BW-CLEAN-05",
    categoryId: "daily-clean",
    nameZh: "甲板清洁剂",
    nameEn: "Deck Cleaner",
    descriptionZh: "液体清洁剂，需人工确认配送方式。",
    descriptionEn: "Liquid cleaner requiring manual delivery confirmation.",
    specification: "5L",
    price: 92,
    weightKg: 5.5,
    volumeM3: 0.018,
    imageTone: "blue",
    source: "BONDED_WAREHOUSE",
    droneDeliverable: false,
    inventory: { availableQty: 16, lockedQty: 1 },
    restrictionNoteZh: "该商品需人工确认是否可无人机配送。",
    restrictionNoteEn: "This item requires manual confirmation before drone delivery."
  },
  {
    id: "prod-filter",
    skuCode: "BW-FILTER-01",
    categoryId: "parts",
    nameZh: "燃油滤芯",
    nameEn: "Fuel Filter",
    descriptionZh: "常用维修备件，当前缺货。",
    descriptionEn: "Common spare part, currently out of stock.",
    specification: "Standard",
    price: 320,
    weightKg: 2.1,
    volumeM3: 0.01,
    imageTone: "amber",
    source: "BONDED_WAREHOUSE",
    droneDeliverable: true,
    inventory: { availableQty: 0, lockedQty: 0 }
  }
];

export const initialOrders: Order[] = [
  {
    id: "ORD-20260523-0001",
    ship: createDemoShipSnapshot(),
    items: [
      {
        productId: "prod-water",
        skuCode: "BW-WATER-24",
        nameZh: "保税饮用水 24 瓶",
        nameEn: "Bonded Drinking Water 24 Pack",
        quantity: 1,
        unitPrice: 68,
        unitWeightKg: 13.2,
        unitVolumeM3: 0.032
      }
    ],
    totalPrice: 68,
    totalWeightKg: 13.2,
    totalVolumeM3: 0.032,
    tradeMode: "AUTO_TRADE",
    status: "IN_DELIVERY",
    warehouseStatus: "OUTBOUND_DONE",
    deliveryStatus: "IN_DELIVERY",
    customsSyncStatus: "SYNC_SUCCESS",
    consigneeName: "Alex Chen",
    cabinNo: "C-203",
    createdAt: "2026-05-23T08:45:00+08:00",
    estimatedArrival: "2026-05-23T10:15:00+08:00"
  },
  {
    id: "ORD-20260522-0007",
    ship: createDemoShipSnapshot(),
    items: [
      {
        productId: "prod-cleaner",
        skuCode: "BW-CLEAN-05",
        nameZh: "甲板清洁剂",
        nameEn: "Deck Cleaner",
        quantity: 1,
        unitPrice: 92,
        unitWeightKg: 5.5,
        unitVolumeM3: 0.018
      }
    ],
    totalPrice: 92,
    totalWeightKg: 5.5,
    totalVolumeM3: 0.018,
    tradeMode: "MATCHING_ORDER",
    status: "COMPLETED",
    warehouseStatus: "OUTBOUND_DONE",
    deliveryStatus: "SIGNED",
    customsSyncStatus: "SYNC_SUCCESS",
    consigneeName: "Demo User",
    cabinNo: "A-101",
    createdAt: "2026-05-22T14:10:00+08:00",
    completedAt: "2026-05-22T17:30:00+08:00",
    receiptMethod: "PACKAGE_SCAN"
  }
];

export const scanCodes: Record<"product" | "order" | "package", ScanCode> = {
  product: { code: "SCAN-PRODUCT-WATER", kind: "product", targetId: "prod-water" },
  order: { code: "SCAN-ORDER-ACTIVE", kind: "order", targetId: "ORD-20260523-0001" },
  package: { code: "SCAN-PACKAGE-READY", kind: "package", targetId: "ORD-20260523-0001" }
};

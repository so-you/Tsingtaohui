# H5 Client MVP Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Build a runnable Vite + React + TypeScript H5 client MVP for the bonded warehouse ship delivery ordering flow.

**Architecture:** Use a client-side single-page app with an operational bottom-tab shell, feature folders, typed domain models, mock async APIs, localStorage persistence, and testable business logic outside React components. The MVP does not call real backend, customs, ship-position, payment, or drone services.

**Tech Stack:** Vite, React, TypeScript, Vitest, Testing Library, lucide-react, CSS modules or plain CSS, localStorage.

---

## Scope Check

The approved spec covers one subsystem: the H5 client MVP. It can be implemented as one plan because the warehouse client, management console, real backend APIs, real QR camera integration, customs sync, and drone integration are explicitly out of scope.

## File Structure Map

Create or modify these files:

- Create or modify `.gitignore`: ignore frontend build artifacts, dependencies, coverage, `.superpowers/`, and `.worktrees/`.
- Create `package.json`: scripts and dependencies.
- Create `index.html`, `vite.config.ts`, `tsconfig.json`, `tsconfig.node.json`, `vitest.setup.ts`: build and test tooling.
- Create `src/main.tsx`, `src/index.css`, `src/vite-env.d.ts`: app entry and global H5 styles.
- Create `src/app/App.tsx`, `src/app/AppLayout.tsx`, `src/app/routes.ts`, `src/app/AppState.tsx`: app shell, route state, bottom tabs, global state.
- Create `src/shared/types/domain.ts`: all PRD-aligned domain types.
- Create `src/shared/data/mockData.ts`, `src/shared/api/mockApi.ts`: local fixtures and async mock service functions.
- Create `src/shared/lib/storage.ts`, `src/shared/lib/format.ts`: safe persistence and formatting helpers.
- Create `src/shared/i18n/messages.ts`, `src/shared/i18n/I18nProvider.tsx`: Chinese and English text.
- Create `src/shared/components/Button.tsx`, `StatusBadge.tsx`, `EmptyState.tsx`, `Field.tsx`, `QuantityStepper.tsx`: small reusable UI primitives.
- Create `src/features/ship/shipLogic.ts`, `ShipContextPanel.tsx`, `InvalidShipTokenPage.tsx`: ship token resolution and ship context display/editing.
- Create `src/features/catalog/catalogLogic.ts`, `CatalogPage.tsx`, `ProductDetailPage.tsx`, `ProductCard.tsx`: product browsing.
- Create `src/features/cart/cartLogic.ts`, `CartPage.tsx`, `OrderConfirmPage.tsx`: cart totals, validation, and order submission.
- Create `src/features/orders/orderLogic.ts`, `OrdersPage.tsx`, `OrderDetailPage.tsx`, `OrderTimeline.tsx`, `ReceiptConfirm.tsx`: order list/detail/status/receipt.
- Create `src/features/scan/ScanPage.tsx`: simulated scan actions.
- Create `src/features/profile/ProfilePage.tsx`: language, ship, bound ships, contacts.
- Create colocated `*.test.ts` and `*.test.tsx` files for behavior-bearing logic and user flows.
- Modify `README.md`: document app commands.

Do not modify untracked `AGENTS.md`, `CLAUDE.md`, or the untracked GLM product spec unless a later user request explicitly asks for that.

## Task 1: Scaffold Vite React Tooling

**Files:**
- Modify: `.gitignore`
- Create: `package.json`
- Create: `index.html`
- Create: `vite.config.ts`
- Create: `tsconfig.json`
- Create: `tsconfig.node.json`
- Create: `vitest.setup.ts`
- Create: `src/main.tsx`
- Create: `src/app/App.tsx`
- Create: `src/index.css`
- Create: `src/vite-env.d.ts`

- [ ] **Step 1: Create or update `.gitignore`**

```gitignore
node_modules/
dist/
coverage/
.superpowers/
.worktrees/
.DS_Store
*.log
```

- [ ] **Step 2: Create `package.json`**

```json
{
  "name": "tsingtaohui-h5-client",
  "version": "0.1.0",
  "private": true,
  "type": "module",
  "scripts": {
    "dev": "vite --host 127.0.0.1",
    "build": "tsc -b && vite build",
    "test": "vitest run --passWithNoTests",
    "test:watch": "vitest",
    "preview": "vite preview --host 127.0.0.1"
  },
  "dependencies": {
    "@vitejs/plugin-react": "^5.0.0",
    "lucide-react": "^0.468.0",
    "react": "^19.0.0",
    "react-dom": "^19.0.0",
    "vite": "^6.0.0"
  },
  "devDependencies": {
    "@testing-library/jest-dom": "^6.6.0",
    "@testing-library/react": "^16.0.0",
    "@testing-library/user-event": "^14.5.0",
    "@types/react": "^19.0.0",
    "@types/react-dom": "^19.0.0",
    "jsdom": "^25.0.0",
    "typescript": "^5.7.0",
    "vitest": "^2.1.0"
  }
}
```

- [ ] **Step 3: Create Vite and TypeScript config**

Create `vite.config.ts`:

```ts
import { defineConfig } from "vite";
import react from "@vitejs/plugin-react";

export default defineConfig({
  plugins: [react()],
  test: {
    environment: "jsdom",
    setupFiles: "./vitest.setup.ts",
    css: true
  }
});
```

Create `tsconfig.json`:

```json
{
  "compilerOptions": {
    "target": "ES2020",
    "useDefineForClassFields": true,
    "lib": ["DOM", "DOM.Iterable", "ES2020"],
    "allowJs": false,
    "skipLibCheck": true,
    "esModuleInterop": true,
    "allowSyntheticDefaultImports": true,
    "strict": true,
    "forceConsistentCasingInFileNames": true,
    "module": "ESNext",
    "moduleResolution": "Bundler",
    "resolveJsonModule": true,
    "isolatedModules": true,
    "noEmit": true,
    "jsx": "react-jsx"
  },
  "include": ["src", "vitest.setup.ts"],
  "references": [{ "path": "./tsconfig.node.json" }]
}
```

Create `tsconfig.node.json`:

```json
{
  "compilerOptions": {
    "composite": true,
    "skipLibCheck": true,
    "module": "ESNext",
    "moduleResolution": "Bundler",
    "allowSyntheticDefaultImports": true
  },
  "include": ["vite.config.ts"]
}
```

Create `vitest.setup.ts`:

```ts
import "@testing-library/jest-dom/vitest";
```

- [ ] **Step 4: Create minimal app entry**

Create `index.html`:

```html
<!doctype html>
<html lang="zh-CN">
  <head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>青岛汇 H5</title>
  </head>
  <body>
    <div id="root"></div>
    <script type="module" src="/src/main.tsx"></script>
  </body>
</html>
```

Create `src/vite-env.d.ts`:

```ts
/// <reference types="vite/client" />
```

Create `src/main.tsx`:

```tsx
import { StrictMode } from "react";
import { createRoot } from "react-dom/client";
import { App } from "./app/App";
import "./index.css";

createRoot(document.getElementById("root")!).render(
  <StrictMode>
    <App />
  </StrictMode>
);
```

Create `src/app/App.tsx`:

```tsx
export function App() {
  return (
    <main className="app-shell">
      <h1>青岛汇 H5</h1>
      <p>H5 client MVP scaffold</p>
    </main>
  );
}
```

Create `src/index.css`:

```css
:root {
  font-family: Inter, ui-sans-serif, system-ui, -apple-system, BlinkMacSystemFont, "Segoe UI", sans-serif;
  color: #172033;
  background: #eef3f7;
  font-synthesis: none;
  text-rendering: optimizeLegibility;
  -webkit-font-smoothing: antialiased;
  --color-primary: #0f766e;
  --color-primary-strong: #0b5f59;
  --color-blue: #2563eb;
  --color-green: #15803d;
  --color-amber: #b45309;
  --color-red: #b91c1c;
  --color-text: #172033;
  --color-muted: #667085;
  --color-border: #d8dee8;
  --color-surface: #ffffff;
  --shadow-soft: 0 12px 30px rgba(15, 23, 42, 0.09);
}

* {
  box-sizing: border-box;
}

body {
  margin: 0;
  min-width: 320px;
  min-height: 100vh;
}

button,
input,
textarea,
select {
  font: inherit;
}

.app-shell {
  max-width: 480px;
  min-height: 100vh;
  margin: 0 auto;
  background: #f8fafc;
  color: var(--color-text);
}
```

- [ ] **Step 5: Install dependencies**

Run: `npm install`

Expected: dependencies install and `package-lock.json` is created.

- [ ] **Step 6: Verify scaffold**

Run: `npm run test`

Expected: exit 0 with no test files found because `--passWithNoTests` is configured.

Run: `npm run build`

Expected: exit 0 and `dist/` is created.

- [ ] **Step 7: Commit scaffold**

```bash
git add .gitignore package.json package-lock.json index.html vite.config.ts tsconfig.json tsconfig.node.json vitest.setup.ts src/main.tsx src/app/App.tsx src/index.css src/vite-env.d.ts
git commit -m "Add H5 React app scaffold"
```

## Task 2: Add Domain Types And Mock Fixtures

**Files:**
- Create: `src/shared/types/domain.ts`
- Create: `src/shared/data/mockData.test.ts`
- Create: `src/shared/data/mockData.ts`

- [ ] **Step 1: Write failing mock data tests**

Create `src/shared/data/mockData.test.ts`:

```ts
import { describe, expect, it } from "vitest";
import { categories, demoShipTokens, products, scanCodes, shippingAgents } from "./mockData";

describe("mockData", () => {
  it("contains one valid demo token and one invalid demo token path", () => {
    expect(demoShipTokens["demo-ship-token"].shipName).toBe("TSINGTAO STAR");
    expect(demoShipTokens["invalid-token"]).toBeNull();
  });

  it("contains at least two category levels", () => {
    expect(categories.length).toBeGreaterThanOrEqual(2);
    expect(categories.some((category) => category.parentId)).toBe(true);
  });

  it("contains products with varied inventory and drone availability", () => {
    expect(products.some((product) => product.inventory.availableQty === 0)).toBe(true);
    expect(products.some((product) => !product.droneDeliverable)).toBe(true);
    expect(products.some((product) => product.droneDeliverable && product.inventory.availableQty > 0)).toBe(true);
  });

  it("contains shipping agents and scan codes for product, order, and package", () => {
    expect(shippingAgents).toHaveLength(2);
    expect(scanCodes.product.code).toBe("SCAN-PRODUCT-WATER");
    expect(scanCodes.order.code).toBe("SCAN-ORDER-ACTIVE");
    expect(scanCodes.package.code).toBe("SCAN-PACKAGE-READY");
  });
});
```

- [ ] **Step 2: Run test to verify it fails**

Run: `npm run test -- src/shared/data/mockData.test.ts`

Expected: FAIL with module resolution error for `./mockData`.

- [ ] **Step 3: Create domain types**

Create `src/shared/types/domain.ts`:

```ts
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
```

- [ ] **Step 4: Create mock data**

Create `src/shared/data/mockData.ts` with valid data matching the domain types:

```ts
import type { Category, Order, Product, ScanCode, ShipContext, ShippingAgent } from "../types/domain";

export const shippingAgents: ShippingAgent[] = [
  { id: "agent-qdh-01", nameZh: "青岛远洋船代", nameEn: "Qingdao Ocean Agency", contact: "+86 532 8000 1001" },
  { id: "agent-qdh-02", nameZh: "汇港船务代理", nameEn: "Harbor Link Agency", contact: "+86 532 8000 1002" }
];

export const demoShipTokens: Record<string, ShipContext | null> = {
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
    ship: demoShipTokens["demo-ship-token"]!,
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
    ship: demoShipTokens["demo-ship-token"]!,
    items: [],
    totalPrice: 0,
    totalWeightKg: 0,
    totalVolumeM3: 0,
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
```

- [ ] **Step 5: Run test to verify it passes**

Run: `npm run test -- src/shared/data/mockData.test.ts`

Expected: PASS.

- [ ] **Step 6: Commit domain and mock data**

```bash
git add src/shared/types/domain.ts src/shared/data/mockData.ts src/shared/data/mockData.test.ts
git commit -m "Add H5 domain types and mock data"
```

## Task 3: Add Business Logic With Test-First Coverage

**Files:**
- Create: `src/features/catalog/catalogLogic.test.ts`
- Create: `src/features/catalog/catalogLogic.ts`
- Create: `src/features/cart/cartLogic.test.ts`
- Create: `src/features/cart/cartLogic.ts`
- Create: `src/features/orders/orderLogic.test.ts`
- Create: `src/features/orders/orderLogic.ts`
- Create: `src/features/ship/shipLogic.test.ts`
- Create: `src/features/ship/shipLogic.ts`

- [ ] **Step 1: Write failing catalog logic tests**

Create `src/features/catalog/catalogLogic.test.ts`:

```ts
import { describe, expect, it } from "vitest";
import { products } from "../../shared/data/mockData";
import { filterProducts } from "./catalogLogic";

describe("filterProducts", () => {
  it("filters by child category", () => {
    const result = filterProducts(products, { categoryId: "food-water", keyword: "" });
    expect(result.map((product) => product.id)).toEqual(["prod-water"]);
  });

  it("searches Chinese and English product text", () => {
    expect(filterProducts(products, { keyword: "饮用水" }).map((product) => product.id)).toEqual(["prod-water"]);
    expect(filterProducts(products, { keyword: "meal" }).map((product) => product.id)).toEqual(["prod-meal"]);
  });

  it("returns all products when filters are empty", () => {
    expect(filterProducts(products, {}).length).toBe(products.length);
  });
});
```

- [ ] **Step 2: Run catalog test to verify it fails**

Run: `npm run test -- src/features/catalog/catalogLogic.test.ts`

Expected: FAIL with missing `catalogLogic` module.

- [ ] **Step 3: Implement catalog logic**

Create `src/features/catalog/catalogLogic.ts`:

```ts
import type { Product } from "../../shared/types/domain";

interface ProductFilter {
  categoryId?: string;
  keyword?: string;
}

export function filterProducts(products: Product[], filter: ProductFilter): Product[] {
  const keyword = filter.keyword?.trim().toLowerCase();

  return products.filter((product) => {
    const matchesCategory = !filter.categoryId || product.categoryId === filter.categoryId;
    const searchable = [
      product.nameZh,
      product.nameEn,
      product.descriptionZh,
      product.descriptionEn,
      product.skuCode,
      product.specification
    ]
      .join(" ")
      .toLowerCase();
    const matchesKeyword = !keyword || searchable.includes(keyword);
    return matchesCategory && matchesKeyword;
  });
}
```

- [ ] **Step 4: Write failing cart logic tests**

Create `src/features/cart/cartLogic.test.ts`:

```ts
import { describe, expect, it } from "vitest";
import { products } from "../../shared/data/mockData";
import type { CartItem } from "../../shared/types/domain";
import { calculateCartTotals, validateCartForSubmission } from "./cartLogic";

describe("cartLogic", () => {
  it("calculates price, weight, and volume totals", () => {
    const items: CartItem[] = [
      { productId: "prod-water", quantity: 2 },
      { productId: "prod-meal", quantity: 1 }
    ];

    expect(calculateCartTotals(items, products)).toEqual({
      totalPrice: 292,
      totalWeightKg: 33.2,
      totalVolumeM3: 0.088
    });
  });

  it("flags empty cart, stock, and drone delivery problems", () => {
    expect(validateCartForSubmission([], products).map((error) => error.code)).toContain("EMPTY_CART");

    const invalidItems: CartItem[] = [
      { productId: "prod-filter", quantity: 1 },
      { productId: "prod-cleaner", quantity: 1 }
    ];

    expect(validateCartForSubmission(invalidItems, products).map((error) => error.code)).toEqual([
      "OUT_OF_STOCK",
      "NOT_DRONE_DELIVERABLE"
    ]);
  });
});
```

- [ ] **Step 5: Run cart test to verify it fails**

Run: `npm run test -- src/features/cart/cartLogic.test.ts`

Expected: FAIL with missing `cartLogic` module.

- [ ] **Step 6: Implement cart logic**

Create `src/features/cart/cartLogic.ts`:

```ts
import type { CartItem, Product } from "../../shared/types/domain";

export interface CartTotals {
  totalPrice: number;
  totalWeightKg: number;
  totalVolumeM3: number;
}

export type CartValidationCode = "EMPTY_CART" | "MISSING_PRODUCT" | "OUT_OF_STOCK" | "NOT_DRONE_DELIVERABLE";

export interface CartValidationError {
  code: CartValidationCode;
  productId?: string;
  messageZh: string;
  messageEn: string;
}

export function calculateCartTotals(items: CartItem[], products: Product[]): CartTotals {
  const totals = items.reduce(
    (sum, item) => {
      const product = products.find((candidate) => candidate.id === item.productId);
      if (!product) return sum;

      sum.totalPrice += product.price * item.quantity;
      sum.totalWeightKg += product.weightKg * item.quantity;
      sum.totalVolumeM3 += product.volumeM3 * item.quantity;
      return sum;
    },
    { totalPrice: 0, totalWeightKg: 0, totalVolumeM3: 0 }
  );

  return {
    totalPrice: round(totals.totalPrice),
    totalWeightKg: round(totals.totalWeightKg),
    totalVolumeM3: round(totals.totalVolumeM3, 3)
  };
}

export function validateCartForSubmission(items: CartItem[], products: Product[]): CartValidationError[] {
  if (items.length === 0) {
    return [{ code: "EMPTY_CART", messageZh: "购物车为空", messageEn: "Cart is empty" }];
  }

  return items.flatMap((item) => {
    const product = products.find((candidate) => candidate.id === item.productId);
    if (!product) {
      return [{ code: "MISSING_PRODUCT" as const, productId: item.productId, messageZh: "商品不存在", messageEn: "Product not found" }];
    }
    if (product.inventory.availableQty < item.quantity) {
      return [{ code: "OUT_OF_STOCK" as const, productId: product.id, messageZh: `${product.nameZh} 库存不足`, messageEn: `${product.nameEn} is out of stock` }];
    }
    if (!product.droneDeliverable) {
      return [{ code: "NOT_DRONE_DELIVERABLE" as const, productId: product.id, messageZh: `${product.nameZh} 需人工确认配送`, messageEn: `${product.nameEn} requires manual delivery confirmation` }];
    }
    return [];
  });
}

function round(value: number, digits = 2): number {
  return Number(value.toFixed(digits));
}
```

- [ ] **Step 7: Write failing order logic tests**

Create `src/features/orders/orderLogic.test.ts`:

```ts
import { describe, expect, it } from "vitest";
import { demoShipTokens, products } from "../../shared/data/mockData";
import type { CartItem, Order } from "../../shared/types/domain";
import { advanceOrderStatus, confirmReceipt, createOrderFromCart, decideTradeMode } from "./orderLogic";

const ship = demoShipTokens["demo-ship-token"]!;

describe("orderLogic", () => {
  it("decides auto-trade when all automatic conditions pass", () => {
    const items: CartItem[] = [{ productId: "prod-water", quantity: 1 }];
    expect(decideTradeMode(items, products, ship)).toBe("AUTO_TRADE");
  });

  it("decides matching order when a product needs manual delivery confirmation", () => {
    const items: CartItem[] = [{ productId: "prod-cleaner", quantity: 1 }];
    expect(decideTradeMode(items, products, ship)).toBe("MATCHING_ORDER");
  });

  it("creates order items and totals from cart", () => {
    const order = createOrderFromCart({
      cartItems: [{ productId: "prod-water", quantity: 2 }],
      products,
      ship,
      consigneeName: "Alex Chen",
      cabinNo: "C-203",
      now: "2026-05-23T10:00:00+08:00"
    });

    expect(order.id).toMatch(/^ORD-20260523-/);
    expect(order.tradeMode).toBe("AUTO_TRADE");
    expect(order.totalPrice).toBe(136);
    expect(order.items[0].quantity).toBe(2);
  });

  it("advances order status through demo states", () => {
    const order = createOrderFromCart({
      cartItems: [{ productId: "prod-water", quantity: 1 }],
      products,
      ship,
      consigneeName: "Alex Chen",
      cabinNo: "C-203",
      now: "2026-05-23T10:00:00+08:00"
    });

    expect(advanceOrderStatus(order).status).toBe("CONFIRMED");
  });

  it("allows receipt confirmation only for pending receipt orders", () => {
    const base: Order = {
      ...createOrderFromCart({
        cartItems: [{ productId: "prod-water", quantity: 1 }],
        products,
        ship,
        consigneeName: "Alex Chen",
        cabinNo: "C-203",
        now: "2026-05-23T10:00:00+08:00"
      }),
      status: "PENDING_RECEIPT"
    };

    expect(confirmReceipt(base, "CODE", "2026-05-23T11:00:00+08:00").status).toBe("COMPLETED");
    expect(() => confirmReceipt({ ...base, status: "IN_DELIVERY" }, "CODE", "2026-05-23T11:00:00+08:00")).toThrow("Order is not pending receipt");
  });
});
```

- [ ] **Step 8: Run order test to verify it fails**

Run: `npm run test -- src/features/orders/orderLogic.test.ts`

Expected: FAIL with missing `orderLogic` module.

- [ ] **Step 9: Implement order logic**

Create `src/features/orders/orderLogic.ts`:

```ts
import { calculateCartTotals } from "../cart/cartLogic";
import type { CartItem, Order, OrderItem, OrderStatus, Product, ShipContext, TradeMode } from "../../shared/types/domain";

const MAX_MOCK_PAYLOAD_KG = 20;
const MAX_MOCK_VOLUME_M3 = 0.08;

const demoStatusFlow: OrderStatus[] = [
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

export function decideTradeMode(items: CartItem[], products: Product[], ship: ShipContext): TradeMode {
  const totals = calculateCartTotals(items, products);
  const hasOnlyAvailableDroneItems = items.every((item) => {
    const product = products.find((candidate) => candidate.id === item.productId);
    return Boolean(product && product.droneDeliverable && product.inventory.availableQty >= item.quantity);
  });
  const shipPositionConfirmed = Boolean(ship.berthOrAnchorage && ship.shippingAgentId);

  return hasOnlyAvailableDroneItems &&
    totals.totalWeightKg <= MAX_MOCK_PAYLOAD_KG &&
    totals.totalVolumeM3 <= MAX_MOCK_VOLUME_M3 &&
    shipPositionConfirmed
    ? "AUTO_TRADE"
    : "MATCHING_ORDER";
}

export function createOrderFromCart(input: {
  cartItems: CartItem[];
  products: Product[];
  ship: ShipContext;
  consigneeName: string;
  cabinNo: string;
  contactInfo?: string;
  expectedDeliveryTime?: string;
  remark?: string;
  now: string;
}): Order {
  const items: OrderItem[] = input.cartItems.map((cartItem) => {
    const product = input.products.find((candidate) => candidate.id === cartItem.productId);
    if (!product) throw new Error(`Missing product ${cartItem.productId}`);
    return {
      productId: product.id,
      skuCode: product.skuCode,
      nameZh: product.nameZh,
      nameEn: product.nameEn,
      quantity: cartItem.quantity,
      unitPrice: product.price,
      unitWeightKg: product.weightKg,
      unitVolumeM3: product.volumeM3
    };
  });
  const totals = calculateCartTotals(input.cartItems, input.products);

  return {
    id: `ORD-${input.now.slice(0, 10).replaceAll("-", "")}-${String(Date.now()).slice(-4)}`,
    ship: input.ship,
    items,
    totalPrice: totals.totalPrice,
    totalWeightKg: totals.totalWeightKg,
    totalVolumeM3: totals.totalVolumeM3,
    tradeMode: decideTradeMode(input.cartItems, input.products, input.ship),
    status: "PENDING_CONFIRM",
    warehouseStatus: "NOT_STARTED",
    deliveryStatus: "NOT_CREATED",
    customsSyncStatus: "SYNCING",
    consigneeName: input.consigneeName,
    cabinNo: input.cabinNo,
    contactInfo: input.contactInfo,
    expectedDeliveryTime: input.expectedDeliveryTime,
    remark: input.remark,
    createdAt: input.now
  };
}

export function advanceOrderStatus(order: Order): Order {
  const currentIndex = demoStatusFlow.indexOf(order.status);
  const nextStatus = demoStatusFlow[Math.min(currentIndex + 1, demoStatusFlow.length - 1)];
  return {
    ...order,
    status: nextStatus,
    warehouseStatus: nextStatus === "WAREHOUSE_PROCESSING" ? "PICKING" : nextStatus === "PENDING_OUTBOUND" ? "PACKED" : nextStatus === "OUTBOUND" ? "OUTBOUND_DONE" : order.warehouseStatus,
    deliveryStatus: nextStatus === "PENDING_LOADING" ? "PENDING_LOADING" : nextStatus === "IN_DELIVERY" ? "IN_DELIVERY" : nextStatus === "PENDING_RECEIPT" ? "ARRIVED" : nextStatus === "COMPLETED" ? "SIGNED" : order.deliveryStatus,
    customsSyncStatus: nextStatus === "PENDING_CONFIRM" ? "SYNCING" : "SYNC_SUCCESS"
  };
}

type ReceiptMethod = "CODE" | "PACKAGE_SCAN";

export function confirmReceipt(order: Order, receiptMethod: ReceiptMethod, now: string): Order {
  if (order.status !== "PENDING_RECEIPT") {
    throw new Error("Order is not pending receipt");
  }
  return {
    ...order,
    status: "COMPLETED",
    deliveryStatus: "SIGNED",
    customsSyncStatus: "SYNC_SUCCESS",
    receiptMethod,
    completedAt: now
  };
}
```

- [ ] **Step 10: Write failing ship logic tests**

Create `src/features/ship/shipLogic.test.ts`:

```ts
import { describe, expect, it } from "vitest";
import { resolveShipToken, updateShipLocation } from "./shipLogic";

describe("shipLogic", () => {
  it("resolves missing token to the demo ship", () => {
    expect(resolveShipToken(undefined).ship?.shipName).toBe("TSINGTAO STAR");
  });

  it("marks invalid token as invalid", () => {
    expect(resolveShipToken("invalid-token").status).toBe("invalid");
  });

  it("manual berth edit changes source and timestamp", () => {
    const ship = resolveShipToken("demo-ship-token").ship!;
    const edited = updateShipLocation(ship, "Anchorage A3", "2026-05-23T12:00:00+08:00");
    expect(edited.berthOrAnchorage).toBe("Anchorage A3");
    expect(edited.locationSource).toBe("MANUAL_EDIT");
    expect(edited.manuallyEditedAt).toBe("2026-05-23T12:00:00+08:00");
  });
});
```

- [ ] **Step 11: Run ship test to verify it fails**

Run: `npm run test -- src/features/ship/shipLogic.test.ts`

Expected: FAIL with missing `shipLogic` module.

- [ ] **Step 12: Implement ship logic**

Create `src/features/ship/shipLogic.ts`:

```ts
import { demoShipTokens } from "../../shared/data/mockData";
import type { ShipContext } from "../../shared/types/domain";

export type ShipTokenResolution =
  | { status: "valid"; ship: ShipContext }
  | { status: "invalid"; ship: null };

export function resolveShipToken(token: string | undefined): ShipTokenResolution {
  const resolved = demoShipTokens[token || "demo-ship-token"];
  if (!resolved) {
    return { status: "invalid", ship: null };
  }
  return { status: "valid", ship: resolved };
}

export function updateShipLocation(ship: ShipContext, berthOrAnchorage: string, now: string): ShipContext {
  return {
    ...ship,
    berthOrAnchorage,
    locationSource: "MANUAL_EDIT",
    locationUpdatedAt: now,
    manuallyEditedAt: now
  };
}
```

- [ ] **Step 13: Run logic tests to verify they pass**

Run: `npm run test -- src/features/catalog/catalogLogic.test.ts src/features/cart/cartLogic.test.ts src/features/orders/orderLogic.test.ts src/features/ship/shipLogic.test.ts`

Expected: PASS for all four test files.

- [ ] **Step 14: Commit business logic**

```bash
git add src/features/catalog src/features/cart src/features/orders src/features/ship
git commit -m "Add H5 client business logic"
```

## Task 4: Add Mock API, Persistence, And App State

**Files:**
- Create: `src/shared/lib/storage.test.ts`
- Create: `src/shared/lib/storage.ts`
- Create: `src/shared/api/mockApi.test.ts`
- Create: `src/shared/api/mockApi.ts`
- Create: `src/app/AppState.test.tsx`
- Create: `src/app/AppState.tsx`

- [ ] **Step 1: Write failing storage tests**

Create `src/shared/lib/storage.test.ts`:

```ts
import { beforeEach, describe, expect, it } from "vitest";
import { loadJson, saveJson } from "./storage";

describe("storage", () => {
  beforeEach(() => localStorage.clear());

  it("saves and loads JSON", () => {
    saveJson("demo", { value: 42 });
    expect(loadJson("demo", { value: 0 })).toEqual({ value: 42 });
  });

  it("returns fallback for missing or invalid JSON", () => {
    expect(loadJson("missing", ["fallback"])).toEqual(["fallback"]);
    localStorage.setItem("broken", "{");
    expect(loadJson("broken", { safe: true })).toEqual({ safe: true });
  });
});
```

- [ ] **Step 2: Run storage test to verify it fails**

Run: `npm run test -- src/shared/lib/storage.test.ts`

Expected: FAIL with missing `storage` module.

- [ ] **Step 3: Implement storage helpers**

Create `src/shared/lib/storage.ts`:

```ts
export function loadJson<T>(key: string, fallback: T): T {
  const raw = localStorage.getItem(key);
  if (!raw) return fallback;
  try {
    return JSON.parse(raw) as T;
  } catch {
    return fallback;
  }
}

export function saveJson<T>(key: string, value: T): void {
  localStorage.setItem(key, JSON.stringify(value));
}
```

- [ ] **Step 4: Write failing mock API tests**

Create `src/shared/api/mockApi.test.ts`:

```ts
import { describe, expect, it } from "vitest";
import { getProducts, getShipByToken, resolveScanCode } from "./mockApi";

describe("mockApi", () => {
  it("returns products asynchronously", async () => {
    await expect(getProducts()).resolves.toContainEqual(expect.objectContaining({ id: "prod-water" }));
  });

  it("returns demo ship for an omitted token", async () => {
    await expect(getShipByToken()).resolves.toMatchObject({ status: "valid", ship: { shipName: "TSINGTAO STAR" } });
  });

  it("resolves scan codes", async () => {
    await expect(resolveScanCode("SCAN-PRODUCT-WATER")).resolves.toMatchObject({ kind: "product", targetId: "prod-water" });
  });
});
```

- [ ] **Step 5: Run mock API test to verify it fails**

Run: `npm run test -- src/shared/api/mockApi.test.ts`

Expected: FAIL with missing `mockApi` module.

- [ ] **Step 6: Implement mock API**

Create `src/shared/api/mockApi.ts`:

```ts
import { categories, initialOrders, products, scanCodes, shippingAgents } from "../data/mockData";
import { resolveShipToken } from "../../features/ship/shipLogic";
import type { Order, ScanCode } from "../types/domain";

const delay = 10;

export async function getProducts() {
  return withDelay(products);
}

export async function getCategories() {
  return withDelay(categories);
}

export async function getShippingAgents() {
  return withDelay(shippingAgents);
}

export async function getInitialOrders(): Promise<Order[]> {
  return withDelay(initialOrders);
}

export async function getShipByToken(token?: string) {
  return withDelay(resolveShipToken(token));
}

export async function resolveScanCode(code: string): Promise<ScanCode | null> {
  const match = Object.values(scanCodes).find((candidate) => candidate.code === code) ?? null;
  return withDelay(match);
}

async function withDelay<T>(value: T): Promise<T> {
  await new Promise((resolve) => window.setTimeout(resolve, delay));
  return structuredClone(value);
}
```

- [ ] **Step 7: Write failing AppState test**

Create `src/app/AppState.test.tsx`:

```tsx
import { render, screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { describe, expect, it } from "vitest";
import { AppStateProvider, useAppState } from "./AppState";

function Probe() {
  const { cartItems, addToCart, language, setLanguage } = useAppState();
  return (
    <div>
      <div data-testid="language">{language}</div>
      <div data-testid="cart-count">{cartItems.length}</div>
      <button onClick={() => addToCart("prod-water")}>add</button>
      <button onClick={() => setLanguage("en")}>en</button>
    </div>
  );
}

describe("AppState", () => {
  it("stores language and cart items", async () => {
    render(
      <AppStateProvider>
        <Probe />
      </AppStateProvider>
    );

    await userEvent.click(screen.getByText("add"));
    await userEvent.click(screen.getByText("en"));

    expect(screen.getByTestId("cart-count")).toHaveTextContent("1");
    expect(screen.getByTestId("language")).toHaveTextContent("en");
  });
});
```

- [ ] **Step 8: Run AppState test to verify it fails**

Run: `npm run test -- src/app/AppState.test.tsx`

Expected: FAIL with missing `AppState` module.

- [ ] **Step 9: Implement AppState**

Create `src/app/AppState.tsx`:

```tsx
import { createContext, useContext, useMemo, useState, type ReactNode } from "react";
import { initialOrders } from "../shared/data/mockData";
import { loadJson, saveJson } from "../shared/lib/storage";
import type { CartItem, Language, Order, ShipContext } from "../shared/types/domain";

interface AppStateValue {
  language: Language;
  setLanguage: (language: Language) => void;
  currentShip: ShipContext | null;
  setCurrentShip: (ship: ShipContext | null) => void;
  cartItems: CartItem[];
  addToCart: (productId: string) => void;
  updateCartQuantity: (productId: string, quantity: number) => void;
  removeFromCart: (productId: string) => void;
  clearCart: () => void;
  orders: Order[];
  saveOrder: (order: Order) => void;
  updateOrder: (order: Order) => void;
}

const AppStateContext = createContext<AppStateValue | null>(null);

export function AppStateProvider({ children }: { children: ReactNode }) {
  const [language, setLanguageState] = useState<Language>(() => loadJson("tqh.language", "zh"));
  const [currentShip, setCurrentShipState] = useState<ShipContext | null>(() => loadJson("tqh.currentShip", null));
  const [cartItems, setCartItems] = useState<CartItem[]>(() => loadJson("tqh.cart", []));
  const [orders, setOrders] = useState<Order[]>(() => loadJson("tqh.orders", initialOrders));

  const value = useMemo<AppStateValue>(() => {
    function persistCart(next: CartItem[]) {
      setCartItems(next);
      saveJson("tqh.cart", next);
    }

    function persistOrders(next: Order[]) {
      setOrders(next);
      saveJson("tqh.orders", next);
    }

    return {
      language,
      setLanguage(next) {
        setLanguageState(next);
        saveJson("tqh.language", next);
      },
      currentShip,
      setCurrentShip(ship) {
        setCurrentShipState(ship);
        saveJson("tqh.currentShip", ship);
      },
      cartItems,
      addToCart(productId) {
        const existing = cartItems.find((item) => item.productId === productId);
        const next = existing
          ? cartItems.map((item) => (item.productId === productId ? { ...item, quantity: item.quantity + 1 } : item))
          : [...cartItems, { productId, quantity: 1 }];
        persistCart(next);
      },
      updateCartQuantity(productId, quantity) {
        persistCart(cartItems.map((item) => (item.productId === productId ? { ...item, quantity } : item)).filter((item) => item.quantity > 0));
      },
      removeFromCart(productId) {
        persistCart(cartItems.filter((item) => item.productId !== productId));
      },
      clearCart() {
        persistCart([]);
      },
      orders,
      saveOrder(order) {
        persistOrders([order, ...orders]);
      },
      updateOrder(order) {
        persistOrders(orders.map((candidate) => (candidate.id === order.id ? order : candidate)));
      }
    };
  }, [cartItems, currentShip, language, orders]);

  return <AppStateContext.Provider value={value}>{children}</AppStateContext.Provider>;
}

export function useAppState(): AppStateValue {
  const value = useContext(AppStateContext);
  if (!value) throw new Error("useAppState must be used inside AppStateProvider");
  return value;
}
```

- [ ] **Step 10: Run state tests to verify they pass**

Run: `npm run test -- src/shared/lib/storage.test.ts src/shared/api/mockApi.test.ts src/app/AppState.test.tsx`

Expected: PASS.

- [ ] **Step 11: Commit state and mock API**

```bash
git add src/shared/lib src/shared/api src/app/AppState.tsx src/app/AppState.test.tsx
git commit -m "Add H5 mock API and app state"
```

## Task 5: Add Internationalization And Formatting Helpers

**Files:**
- Create: `src/shared/i18n/messages.test.ts`
- Create: `src/shared/i18n/messages.ts`
- Create: `src/shared/i18n/I18nProvider.tsx`
- Create: `src/shared/lib/format.test.ts`
- Create: `src/shared/lib/format.ts`

- [ ] **Step 1: Write failing i18n tests**

Create `src/shared/i18n/messages.test.ts`:

```ts
import { describe, expect, it } from "vitest";
import { getMessage, messages } from "./messages";

describe("messages", () => {
  it("has Chinese and English labels for bottom tabs", () => {
    expect(getMessage("zh", "tabs.goods")).toBe("商品");
    expect(getMessage("en", "tabs.goods")).toBe("Goods");
    expect(messages.zh["tabs.orders"]).toBe("订单");
    expect(messages.en["tabs.orders"]).toBe("Orders");
  });
});
```

- [ ] **Step 2: Run i18n test to verify it fails**

Run: `npm run test -- src/shared/i18n/messages.test.ts`

Expected: FAIL with missing `messages` module.

- [ ] **Step 3: Implement messages and provider**

Create `src/shared/i18n/messages.ts`:

```ts
import type { Language } from "../types/domain";

export const messages = {
  zh: {
    "tabs.home": "首页",
    "tabs.goods": "商品",
    "tabs.orders": "订单",
    "tabs.scan": "扫码",
    "tabs.mine": "我的",
    "common.confirm": "确认",
    "common.cancel": "取消",
    "common.save": "保存",
    "common.refresh": "刷新状态",
    "home.activeOrders": "进行中订单",
    "home.availableSkus": "可选商品",
    "catalog.search": "搜索商品",
    "catalog.addToCart": "加入购物车",
    "catalog.buyNow": "立即下单",
    "cart.title": "订单确认",
    "cart.submit": "提交订单",
    "orders.title": "订单",
    "orders.detail": "订单详情",
    "orders.confirmReceipt": "确认收货",
    "scan.title": "扫码模拟",
    "profile.language": "语言"
  },
  en: {
    "tabs.home": "Home",
    "tabs.goods": "Goods",
    "tabs.orders": "Orders",
    "tabs.scan": "Scan",
    "tabs.mine": "Mine",
    "common.confirm": "Confirm",
    "common.cancel": "Cancel",
    "common.save": "Save",
    "common.refresh": "Refresh Status",
    "home.activeOrders": "Active Orders",
    "home.availableSkus": "Available SKUs",
    "catalog.search": "Search goods",
    "catalog.addToCart": "Add to cart",
    "catalog.buyNow": "Buy now",
    "cart.title": "Order Confirmation",
    "cart.submit": "Submit Order",
    "orders.title": "Orders",
    "orders.detail": "Order Detail",
    "orders.confirmReceipt": "Confirm Receipt",
    "scan.title": "Scan Demo",
    "profile.language": "Language"
  }
} as const;

export type MessageKey = keyof typeof messages.zh;

export function getMessage(language: Language, key: MessageKey): string {
  return messages[language][key];
}
```

Create `src/shared/i18n/I18nProvider.tsx`:

```tsx
import { createContext, useContext, type ReactNode } from "react";
import { useAppState } from "../../app/AppState";
import { getMessage, type MessageKey } from "./messages";

const I18nContext = createContext<{ t: (key: MessageKey) => string } | null>(null);

export function I18nProvider({ children }: { children: ReactNode }) {
  const { language } = useAppState();
  return <I18nContext.Provider value={{ t: (key) => getMessage(language, key) }}>{children}</I18nContext.Provider>;
}

export function useI18n() {
  const value = useContext(I18nContext);
  if (!value) throw new Error("useI18n must be used inside I18nProvider");
  return value;
}
```

- [ ] **Step 4: Write failing formatting tests**

Create `src/shared/lib/format.test.ts`:

```ts
import { describe, expect, it } from "vitest";
import { formatCurrency, formatDateTime, formatMeasure } from "./format";

describe("format", () => {
  it("formats currency and measures", () => {
    expect(formatCurrency(68)).toBe("¥68.00");
    expect(formatMeasure(13.2, "kg")).toBe("13.2 kg");
  });

  it("formats date time safely", () => {
    expect(formatDateTime("2026-05-23T10:00:00+08:00")).toContain("2026");
  });
});
```

- [ ] **Step 5: Run formatting test to verify it fails**

Run: `npm run test -- src/shared/lib/format.test.ts`

Expected: FAIL with missing `format` module.

- [ ] **Step 6: Implement formatting helpers**

Create `src/shared/lib/format.ts`:

```ts
export function formatCurrency(value: number): string {
  return `¥${value.toFixed(2)}`;
}

export function formatMeasure(value: number, unit: string): string {
  return `${Number(value.toFixed(3))} ${unit}`;
}

export function formatDateTime(value?: string): string {
  if (!value) return "-";
  return new Intl.DateTimeFormat("zh-CN", {
    year: "numeric",
    month: "2-digit",
    day: "2-digit",
    hour: "2-digit",
    minute: "2-digit"
  }).format(new Date(value));
}
```

- [ ] **Step 7: Run helper tests to verify they pass**

Run: `npm run test -- src/shared/i18n/messages.test.ts src/shared/lib/format.test.ts`

Expected: PASS.

- [ ] **Step 8: Commit i18n and formatting helpers**

```bash
git add src/shared/i18n src/shared/lib/format.ts src/shared/lib/format.test.ts
git commit -m "Add H5 i18n and formatting helpers"
```

## Task 6: Add App Shell, Bottom Tabs, And Shared UI Components

**Files:**
- Create: `src/shared/components/Button.tsx`
- Create: `src/shared/components/StatusBadge.tsx`
- Create: `src/shared/components/EmptyState.tsx`
- Create: `src/shared/components/Field.tsx`
- Create: `src/shared/components/QuantityStepper.tsx`
- Create: `src/app/routes.ts`
- Create: `src/app/AppLayout.test.tsx`
- Create: `src/app/AppLayout.tsx`
- Modify: `src/app/App.tsx`
- Modify: `src/index.css`

- [ ] **Step 1: Write failing app layout test**

Create `src/app/AppLayout.test.tsx`:

```tsx
import { render, screen } from "@testing-library/react";
import { describe, expect, it } from "vitest";
import { AppStateProvider } from "./AppState";
import { AppLayout } from "./AppLayout";
import { I18nProvider } from "../shared/i18n/I18nProvider";

describe("AppLayout", () => {
  it("renders operational bottom tabs", () => {
    render(
      <AppStateProvider>
        <I18nProvider>
          <AppLayout activeRoute="goods" onNavigate={() => null}>
            <div>content</div>
          </AppLayout>
        </I18nProvider>
      </AppStateProvider>
    );

    expect(screen.getByText("商品")).toBeInTheDocument();
    expect(screen.getByText("订单")).toBeInTheDocument();
    expect(screen.getByText("扫码")).toBeInTheDocument();
    expect(screen.getByText("我的")).toBeInTheDocument();
  });
});
```

- [ ] **Step 2: Run app layout test to verify it fails**

Run: `npm run test -- src/app/AppLayout.test.tsx`

Expected: FAIL with missing `AppLayout` module.

- [ ] **Step 3: Create shared components**

Create `src/shared/components/Button.tsx`:

```tsx
import type { ButtonHTMLAttributes, ReactNode } from "react";

interface ButtonProps extends ButtonHTMLAttributes<HTMLButtonElement> {
  variant?: "primary" | "secondary" | "ghost" | "danger";
  children: ReactNode;
}

export function Button({ variant = "primary", className = "", children, ...props }: ButtonProps) {
  return (
    <button className={`button button-${variant} ${className}`.trim()} {...props}>
      {children}
    </button>
  );
}
```

Create `src/shared/components/StatusBadge.tsx`:

```tsx
import type { ReactNode } from "react";

export function StatusBadge({ tone, children }: { tone: "blue" | "green" | "amber" | "red" | "gray"; children: ReactNode }) {
  return <span className={`status-badge status-${tone}`}>{children}</span>;
}
```

Create `src/shared/components/EmptyState.tsx`:

```tsx
export function EmptyState({ title, detail }: { title: string; detail?: string }) {
  return (
    <div className="empty-state">
      <strong>{title}</strong>
      {detail ? <p>{detail}</p> : null}
    </div>
  );
}
```

Create `src/shared/components/Field.tsx`:

```tsx
import type { InputHTMLAttributes, ReactNode } from "react";

interface FieldProps extends InputHTMLAttributes<HTMLInputElement> {
  label: string;
  error?: string;
  action?: ReactNode;
}

export function Field({ label, error, action, ...props }: FieldProps) {
  return (
    <label className="field">
      <span className="field-label">
        {label}
        {action}
      </span>
      <input className="field-input" {...props} />
      {error ? <span className="field-error">{error}</span> : null}
    </label>
  );
}
```

Create `src/shared/components/QuantityStepper.tsx`:

```tsx
import { Minus, Plus } from "lucide-react";

export function QuantityStepper({ value, onChange }: { value: number; onChange: (value: number) => void }) {
  return (
    <div className="quantity-stepper">
      <button type="button" aria-label="Decrease quantity" onClick={() => onChange(Math.max(0, value - 1))}>
        <Minus size={14} />
      </button>
      <span>{value}</span>
      <button type="button" aria-label="Increase quantity" onClick={() => onChange(value + 1)}>
        <Plus size={14} />
      </button>
    </div>
  );
}
```

- [ ] **Step 4: Create routes and app layout**

Create `src/app/routes.ts`:

```ts
export type AppRoute =
  | "home"
  | "goods"
  | "productDetail"
  | "cart"
  | "orders"
  | "orderDetail"
  | "scan"
  | "mine"
  | "invalidShip";
```

Create `src/app/AppLayout.tsx`:

```tsx
import { ClipboardList, Home, PackageSearch, QrCode, UserRound } from "lucide-react";
import type { ReactNode } from "react";
import { useI18n } from "../shared/i18n/I18nProvider";
import type { AppRoute } from "./routes";

interface AppLayoutProps {
  activeRoute: AppRoute;
  onNavigate: (route: AppRoute) => void;
  children: ReactNode;
}

const tabs: Array<{ route: AppRoute; labelKey: "tabs.home" | "tabs.goods" | "tabs.orders" | "tabs.scan" | "tabs.mine"; icon: typeof Home }> = [
  { route: "home", labelKey: "tabs.home", icon: Home },
  { route: "goods", labelKey: "tabs.goods", icon: PackageSearch },
  { route: "orders", labelKey: "tabs.orders", icon: ClipboardList },
  { route: "scan", labelKey: "tabs.scan", icon: QrCode },
  { route: "mine", labelKey: "tabs.mine", icon: UserRound }
];

export function AppLayout({ activeRoute, onNavigate, children }: AppLayoutProps) {
  const { t } = useI18n();
  return (
    <div className="app-shell">
      <div className="app-content">{children}</div>
      <nav className="bottom-tabs" aria-label="Primary">
        {tabs.map((tab) => {
          const Icon = tab.icon;
          const isActive = activeRoute === tab.route;
          return (
            <button key={tab.route} type="button" className={isActive ? "tab tab-active" : "tab"} onClick={() => onNavigate(tab.route)}>
              <Icon size={20} />
              <span>{t(tab.labelKey)}</span>
            </button>
          );
        })}
      </nav>
    </div>
  );
}
```

- [ ] **Step 5: Modify `src/app/App.tsx` to use providers**

```tsx
import { useState } from "react";
import { I18nProvider } from "../shared/i18n/I18nProvider";
import { AppLayout } from "./AppLayout";
import { AppStateProvider } from "./AppState";
import type { AppRoute } from "./routes";

function AppInner() {
  const [route, setRoute] = useState<AppRoute>("home");
  return (
    <AppLayout activeRoute={route} onNavigate={setRoute}>
      <section className="page">
        <h1>青岛汇 H5</h1>
        <p>Operational client shell</p>
      </section>
    </AppLayout>
  );
}

export function App() {
  return (
    <AppStateProvider>
      <I18nProvider>
        <AppInner />
      </I18nProvider>
    </AppStateProvider>
  );
}
```

- [ ] **Step 6: Extend `src/index.css` with layout and component styles**

Append CSS classes for `.app-content`, `.bottom-tabs`, `.tab`, `.tab-active`, `.button`, `.status-badge`, `.empty-state`, `.field`, `.field-input`, `.field-error`, `.quantity-stepper`, and `.page`. Keep buttons at stable heights and prevent label overflow with `min-width: 0`, `white-space: nowrap`, and `text-overflow: ellipsis`.

- [ ] **Step 7: Run layout test and build**

Run: `npm run test -- src/app/AppLayout.test.tsx`

Expected: PASS.

Run: `npm run build`

Expected: PASS.

- [ ] **Step 8: Commit app shell**

```bash
git add src/app src/shared/components src/index.css
git commit -m "Add H5 operational app shell"
```

## Task 7: Implement Home, Ship Context, And Product Catalog UI

**Files:**
- Create: `src/features/ship/ShipContextPanel.tsx`
- Create: `src/features/ship/InvalidShipTokenPage.tsx`
- Create: `src/features/catalog/ProductCard.tsx`
- Create: `src/features/catalog/CatalogPage.test.tsx`
- Create: `src/features/catalog/CatalogPage.tsx`
- Create: `src/features/catalog/ProductDetailPage.tsx`
- Create: `src/features/home/HomePage.tsx`
- Modify: `src/app/App.tsx`
- Modify: `src/index.css`

- [ ] **Step 1: Write failing catalog UI test**

Create `src/features/catalog/CatalogPage.test.tsx`:

```tsx
import { render, screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { describe, expect, it } from "vitest";
import { AppStateProvider } from "../../app/AppState";
import { I18nProvider } from "../../shared/i18n/I18nProvider";
import { CatalogPage } from "./CatalogPage";

describe("CatalogPage", () => {
  it("searches products and adds a product to cart", async () => {
    render(
      <AppStateProvider>
        <I18nProvider>
          <CatalogPage onOpenProduct={() => null} onOpenCart={() => null} />
        </I18nProvider>
      </AppStateProvider>
    );

    await userEvent.type(screen.getByPlaceholderText("搜索商品"), "饮用水");
    expect(screen.getByText("保税饮用水 24 瓶")).toBeInTheDocument();
    expect(screen.queryByText("即食餐包 12 份")).not.toBeInTheDocument();

    await userEvent.click(screen.getByText("加入购物车"));
    expect(screen.getByText("购物车 1")).toBeInTheDocument();
  });
});
```

- [ ] **Step 2: Run catalog UI test to verify it fails**

Run: `npm run test -- src/features/catalog/CatalogPage.test.tsx`

Expected: FAIL with missing `CatalogPage` module.

- [ ] **Step 3: Implement product card and catalog page**

Create `ProductCard.tsx` and `CatalogPage.tsx` so that:

```tsx
// CatalogPage public props
export interface CatalogPageProps {
  onOpenProduct: (productId: string) => void;
  onOpenCart: () => void;
}
```

The page must render:

- Search input showing `搜索商品` before the user types.
- Category buttons from `categories`.
- Product cards from filtered products.
- `加入购物车` buttons for available drone-deliverable products.
- Disabled or secondary action messaging for out-of-stock and non-drone-deliverable products.
- A cart entry button showing `购物车 ${cartItems.length}`.

Use `filterProducts`, `useAppState`, `products`, `categories`, and `Button`.

- [ ] **Step 4: Implement ship context and home page**

Create `ShipContextPanel.tsx` to show ship name, IMO/MMSI, berth or anchorage, location source, shipping agent, and a compact edit action.

Create `InvalidShipTokenPage.tsx` with a concise recovery message and a button that returns to demo mode.

Create `HomePage.tsx` to show:

- `ShipContextPanel`.
- Active order count from app state.
- Available SKU count from `products`.
- Entry buttons for goods, orders, scan, and mine.
- A short active-order card when an active order exists.

- [ ] **Step 5: Implement product detail page**

Create `ProductDetailPage.tsx` with props:

```tsx
export interface ProductDetailPageProps {
  productId: string;
  onBack: () => void;
  onOpenCart: () => void;
}
```

Render bilingual product name according to current language, image tone panel, description, specification, price, weight, volume, inventory, restriction note, add-to-cart, and buy-now actions. Buy-now adds the item and opens cart.

- [ ] **Step 6: Wire home and catalog routes in `App.tsx`**

Replace the temporary page body with route selection. Track `selectedProductId` in `AppInner`. Render `HomePage`, `CatalogPage`, or `ProductDetailPage` for `home`, `goods`, and `productDetail` routes. Keep the existing bottom tab route behavior.

- [ ] **Step 7: Add CSS for product and home views**

Add responsive styles for `.ship-panel`, `.metric-grid`, `.entry-grid`, `.product-list`, `.product-card`, `.product-art`, `.category-row`, and `.search-input`. At 375px width product cards must not force horizontal scrolling.

- [ ] **Step 8: Run tests and build**

Run: `npm run test -- src/features/catalog/CatalogPage.test.tsx`

Expected: PASS.

Run: `npm run build`

Expected: PASS.

- [ ] **Step 9: Commit home and catalog UI**

```bash
git add src/features/home src/features/ship src/features/catalog src/app/App.tsx src/index.css
git commit -m "Add H5 home and catalog views"
```

## Task 8: Implement Cart And Order Confirmation Flow

**Files:**
- Create: `src/features/cart/OrderConfirmPage.test.tsx`
- Create: `src/features/cart/CartPage.tsx`
- Create: `src/features/cart/OrderConfirmPage.tsx`
- Modify: `src/app/App.tsx`
- Modify: `src/index.css`

- [ ] **Step 1: Write failing order confirmation test**

Create `src/features/cart/OrderConfirmPage.test.tsx`:

```tsx
import { render, screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { describe, expect, it } from "vitest";
import { AppStateProvider, useAppState } from "../../app/AppState";
import { I18nProvider } from "../../shared/i18n/I18nProvider";
import { demoShipTokens } from "../../shared/data/mockData";
import { OrderConfirmPage } from "./OrderConfirmPage";

function Harness() {
  const { addToCart, setCurrentShip } = useAppState();
  return (
    <>
      <button onClick={() => { setCurrentShip(demoShipTokens["demo-ship-token"]!); addToCart("prod-water"); }}>seed</button>
      <OrderConfirmPage onOrderCreated={() => null} />
    </>
  );
}

describe("OrderConfirmPage", () => {
  it("requires consignee and cabin before order submission", async () => {
    render(
      <AppStateProvider>
        <I18nProvider>
          <Harness />
        </I18nProvider>
      </AppStateProvider>
    );

    await userEvent.click(screen.getByText("seed"));
    await userEvent.click(screen.getByText("提交订单"));
    expect(screen.getByText("请填写收货人")).toBeInTheDocument();
    expect(screen.getByText("请填写 Cabin No.")).toBeInTheDocument();
  });

  it("submits an order after berth edit and required fields", async () => {
    render(
      <AppStateProvider>
        <I18nProvider>
          <Harness />
        </I18nProvider>
      </AppStateProvider>
    );

    await userEvent.click(screen.getByText("seed"));
    await userEvent.clear(screen.getByLabelText("泊位 / 锚地"));
    await userEvent.type(screen.getByLabelText("泊位 / 锚地"), "Anchorage A3");
    await userEvent.type(screen.getByLabelText("收货人"), "Alex Chen");
    await userEvent.type(screen.getByLabelText("Cabin No."), "C-203");
    await userEvent.click(screen.getByText("提交订单"));

    expect(screen.getByText("订单已创建")).toBeInTheDocument();
  });
});
```

- [ ] **Step 2: Run order confirmation test to verify it fails**

Run: `npm run test -- src/features/cart/OrderConfirmPage.test.tsx`

Expected: FAIL with missing `OrderConfirmPage` module.

- [ ] **Step 3: Implement cart page**

Create `CartPage.tsx` to render current cart items, `QuantityStepper`, remove action, total price, weight, volume, validation messages, and a primary action to continue to order confirmation.

- [ ] **Step 4: Implement order confirmation page**

Create `OrderConfirmPage.tsx` with props:

```tsx
export interface OrderConfirmPageProps {
  onOrderCreated: (orderId: string) => void;
}
```

Use local component state for consignee, Cabin No., contact, expected delivery time, remark, and editable berth or anchorage. On submit:

1. Validate non-empty cart, current ship, consignee, and Cabin No.
2. Apply `updateShipLocation` when berth or anchorage differs from current ship.
3. Create order with `createOrderFromCart`.
4. Save order and clear cart through AppState.
5. Render `订单已创建` for test visibility.
6. Call `onOrderCreated(order.id)`.

- [ ] **Step 5: Wire cart routes in `App.tsx`**

Add route rendering for `cart`. From catalog, product detail, and home entry cards, navigate to `cart`. After order creation set selected order ID and navigate to `orderDetail`.

- [ ] **Step 6: Add CSS for cart and forms**

Add `.cart-list`, `.cart-line`, `.summary-panel`, `.form-panel`, `.validation-list`, and `.success-banner`. Keep sticky bottom actions above the bottom tabs with enough padding.

- [ ] **Step 7: Run tests and build**

Run: `npm run test -- src/features/cart/cartLogic.test.ts src/features/cart/OrderConfirmPage.test.tsx`

Expected: PASS.

Run: `npm run build`

Expected: PASS.

- [ ] **Step 8: Commit cart flow**

```bash
git add src/features/cart src/app/App.tsx src/index.css
git commit -m "Add H5 cart and order confirmation"
```

## Task 9: Implement Orders, Status Tracking, Receipt, Scan, And Profile

**Files:**
- Create: `src/features/orders/OrderDetailPage.test.tsx`
- Create: `src/features/orders/OrdersPage.tsx`
- Create: `src/features/orders/OrderTimeline.tsx`
- Create: `src/features/orders/ReceiptConfirm.tsx`
- Create: `src/features/orders/OrderDetailPage.tsx`
- Create: `src/features/scan/ScanPage.test.tsx`
- Create: `src/features/scan/ScanPage.tsx`
- Create: `src/features/profile/ProfilePage.test.tsx`
- Create: `src/features/profile/ProfilePage.tsx`
- Modify: `src/app/App.tsx`
- Modify: `src/index.css`

- [ ] **Step 1: Write failing order detail test**

Create `src/features/orders/OrderDetailPage.test.tsx`:

```tsx
import { render, screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { describe, expect, it } from "vitest";
import { AppStateProvider } from "../../app/AppState";
import { I18nProvider } from "../../shared/i18n/I18nProvider";
import { initialOrders } from "../../shared/data/mockData";
import { OrderDetailPage } from "./OrderDetailPage";

describe("OrderDetailPage", () => {
  it("shows order status and advances demo status", async () => {
    render(
      <AppStateProvider>
        <I18nProvider>
          <OrderDetailPage orderId={initialOrders[0].id} onBack={() => null} />
        </I18nProvider>
      </AppStateProvider>
    );

    expect(screen.getByText("ORD-20260523-0001")).toBeInTheDocument();
    await userEvent.click(screen.getByText("刷新状态"));
    expect(screen.getByText(/待签收|已完成|配送中/)).toBeInTheDocument();
  });
});
```

- [ ] **Step 2: Run order detail test to verify it fails**

Run: `npm run test -- src/features/orders/OrderDetailPage.test.tsx`

Expected: FAIL with missing `OrderDetailPage` module.

- [ ] **Step 3: Implement order list and order detail**

Create:

- `OrdersPage.tsx`: status filter chips, active/history list, empty state, click opens detail.
- `OrderTimeline.tsx`: timeline from statuses with reached/current/pending styling.
- `ReceiptConfirm.tsx`: verification code input and confirm button enabled only for `PENDING_RECEIPT`.
- `OrderDetailPage.tsx`: order header, item list, ship and consignee info, timeline, warehouse/delivery/customs status badges, refresh status action using `advanceOrderStatus`, and receipt confirmation using `confirmReceipt`.

- [ ] **Step 4: Write failing scan test**

Create `src/features/scan/ScanPage.test.tsx`:

```tsx
import { render, screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { describe, expect, it } from "vitest";
import { AppStateProvider } from "../../app/AppState";
import { I18nProvider } from "../../shared/i18n/I18nProvider";
import { ScanPage } from "./ScanPage";

describe("ScanPage", () => {
  it("resolves demo product scan code", async () => {
    render(
      <AppStateProvider>
        <I18nProvider>
          <ScanPage onOpenProduct={() => null} onOpenOrder={() => null} />
        </I18nProvider>
      </AppStateProvider>
    );

    await userEvent.click(screen.getByText("商品码"));
    expect(await screen.findByText("SCAN-PRODUCT-WATER")).toBeInTheDocument();
  });
});
```

- [ ] **Step 5: Run scan test to verify it fails**

Run: `npm run test -- src/features/scan/ScanPage.test.tsx`

Expected: FAIL with missing `ScanPage` module.

- [ ] **Step 6: Implement scan page**

Create `ScanPage.tsx` with props:

```tsx
export interface ScanPageProps {
  onOpenProduct: (productId: string) => void;
  onOpenOrder: (orderId: string) => void;
}
```

Render three buttons: `商品码`, `订单码`, `包裹码`. Each displays its demo scan code. Product opens product detail, order opens order detail, package opens the linked order detail and shows copy saying the package code can be used for receipt confirmation when the order reaches pending receipt.

- [ ] **Step 7: Write failing profile test**

Create `src/features/profile/ProfilePage.test.tsx`:

```tsx
import { render, screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { describe, expect, it } from "vitest";
import { AppStateProvider } from "../../app/AppState";
import { I18nProvider } from "../../shared/i18n/I18nProvider";
import { ProfilePage } from "./ProfilePage";

describe("ProfilePage", () => {
  it("switches language", async () => {
    render(
      <AppStateProvider>
        <I18nProvider>
          <ProfilePage />
        </I18nProvider>
      </AppStateProvider>
    );

    await userEvent.click(screen.getByText("English"));
    expect(screen.getByText("Language")).toBeInTheDocument();
  });
});
```

- [ ] **Step 8: Run profile test to verify it fails**

Run: `npm run test -- src/features/profile/ProfilePage.test.tsx`

Expected: FAIL with missing `ProfilePage` module.

- [ ] **Step 9: Implement profile page**

Create `ProfilePage.tsx` to render language switch buttons, current ship summary, bound ship list using demo ship data, contact list examples, and links to historical orders. Use `useAppState` to switch language.

- [ ] **Step 10: Wire routes in `App.tsx`**

Render `OrdersPage`, `OrderDetailPage`, `ScanPage`, and `ProfilePage`. Maintain `selectedOrderId`. Bottom tab `orders`, `scan`, and `mine` must navigate to the corresponding pages.

- [ ] **Step 11: Add CSS for orders, scan, and profile**

Add `.order-list`, `.order-card`, `.timeline`, `.timeline-item`, `.scan-options`, `.profile-section`, and status badge spacing. Ensure timeline labels wrap cleanly at 375px.

- [ ] **Step 12: Run tests and build**

Run: `npm run test -- src/features/orders/OrderDetailPage.test.tsx src/features/scan/ScanPage.test.tsx src/features/profile/ProfilePage.test.tsx`

Expected: PASS.

Run: `npm run build`

Expected: PASS.

- [ ] **Step 13: Commit order, scan, and profile views**

```bash
git add src/features/orders src/features/scan src/features/profile src/app/App.tsx src/index.css
git commit -m "Add H5 order tracking and scan flows"
```

## Task 10: Add End-To-End Flow Test And Documentation

**Files:**
- Create: `src/app/App.test.tsx`
- Modify: `README.md`

- [ ] **Step 1: Write failing app flow test**

Create `src/app/App.test.tsx`:

```tsx
import { render, screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { describe, expect, it } from "vitest";
import { App } from "./App";

describe("H5 MVP flow", () => {
  it("lets a user browse, add to cart, and reach order confirmation", async () => {
    render(<App />);

    await userEvent.click(screen.getByText("商品"));
    await userEvent.type(screen.getByPlaceholderText("搜索商品"), "饮用水");
    await userEvent.click(screen.getByText("加入购物车"));
    await userEvent.click(screen.getByText("购物车 1"));

    expect(screen.getByText("订单确认")).toBeInTheDocument();
    expect(screen.getByText("保税饮用水 24 瓶")).toBeInTheDocument();
  });
});
```

- [ ] **Step 2: Run app flow test to verify it fails if routes are incomplete**

Run: `npm run test -- src/app/App.test.tsx`

Expected before final wiring: FAIL if any route labels or cart navigation are missing. If it passes immediately, verify the test can fail by temporarily changing the expected `订单确认` text to `订单确认X`, running the test to see FAIL, then restore the correct text.

- [ ] **Step 3: Complete app flow wiring**

Adjust `App.tsx` so the flow in `App.test.tsx` passes:

1. App starts on home with valid demo ship.
2. Bottom tab Goods opens catalog.
3. Add-to-cart increments app state.
4. Cart button opens order confirmation.
5. Order confirmation displays selected items.

- [ ] **Step 4: Update README with commands**

Modify `README.md` to include:

```md
## H5 Client MVP

The H5 client is a Vite + React + TypeScript single-page app with local mock data.

### Commands

- `npm install` — install dependencies.
- `npm run dev` — start the local development server.
- `npm run test` — run unit and component tests.
- `npm run build` — type-check and build the app.
- `npm run preview` — preview the production build locally.
```

- [ ] **Step 5: Run full test and build verification**

Run: `npm run test`

Expected: PASS for all test files.

Run: `npm run build`

Expected: PASS.

- [ ] **Step 6: Commit flow test and docs**

```bash
git add src/app/App.test.tsx README.md
git commit -m "Document and verify H5 MVP flow"
```

## Task 11: Browser Verification And Final Polish

**Files:**
- Modify only files with verified layout or copy issues found during browser checks.

- [ ] **Step 1: Start dev server**

Run: `npm run dev`

Expected: Vite prints a local URL such as `http://127.0.0.1:5173/`.

- [ ] **Step 2: Open browser at mobile viewport**

Use the Browser plugin or equivalent local browser automation to open the Vite URL at 375px width. Check:

1. Home shows ship context, active order count, available SKU count, and entry actions.
2. Goods list does not horizontally scroll.
3. Order confirmation keeps primary action above bottom tabs.
4. Order detail timeline labels wrap without overlap.
5. Bottom tabs remain visible and tappable.

- [ ] **Step 3: Open browser at desktop viewport**

Check the same URL at a desktop width. The app should remain centered with max width around a phone viewport and should not stretch operational cards across the whole screen.

- [ ] **Step 4: Fix any visual defects with focused CSS changes**

For each defect, change only the relevant CSS or component. Re-run:

```bash
npm run test
npm run build
```

Expected: both commands PASS.

- [ ] **Step 5: Commit visual polish if changes were needed**

If browser verification required changes:

```bash
git add src
git commit -m "Polish H5 MVP responsive layout"
```

If no changes were needed, do not create an empty commit.

## Task 12: Final Verification

**Files:**
- No planned file edits.

- [ ] **Step 1: Confirm worktree scope**

Run: `git status --short`

Expected: only unrelated pre-existing untracked files may remain, such as `.superpowers/`, `AGENTS.md`, `CLAUDE.md`, or the untracked GLM spec. There should be no unstaged implementation edits.

- [ ] **Step 2: Run full automated verification**

Run: `npm run test`

Expected: PASS for all tests.

Run: `npm run build`

Expected: PASS.

- [ ] **Step 3: Confirm demo server URL for handoff**

If the dev server is still running, provide its URL. If not running, start it with:

```bash
npm run dev
```

Expected: Vite prints a local URL that the user can open.

- [ ] **Step 4: Prepare final summary**

Report:

1. The local dev URL.
2. The commands that passed with fresh output.
3. The implemented MVP path: ship context, goods, cart/order confirmation, order tracking, scan, and profile.
4. Any remaining intentional limitations: local mock data, no real QR camera, no real backend, no real customs/drone APIs.

## Self-Review

Spec coverage:

1. Simulated scan entry and ship context resolution: Tasks 3, 4, 7, 9.
2. Operational home view and bottom tabs: Tasks 6 and 7.
3. Product category browsing, search, list, detail: Tasks 3 and 7.
4. Cart and order confirmation: Tasks 3 and 8.
5. Ship position display and manual berth or anchorage editing: Tasks 3, 7, 8.
6. Shipping agent display and validation: Tasks 2, 8.
7. Consignee, Cabin No., contact, expected delivery, remark: Task 8.
8. Local order creation and auto-trade versus matching-order decision: Tasks 3 and 8.
9. Order list and detail: Task 9.
10. Timeline, warehouse, drone, and customs statuses: Tasks 3 and 9.
11. Mock status refresh: Tasks 3 and 9.
12. Receipt confirmation: Tasks 3 and 9.
13. Scan tab for product, order, and package: Task 9.
14. Profile tab and language switching: Tasks 5 and 9.
15. Chinese and English key flow labels: Tasks 5 and 9.
16. Mobile width browser verification: Task 11.
17. Commands documented: Task 10.

Incomplete-marker scan result: no incomplete markers are intentionally present in this plan.

Type consistency:

1. `Product`, `Category`, `CartItem`, `Order`, `OrderItem`, `ShipContext`, `ShipLocationSource`, `TradeMode`, `OrderStatus`, `WarehouseStatus`, `DeliveryStatus`, and `CustomsSyncStatus` are defined in Task 2 and reused by later tasks.
2. `calculateCartTotals`, `validateCartForSubmission`, `decideTradeMode`, `createOrderFromCart`, `advanceOrderStatus`, `confirmReceipt`, `resolveShipToken`, and `updateShipLocation` are introduced before UI tasks use them.
3. Route names are introduced in Task 6 and extended consistently in Tasks 7 through 10.

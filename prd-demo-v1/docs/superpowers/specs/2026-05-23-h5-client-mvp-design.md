# H5 Client MVP Design

Date: 2026-05-23  
Status: Pending written spec review  
Product: 青岛汇·保税仓无人机配送平台  
Scope: H5 client MVP

## 1. Purpose

Build a runnable H5 client MVP for ship crew and ship users. The client should demonstrate the core P0 ordering flow from one-ship-one-code entry through product browsing, order submission, status tracking, and receipt confirmation.

The MVP uses local mock data and does not connect to real backend services, customs systems, ShipXY, MarineTraffic, payment, login, or drone hardware systems.

## 2. Product Scope

### 2.1 In Scope

1. Simulated scan entry and ship context resolution.
2. Operational home view with ship context, active orders, and key entry points.
3. Product category browsing, search, product list, and product detail.
4. Cart and order confirmation.
5. Ship position display and manual berth or anchorage editing.
6. Shipping agent display and required validation.
7. Consignee, Cabin No., contact, expected delivery time, and remark fields.
8. Local order creation with mock auto-trade or matching-order decision.
9. Order list and order detail.
10. Order status timeline, warehouse status, drone delivery status, and customs sync status display.
11. Mock status refresh to advance order status for demo use.
12. Receipt confirmation by verification code or simulated package-code scan.
13. Scan tab that simulates product, order, and package QR results.
14. Profile tab with language switching, current ship, bound ship list, contacts, and related placeholders.
15. Chinese and English text for key user-facing flow.
16. Mobile H5 layout targeting phone browsers, minimum width 375px.

### 2.2 Out of Scope

1. Real account login, SMS, email, or OAuth.
2. Real QR camera integration.
3. Real backend API integration.
4. Real ShipXY or MarineTraffic API integration.
5. Real customs sync requests.
6. Real payment, settlement, or invoice flow.
7. Real drone dispatch or websocket updates.
8. Warehouse client and management console.
9. Full rule engine or low-code rule configuration.

## 3. Information Architecture

The MVP uses an operational bottom-tab layout. The bottom tabs stay visible throughout the main app:

1. Goods: category browsing, search, product list, product detail entry.
2. Orders: active and historical orders, status filters, order detail entry.
3. Scan: simulated QR recognition for product, order, and package receipt.
4. Mine: language switching, current or bound ship information, contacts, and profile-related placeholders.

The home view is an operational workbench rather than a marketing page. It appears after ship context is resolved and keeps the following information visible in the first viewport:

1. Ship name, IMO or MMSI, port, berth or anchorage.
2. Shipping agent.
3. Ship position source: QR code, user binding, ShipXY, MarineTraffic, or manual edit.
4. Active order count and short order status summary.
5. Entry cards for goods, orders, scan, and profile-related actions.

## 4. User Flow

### 4.1 App Start And Ship Context

1. App reads `ship_token` from the URL query string.
2. If no token is present, the app uses a demo token.
3. Mock token resolution returns ship name, IMO, MMSI, berth or anchorage, shipping agent, port, token expiry, and location source.
4. If the token is invalid, the app shows a guided fallback screen for manual supplement or ship binding. The MVP does not implement real login.
5. A valid ship context leads to the operational home view.

### 4.2 Product Browsing

1. User opens the Goods tab or product entry on the home view.
2. User can filter by category and subcategory.
3. User can search with Chinese or English keywords.
4. Product cards show image, name, specification, price, inventory status, and drone-deliverable status.
5. Out-of-stock and non-drone-deliverable products remain visible but provide clear action feedback.

### 4.3 Product Detail

1. Product detail shows images, bilingual name and description, specification, price, unit weight, unit volume, inventory, and delivery restrictions.
2. User can add the product to cart.
3. User can choose buy now, which adds the product to cart and opens order confirmation.

### 4.4 Cart And Order Confirmation

1. User can modify item quantities and remove items.
2. The app recalculates subtotal, total price, total weight, and total volume immediately.
3. The app validates stock, drone-deliverable flag, weight, and volume.
4. The app displays ship information, berth or anchorage, shipping agent, and position source.
5. User can manually edit berth or anchorage. After editing, the location source becomes manual edit and the edit timestamp is recorded locally.
6. Shipping agent is required.
7. Consignee name and Cabin No. are required. Contact information is optional in the MVP UI but supported by the data model.
8. Expected delivery time and remark are optional.
9. Submitting creates a local order and opens order detail.

### 4.5 Mock Order Decision

The MVP uses deterministic local rules to classify an order:

1. Auto-trade if every item is in stock, drone deliverable, total weight is within mock drone payload, total volume is within mock drone volume, ship position is confirmed, and shipping agent is present.
2. Matching order if any automatic condition fails but order submission is still allowed.
3. Submission is blocked only when required user fields are missing or cart is empty.

### 4.6 Order Tracking

Order detail displays:

1. Order number, created time, trade mode, and current primary status.
2. Product line items and total price.
3. Ship, consignee, Cabin No., target position, and shipping agent.
4. Primary order status timeline.
5. Warehouse processing status.
6. Drone delivery status.
7. Customs sync status.
8. Expected arrival when available.
9. Exception contact entry.

The MVP does not implement websocket or polling. A visible refresh or advance action moves the mock order through valid demo states for presentation.

### 4.7 Receipt Confirmation

1. Receipt confirmation is enabled only when an order reaches pending receipt.
2. User can enter a verification code or use the Scan tab to simulate scanning a package code.
3. Successful confirmation sets the order status to completed, records receipt method, and records completion time.

## 5. UX And Visual Direction

The UI should feel like a work-focused port operations tool adapted for phone browsers. It should not look like a marketing landing page.

Visual principles:

1. Use a compact, scannable layout with clear hierarchy.
2. Keep ship context and order status prominent.
3. Use restrained color for status and compliance cues.
4. Use icons for tab navigation and repeated actions.
5. Avoid oversized hero sections, decorative cards, and decorative gradients.
6. Keep cards for repeated product or order items, not for every page section.
7. Ensure text does not overflow at 375px width.

Suggested status color roles:

1. Teal or blue for normal active operational state.
2. Amber for matching order, waiting, or yellow-card warning state.
3. Red for exception or blocking state.
4. Green for completed or successful confirmation.
5. Neutral gray for inactive, historical, or metadata text.

## 6. Technical Architecture

### 6.1 Stack

Use Vite, React, and TypeScript. The app is a client-side single-page H5 application using mock APIs and local persistence.

### 6.2 Suggested Directory Structure

```text
src/
  app/
    App.tsx
    routes.tsx
    AppLayout.tsx
  features/
    catalog/
    cart/
    orders/
    scan/
    ship/
    profile/
  shared/
    components/
    data/
    i18n/
    lib/
    types/
```

### 6.3 Feature Boundaries

1. `app`: application shell, routing, tab layout, and route-level composition.
2. `features/catalog`: category list, product list, product filtering, product detail.
3. `features/cart`: cart state, cart totals, order confirmation, order validation.
4. `features/orders`: order list, order detail, order timeline, mock status advancement, receipt confirmation.
5. `features/ship`: ship context resolution, token handling, location display, manual location edit.
6. `features/scan`: mock scan result selection and route dispatch.
7. `features/profile`: language switch, current ship, bound ship and contact placeholders.
8. `shared`: reusable UI primitives, common types, mock API helpers, formatting, and i18n utilities.

### 6.4 State And Persistence

Use React hooks, context, and localStorage. The following data should survive page refresh during demos:

1. Current language.
2. Current ship context.
3. Cart items.
4. Created orders and their statuses.
5. Manually edited berth or anchorage.

Avoid introducing a larger state management library for the MVP unless implementation proves local state is creating real complexity.

## 7. Data Model

The MVP should define explicit TypeScript types for:

1. `Product`
2. `Category`
3. `CartItem`
4. `Order`
5. `OrderItem`
6. `ShipContext`
7. `ShipLocationSource`
8. `TradeMode`
9. `OrderStatus`
10. `WarehouseStatus`
11. `DeliveryStatus`
12. `CustomsSyncStatus`

These types should align with the PRD terminology and status names where practical.

## 8. Mock Data And Mock API

Mock data should include:

1. At least one valid demo ship and one invalid token path.
2. At least two category levels.
3. Products with varied inventory, weight, volume, and drone-deliverable status.
4. At least one active order and one completed historical order.
5. Mock shipping agents.
6. Mock scan codes for product, order, and package receipt.

Mock APIs should be asynchronous, even if backed by local data, so future real API replacement is straightforward.

## 9. Testing Strategy

Implementation should follow test-first development for behavior-bearing logic.

Required unit-level coverage:

1. Product category filtering and bilingual keyword search.
2. Cart total price, total weight, and total volume calculation.
3. Stock and drone-delivery validation.
4. Required order confirmation field validation.
5. Mock auto-trade versus matching-order decision.
6. Order status advancement.
7. Receipt confirmation guard for pending-receipt-only confirmation.

Required user-flow coverage:

1. Add product to cart and submit order.
2. Edit berth or anchorage before submission and verify manual-edit source.
3. Create matching order when automatic conditions fail.
4. Advance an order to pending receipt and confirm receipt.
5. Switch language and see key flow labels update.

Required browser checks:

1. Home, goods list, order confirmation, and order detail at 375px width.
2. Same pages at desktop width for development convenience.
3. Bottom tab remains usable and does not overlap primary actions.
4. Text does not overflow buttons, cards, or status labels.

## 10. Acceptance Criteria

The MVP is accepted when:

1. `npm install` installs dependencies.
2. `npm run dev` starts the H5 client locally.
3. `npm run test` passes.
4. `npm run build` passes.
5. A user can complete the demo flow from ship context to completed receipt without editing code.
6. The app works at 375px viewport width without layout overlap.
7. The UI includes Chinese and English labels for the core flow.
8. Mock data and mock APIs are isolated from page components.
9. The implementation documents the new build and test commands in the repository guidance.

## 11. Implementation Notes

1. Prefer small focused components over a large page file that owns all business logic.
2. Keep business calculations and status transition logic outside React components so they can be unit tested.
3. Do not expose real ShipXY, MarineTraffic, customs, or drone credentials in the frontend.
4. Do not add payment or settlement UI to the MVP.
5. Use local placeholder product imagery or generated-safe assets; avoid relying on unavailable remote images for the core demo.

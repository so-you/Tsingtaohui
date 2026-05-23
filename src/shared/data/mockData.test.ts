import { describe, expect, it } from "vitest";
import { categories, demoShipTokens, initialOrders, products, scanCodes, shippingAgents } from "./mockData";

describe("mockData", () => {
  it("contains one valid demo token and one invalid demo token path", () => {
    expect(demoShipTokens["demo-ship-token"]?.shipName).toBe("TSINGTAO STAR");
    expect(demoShipTokens["invalid-token"]).toBeNull();
    expect(demoShipTokens["unknown-token"]).toBeUndefined();
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

  it("uses existing categories for every product", () => {
    const categoryIds = new Set(categories.map((category) => category.id));

    for (const product of products) {
      expect(categoryIds.has(product.categoryId)).toBe(true);
    }
  });

  it("uses existing products for every order item", () => {
    const productIds = new Set(products.map((product) => product.id));

    for (const order of initialOrders) {
      expect(order.items.length).toBeGreaterThan(0);

      for (const item of order.items) {
        expect(productIds.has(item.productId)).toBe(true);
      }
    }
  });

  it("uses existing shipping agents for every order ship", () => {
    const shippingAgentIds = new Set(shippingAgents.map((agent) => agent.id));

    for (const order of initialOrders) {
      expect(shippingAgentIds.has(order.ship.shippingAgentId)).toBe(true);
    }
  });

  it("keeps order totals aligned with item quantities", () => {
    for (const order of initialOrders) {
      const expectedTotalPrice = order.items.reduce((total, item) => total + item.unitPrice * item.quantity, 0);
      const expectedTotalWeightKg = order.items.reduce((total, item) => total + item.unitWeightKg * item.quantity, 0);
      const expectedTotalVolumeM3 = order.items.reduce((total, item) => total + item.unitVolumeM3 * item.quantity, 0);

      expect(order.totalPrice).toBeCloseTo(expectedTotalPrice);
      expect(order.totalWeightKg).toBeCloseTo(expectedTotalWeightKg);
      expect(order.totalVolumeM3).toBeCloseTo(expectedTotalVolumeM3);
    }
  });

  it("uses existing targets for scan codes", () => {
    const productIds = new Set(products.map((product) => product.id));
    const orderIds = new Set(initialOrders.map((order) => order.id));

    expect(productIds.has(scanCodes.product.targetId)).toBe(true);
    expect(orderIds.has(scanCodes.order.targetId)).toBe(true);
    expect(orderIds.has(scanCodes.package.targetId)).toBe(true);
  });

  it("uses independent ship snapshots for initial orders", () => {
    expect(new Set(initialOrders.map((order) => order.ship)).size).toBe(initialOrders.length);
  });
});

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

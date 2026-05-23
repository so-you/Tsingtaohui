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

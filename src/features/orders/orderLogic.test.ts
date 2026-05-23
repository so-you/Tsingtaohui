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
    expect(() => confirmReceipt({ ...base, status: "IN_DELIVERY" }, "CODE", "2026-05-23T11:00:00+08:00")).toThrow(
      "Order is not pending receipt"
    );
  });
});

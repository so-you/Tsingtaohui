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

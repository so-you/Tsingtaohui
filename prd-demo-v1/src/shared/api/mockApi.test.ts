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
    await expect(resolveScanCode("SCAN-PRODUCT-WATER")).resolves.toMatchObject({
      kind: "product",
      targetId: "prod-water"
    });
  });
});

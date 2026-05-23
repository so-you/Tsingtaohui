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

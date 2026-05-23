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

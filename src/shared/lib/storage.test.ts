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

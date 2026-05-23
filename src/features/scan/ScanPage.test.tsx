import { cleanup, render, screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { afterEach, describe, expect, it } from "vitest";
import { ScanPage } from "./ScanPage";

describe("ScanPage", () => {
  afterEach(() => {
    cleanup();
  });

  it("simulates product code scanning", async () => {
    render(<ScanPage onOpenOrder={() => null} onOpenProduct={() => null} />);

    await userEvent.click(screen.getByText("商品码"));

    expect(screen.getByText("SCAN-PRODUCT-WATER")).toBeInTheDocument();
  });
});

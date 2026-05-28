import { cleanup, render, screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { afterEach, beforeEach, describe, expect, it } from "vitest";
import { App } from "./App";

describe("H5 MVP flow", () => {
  beforeEach(() => {
    localStorage.clear();
  });

  afterEach(() => {
    cleanup();
  });

  it("lets a user browse, add to cart, and reach order confirmation", async () => {
    render(<App />);

    await userEvent.click(screen.getByText("商品"));
    await userEvent.type(screen.getByPlaceholderText("搜索商品"), "饮用水");
    await userEvent.click(screen.getByText("加入购物车"));
    await userEvent.click(screen.getByText("购物车 1"));

    expect(screen.getByText("订单确认")).toBeInTheDocument();
    expect(screen.getByText("保税饮用水 24 瓶")).toBeInTheDocument();
  });
});

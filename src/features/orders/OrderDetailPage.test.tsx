import { cleanup, render, screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { afterEach, beforeEach, describe, expect, it } from "vitest";
import { AppStateProvider } from "../../app/AppState";
import { I18nProvider } from "../../shared/i18n/I18nProvider";
import { OrderDetailPage } from "./OrderDetailPage";

describe("OrderDetailPage", () => {
  beforeEach(() => {
    localStorage.clear();
  });

  afterEach(() => {
    cleanup();
  });

  it("renders an order and advances status on refresh", async () => {
    render(
      <AppStateProvider>
        <I18nProvider>
          <OrderDetailPage orderId="ORD-20260523-0001" onBack={() => null} />
        </I18nProvider>
      </AppStateProvider>
    );

    expect(screen.getByText("ORD-20260523-0001")).toBeInTheDocument();

    await userEvent.click(screen.getByText("刷新状态"));

    expect(screen.getAllByText(/待签收|已完成|配送中/).length).toBeGreaterThan(0);
  });
});

import { cleanup, render, screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { afterEach, beforeEach, describe, expect, it } from "vitest";
import { AppStateProvider, useAppState } from "../../app/AppState";
import { demoShipTokens } from "../../shared/data/mockData";
import { I18nProvider } from "../../shared/i18n/I18nProvider";
import { OrderConfirmPage } from "./OrderConfirmPage";

function Harness() {
  const { addToCart, setCurrentShip } = useAppState();

  return (
    <>
      <button
        onClick={() => {
          setCurrentShip(demoShipTokens["demo-ship-token"]!);
          addToCart("prod-water");
        }}
      >
        seed
      </button>
      <OrderConfirmPage onOrderCreated={() => null} />
    </>
  );
}

describe("OrderConfirmPage", () => {
  beforeEach(() => {
    localStorage.clear();
  });

  afterEach(() => {
    cleanup();
  });

  it("requires consignee and cabin before order submission", async () => {
    render(
      <AppStateProvider>
        <I18nProvider>
          <Harness />
        </I18nProvider>
      </AppStateProvider>
    );

    await userEvent.click(screen.getByText("seed"));
    await userEvent.click(screen.getByText("提交订单"));
    expect(screen.getByText("请填写收货人")).toBeInTheDocument();
    expect(screen.getByText("请填写 Cabin No.")).toBeInTheDocument();
  });

  it("submits an order after berth edit and required fields", async () => {
    render(
      <AppStateProvider>
        <I18nProvider>
          <Harness />
        </I18nProvider>
      </AppStateProvider>
    );

    await userEvent.click(screen.getByText("seed"));
    await userEvent.clear(screen.getByLabelText("泊位 / 锚地"));
    await userEvent.type(screen.getByLabelText("泊位 / 锚地"), "Anchorage A3");
    await userEvent.type(screen.getByLabelText("收货人"), "Alex Chen");
    await userEvent.type(screen.getByLabelText("Cabin No."), "C-203");
    await userEvent.click(screen.getByText("提交订单"));

    expect(screen.getByText("订单已创建")).toBeInTheDocument();
  });
});

import { render, screen } from "@testing-library/react";
import { describe, expect, it } from "vitest";
import { I18nProvider } from "../shared/i18n/I18nProvider";
import { AppLayout } from "./AppLayout";
import { AppStateProvider } from "./AppState";

describe("AppLayout", () => {
  it("renders operational bottom tabs", () => {
    render(
      <AppStateProvider>
        <I18nProvider>
          <AppLayout activeRoute="goods" onNavigate={() => null}>
            <div>content</div>
          </AppLayout>
        </I18nProvider>
      </AppStateProvider>
    );

    expect(screen.getByText("商品")).toBeInTheDocument();
    expect(screen.getByText("订单")).toBeInTheDocument();
    expect(screen.getByText("扫码")).toBeInTheDocument();
    expect(screen.getByText("我的")).toBeInTheDocument();
  });
});

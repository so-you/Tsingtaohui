import { render, screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { describe, expect, it } from "vitest";
import { AppStateProvider } from "../../app/AppState";
import { I18nProvider } from "../../shared/i18n/I18nProvider";
import { CatalogPage } from "./CatalogPage";

describe("CatalogPage", () => {
  it("searches products and adds a product to cart", async () => {
    render(
      <AppStateProvider>
        <I18nProvider>
          <CatalogPage onOpenProduct={() => null} onOpenCart={() => null} />
        </I18nProvider>
      </AppStateProvider>
    );

    await userEvent.type(screen.getByPlaceholderText("搜索商品"), "饮用水");
    expect(screen.getByText("保税饮用水 24 瓶")).toBeInTheDocument();
    expect(screen.queryByText("即食餐包 12 份")).not.toBeInTheDocument();

    await userEvent.click(screen.getByText("加入购物车"));
    expect(screen.getByText("购物车 1")).toBeInTheDocument();
  });
});

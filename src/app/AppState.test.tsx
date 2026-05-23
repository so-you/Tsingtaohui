import { render, screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { beforeEach, describe, expect, it } from "vitest";
import { AppStateProvider, useAppState } from "./AppState";

function Probe() {
  const { cartItems, addToCart, language, setLanguage } = useAppState();

  return (
    <div>
      <div data-testid="language">{language}</div>
      <div data-testid="cart-count">{cartItems.length}</div>
      <button onClick={() => addToCart("prod-water")}>add</button>
      <button onClick={() => setLanguage("en")}>en</button>
    </div>
  );
}

describe("AppState", () => {
  beforeEach(() => localStorage.clear());

  it("stores language and cart items", async () => {
    render(
      <AppStateProvider>
        <Probe />
      </AppStateProvider>
    );

    await userEvent.click(screen.getByText("add"));
    await userEvent.click(screen.getByText("en"));

    expect(screen.getByTestId("cart-count")).toHaveTextContent("1");
    expect(screen.getByTestId("language")).toHaveTextContent("en");
  });
});

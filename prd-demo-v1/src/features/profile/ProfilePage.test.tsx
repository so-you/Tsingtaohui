import { cleanup, render, screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { afterEach, beforeEach, describe, expect, it } from "vitest";
import { AppStateProvider } from "../../app/AppState";
import { I18nProvider } from "../../shared/i18n/I18nProvider";
import { ProfilePage } from "./ProfilePage";

describe("ProfilePage", () => {
  beforeEach(() => {
    localStorage.clear();
  });

  afterEach(() => {
    cleanup();
  });

  it("switches language to English", async () => {
    render(
      <AppStateProvider>
        <I18nProvider>
          <ProfilePage />
        </I18nProvider>
      </AppStateProvider>
    );

    await userEvent.click(screen.getByText("English"));

    expect(screen.getByText("Language")).toBeInTheDocument();
  });
});

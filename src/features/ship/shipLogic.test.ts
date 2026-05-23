import { describe, expect, it } from "vitest";
import { resolveShipToken, updateShipLocation } from "./shipLogic";

describe("shipLogic", () => {
  it("resolves missing token to the demo ship", () => {
    expect(resolveShipToken(undefined).ship?.shipName).toBe("TSINGTAO STAR");
  });

  it("marks invalid token as invalid", () => {
    expect(resolveShipToken("invalid-token").status).toBe("invalid");
  });

  it("manual berth edit changes source and timestamp", () => {
    const ship = resolveShipToken("demo-ship-token").ship!;
    const edited = updateShipLocation(ship, "Anchorage A3", "2026-05-23T12:00:00+08:00");
    expect(edited.berthOrAnchorage).toBe("Anchorage A3");
    expect(edited.locationSource).toBe("MANUAL_EDIT");
    expect(edited.manuallyEditedAt).toBe("2026-05-23T12:00:00+08:00");
  });
});

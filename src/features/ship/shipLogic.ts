import { demoShipTokens } from "../../shared/data/mockData";
import type { ShipContext } from "../../shared/types/domain";

export type ShipTokenResolution = { status: "valid"; ship: ShipContext } | { status: "invalid"; ship: null };

export function resolveShipToken(token?: string | null): ShipTokenResolution {
  const tokenKey = token === undefined ? "demo-ship-token" : token;
  const ship = tokenKey === null ? null : demoShipTokens[tokenKey];

  if (!ship) {
    return { status: "invalid", ship: null };
  }

  return { status: "valid", ship: { ...ship } };
}

export function updateShipLocation(ship: ShipContext, berthOrAnchorage: string, now: string): ShipContext {
  return {
    ...ship,
    berthOrAnchorage,
    locationSource: "MANUAL_EDIT",
    locationUpdatedAt: now,
    manuallyEditedAt: now
  };
}

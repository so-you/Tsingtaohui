import { resolveShipToken } from "../../features/ship/shipLogic";
import { categories, initialOrders, products, scanCodes, shippingAgents } from "../data/mockData";
import type { Order, ScanCode } from "../types/domain";

const delayMs = 10;

export async function getProducts() {
  return withDelay(products);
}

export async function getCategories() {
  return withDelay(categories);
}

export async function getShippingAgents() {
  return withDelay(shippingAgents);
}

export async function getInitialOrders(): Promise<Order[]> {
  return withDelay(initialOrders);
}

export async function getShipByToken(token?: string | null) {
  return withDelay(resolveShipToken(token));
}

export async function resolveScanCode(code: string): Promise<ScanCode | null> {
  const match = Object.values(scanCodes).find((candidate) => candidate.code === code) ?? null;
  return withDelay(match);
}

async function withDelay<T>(value: T): Promise<T> {
  await new Promise((resolve) => window.setTimeout(resolve, delayMs));
  return structuredClone(value);
}

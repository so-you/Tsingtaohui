import type { ReactNode } from "react";

export function StatusBadge({ tone, children }: { tone: "blue" | "green" | "amber" | "red" | "gray"; children: ReactNode }) {
  return <span className={`status-badge status-${tone}`}>{children}</span>;
}

export function formatCurrency(value: number): string {
  return `¥${value.toFixed(2)}`;
}

export function formatMeasure(value: number, unit: string): string {
  return `${Number(value.toFixed(3))} ${unit}`;
}

export function formatDateTime(value?: string): string {
  if (!value) {
    return "-";
  }

  return new Intl.DateTimeFormat("zh-CN", {
    year: "numeric",
    month: "2-digit",
    day: "2-digit",
    hour: "2-digit",
    minute: "2-digit"
  }).format(new Date(value));
}

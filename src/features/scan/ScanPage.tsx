import { ClipboardList, PackageCheck, PackageSearch, QrCode } from "lucide-react";
import { useState } from "react";
import { Button } from "../../shared/components/Button";
import { StatusBadge } from "../../shared/components/StatusBadge";
import { scanCodes } from "../../shared/data/mockData";
import type { ScanCode } from "../../shared/types/domain";

interface ScanPageProps {
  onOpenOrder: (orderId: string) => void;
  onOpenProduct: (productId: string) => void;
}

const scanOptions: Array<{ kind: ScanCode["kind"]; label: string; icon: typeof QrCode }> = [
  { kind: "product", label: "商品码", icon: PackageSearch },
  { kind: "order", label: "订单码", icon: ClipboardList },
  { kind: "package", label: "包裹码", icon: PackageCheck }
];

export function ScanPage({ onOpenOrder, onOpenProduct }: ScanPageProps) {
  const [result, setResult] = useState<ScanCode | null>(null);

  function openResult() {
    if (!result) {
      return;
    }

    if (result.kind === "product") {
      onOpenProduct(result.targetId);
    } else {
      onOpenOrder(result.targetId);
    }
  }

  return (
    <section className="page scan-page">
      <header className="page-header">
        <div>
          <p className="eyebrow">QR</p>
          <h1>扫码模拟</h1>
        </div>
        <StatusBadge tone="blue">Demo</StatusBadge>
      </header>

      <div className="scan-grid">
        {scanOptions.map((option) => {
          const Icon = option.icon;
          return (
            <button className="scan-card" key={option.kind} onClick={() => setResult(scanCodes[option.kind])} type="button">
              <Icon size={22} />
              <span>{option.label}</span>
            </button>
          );
        })}
      </div>

      {result ? (
        <article className="scan-result">
          <span>识别结果</span>
          <strong>{result.code}</strong>
          <small>{result.targetId}</small>
          <Button onClick={openResult} variant="secondary">
            {result.kind === "product" ? "查看商品" : "查看订单"}
          </Button>
        </article>
      ) : (
        <article className="scan-result muted">
          <span>等待扫码</span>
          <strong>请点击上方模拟码</strong>
        </article>
      )}
    </section>
  );
}

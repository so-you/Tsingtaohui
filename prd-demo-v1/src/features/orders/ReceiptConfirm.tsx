import { CheckCircle2, PackageCheck } from "lucide-react";
import { useState } from "react";
import { Button } from "../../shared/components/Button";
import { Field } from "../../shared/components/Field";
import type { Order } from "../../shared/types/domain";

interface ReceiptConfirmProps {
  order: Order;
  onConfirm: (method: NonNullable<Order["receiptMethod"]>) => void;
}

export function ReceiptConfirm({ order, onConfirm }: ReceiptConfirmProps) {
  const [code, setCode] = useState("");
  const canConfirm = order.status === "PENDING_RECEIPT";

  return (
    <section className="receipt-panel">
      <div>
        <strong>签收确认</strong>
        <p>{canConfirm ? "输入验证码或扫描包裹码完成签收。" : "无人机到达后可在此确认收货。"}</p>
      </div>
      <Field
        label="签收验证码"
        disabled={!canConfirm}
        placeholder="6 位验证码"
        value={code}
        onChange={(event) => setCode(event.target.value)}
      />
      <div className="receipt-actions">
        <Button disabled={!canConfirm || !code.trim()} onClick={() => onConfirm("CODE")}>
          <CheckCircle2 size={18} /> 确认收货
        </Button>
        <Button disabled={!canConfirm} variant="ghost" onClick={() => onConfirm("PACKAGE_SCAN")}>
          <PackageCheck size={18} /> 包裹码签收
        </Button>
      </div>
    </section>
  );
}

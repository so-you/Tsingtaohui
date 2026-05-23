import { Minus, Plus } from "lucide-react";

export function QuantityStepper({ value, onChange }: { value: number; onChange: (value: number) => void }) {
  return (
    <div className="quantity-stepper">
      <button type="button" aria-label="Decrease quantity" onClick={() => onChange(Math.max(0, value - 1))}>
        <Minus size={14} />
      </button>
      <span>{value}</span>
      <button type="button" aria-label="Increase quantity" onClick={() => onChange(value + 1)}>
        <Plus size={14} />
      </button>
    </div>
  );
}

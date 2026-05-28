import type { InputHTMLAttributes, ReactNode } from "react";

interface FieldProps extends InputHTMLAttributes<HTMLInputElement> {
  label: string;
  action?: ReactNode;
  error?: string;
}

export function Field({ label, error, action, ...props }: FieldProps) {
  return (
    <label className="field">
      <span className="field-label">
        {label}
        {action}
      </span>
      <input className="field-input" {...props} />
      {error ? <span className="field-error">{error}</span> : null}
    </label>
  );
}

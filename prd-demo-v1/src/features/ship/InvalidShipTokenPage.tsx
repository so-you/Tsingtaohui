import { Button } from "../../shared/components/Button";

export function InvalidShipTokenPage({ onUseDemo }: { onUseDemo: () => void }) {
  return (
    <section className="page invalid-token-page">
      <h1>Ship code unavailable</h1>
      <p>The ship QR token is invalid, expired, or revoked. Use demo mode to continue the MVP flow.</p>
      <Button onClick={onUseDemo}>Use demo ship</Button>
    </section>
  );
}

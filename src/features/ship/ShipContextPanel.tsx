import { Button } from "../../shared/components/Button";
import { StatusBadge } from "../../shared/components/StatusBadge";
import type { ShipContext } from "../../shared/types/domain";

export function ShipContextPanel({ ship, onEdit }: { ship: ShipContext | null; onEdit?: () => void }) {
  if (!ship) {
    return (
      <section className="ship-panel">
        <p className="eyebrow">Ship Context</p>
        <h2>Ship not confirmed</h2>
        <p>Please scan a ship code or use demo mode.</p>
      </section>
    );
  }

  return (
    <section className="ship-panel">
      <div className="ship-panel-heading">
        <div>
          <p className="eyebrow">{ship.port}</p>
          <h2>{ship.shipName}</h2>
        </div>
        <StatusBadge tone={ship.locationSource === "MANUAL_EDIT" ? "amber" : "blue"}>{ship.locationSource}</StatusBadge>
      </div>
      <dl className="ship-facts">
        <div>
          <dt>IMO / MMSI</dt>
          <dd>{ship.imo ?? ship.mmsi ?? "-"}</dd>
        </div>
        <div>
          <dt>Berth / Anchorage</dt>
          <dd>{ship.berthOrAnchorage}</dd>
        </div>
        <div>
          <dt>Agent</dt>
          <dd>{ship.shippingAgentName}</dd>
        </div>
      </dl>
      {onEdit ? (
        <Button onClick={onEdit} variant="ghost">
          Edit position
        </Button>
      ) : null}
    </section>
  );
}

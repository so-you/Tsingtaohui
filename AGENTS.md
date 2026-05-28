# AGENTS.md

This file provides guidance to Codex (Codex.ai/code) when working with code in this repository.

## Project Overview

Tsingtaohui (青岛汇) is a bonded warehouse-to-ship drone delivery platform for the Port of Qingdao. It provides a closed-loop business flow from online ordering, bonded warehouse fulfillment, drone delivery, ship delivery, to customs data synchronization.

This repository currently contains product specifications and documentation. No application source code, tests, or build system exist yet.

## Project Structure

- `README.md` — Project landing note (minimal).
- `AGENTS.md` — Contributor and agent guidance, including coding style, commit conventions, and PR guidelines.
- `docs/superpowers/specs/` — Product and design specifications.
  - `2026-05-22-bonded-warehouse-ship-drone-delivery-product-spec.md` — Full product spec (V1.0 MVP).

## Build, Test, and Development Commands

No build system or runtime is configured yet. Use these commands when working in the repo:

- `rg --files` — List tracked project files quickly.
- `git status --short` — Check local changes before editing or committing.
- `git diff` — Review pending edits.

When a runtime is added (e.g., `npm`, `cargo`, `poetry`), document the exact commands here: `npm run dev`, `npm test`, `make build`, etc.

## Architecture & Key Design Decisions

The product spec defines a three-client architecture. When implementation begins, these are the high-level structures to preserve:

### Three-Client Architecture

1. **H5 Client** — For ship crew/users. Scan a "one-ship-one-code" QR to enter, no registration/login required. Supports Chinese and English.
2. **Warehouse Client** — For bonded warehouse operators. Picking, verification, packing, outbound. Must support PDA, Bluetooth ring scanners, and physical barcode scanners via keyboard input emulation.
3. **Management Console** — For platform operators. Order management, matching pool, product/stock/drone management, rule configuration, customs sync monitoring, role-based access.

### Core Business Flow

1. User scans QR → H5 auto-identifies ship, berth, anchorage, shipping agent.
2. User browses products, adds to cart, fills in consignee and Cabin No.
3. System validates stock, weight, volume, delivery rules.
4. Order enters either **Auto-Trade** flow (automatic fulfillment) or **Matching Order** pool (manual operator confirmation).
5. Warehouse receives order, locks stock, picks/verifies/packs.
6. System auto-matches drone model and flight based on weight, volume, distance, and availability.
7. Drone delivers to ship; user confirms receipt via QR scan, verification code, or manual confirmation.
8. Key nodes sync to customs server.

### Customs Sync & Compliance

- **Red-card interception** (blocking): Order creation and warehouse outbound. Customs sync must succeed before warehouse operations proceed or drone tasks activate.
- **Yellow-card warning** (non-blocking): Delivery task creation, drone loading, in-delivery, delivered, signed-for, order exception. Failures trigger alerts and retries but do not block fulfillment.
- All sync requests/responses must be logged for traceability.

### Drone Integration

- The drone system is treated as an external black-box hardware system.
- Platform does not control flight actions directly.
- Minimum API contract: `GET /drones/status`, `POST /deliveries/dispatch`, and a webhook callback for task status updates.
- Callbacks must be handled idempotently.

### Order States

Primary states: Pending Submission → Pending Confirmation → Confirmed → Warehouse Processing → Pending Outbound → Outbound → Pending Loading → In Delivery → Pending Receipt → Completed / Cancelled / Exception.

### Transaction Modes

- **Auto-Trade**: For standardized bonded warehouse orders that pass all automatic checks (stock, weight, volume, drone availability, ship position, service hours, customs data completeness).
- **Matching Order**: For orders requiring manual intervention (oversized, multi-package, unclear ship position, special delivery time, insufficient stock but allocatable, drone match failure, etc.).

### Key Data Models (from spec)

- Product (with SKU, weight, volume, drone-deliverable flag, merchant_id for future multi-merchant expansion).
- Order (with ship info, IMO/MMSI, shipping agent, consignee, Cabin No., trade mode, order/warehouse/delivery/customs-sync statuses).
- Ship QR Access Token (encrypted, tamper-proof, expirable, revocable; binds to one ship only).
- Inventory (warehouse, location, batch, available/locked stock).
- Drone (model, flight number, max payload, volume capacity, range, deliverable categories, status).
- Delivery Task (order, package, warehouse, target ship, drone, status).
- Customs Sync Record (order, node, request/response, status, retry count).

## Coding Style & Naming Conventions

For Markdown documents (current primary format):

- Use clear headings and short paragraphs.
- Prefer numbered lists for ordered processes, bullet lists for grouped facts.
- Use descriptive kebab-case filenames for specs, e.g., `2026-05-22-bonded-warehouse-ship-drone-delivery-product-spec.md`.
- Keep terminology consistent: use "H5", "保税仓", "船舶代理人", "无人机", and "海关同步" as established in the spec.

For future code, follow the language formatter and linter adopted by the project. Do not introduce unrelated formatting churn.

## Commit Style

Use concise, imperative English summaries, e.g.:

- `Add bonded warehouse ship delivery product spec`
- `Refine ship delivery product compliance and operations spec`

Start with a verb, describe the change, and keep the subject focused.

## Testing Guidelines

No automated tests exist yet. For documentation changes, verify:

- Links and file paths are correct.
- Sections do not contain placeholders such as `TODO`, `TBD`, or `待定`.
- Requirements do not contradict the current product spec.

When code is introduced, add tests near the implementation or under `tests/`, and document the test command here.

## Security Notes

- Do not commit credentials, API keys, private tokens, or real customer data.
- Product specs may describe sensitive integrations (e.g., customs interface, QR token behavior), but must avoid exposing actual endpoints, secrets, or production identifiers.
- Ship QR tokens must be encrypted and tamper-proof; they should not expose forgeable sensitive business fields in the URL.

# AGENTS.md

Behavioral guidelines to reduce common LLM coding mistakes. Merge with project-specific instructions as needed.

**Tradeoff:** These guidelines bias toward caution over speed. For trivial tasks, use judgment.

## 1. Think Before Coding

**Don't assume. Don't hide confusion. Surface tradeoffs.**

Before implementing:
- State your assumptions explicitly. If uncertain, ask.
- If multiple interpretations exist, present them - don't pick silently.
- If a simpler approach exists, say so. Push back when warranted.
- If something is unclear, stop. Name what's confusing. Ask.

## 2. Simplicity First

**Minimum code that solves the problem. Nothing speculative.**

- No features beyond what was asked.
- No abstractions for single-use code.
- No "flexibility" or "configurability" that wasn't requested.
- No error handling for impossible scenarios.
- If you write 200 lines and it could be 50, rewrite it.

Ask yourself: "Would a senior engineer say this is overcomplicated?" If yes, simplify.

## 3. Surgical Changes

**Touch only what you must. Clean up only your own mess.**

When editing existing code:
- Don't "improve" adjacent code, comments, or formatting.
- Don't refactor things that aren't broken.
- Match existing style, even if you'd do it differently.
- If you notice unrelated dead code, mention it - don't delete it.

When your changes create orphans:
- Remove imports/variables/functions that YOUR changes made unused.
- Don't remove pre-existing dead code unless asked.

The test: Every changed line should trace directly to the user's request.

## 4. Goal-Driven Execution

**Define success criteria. Loop until verified.**

Transform tasks into verifiable goals:
- "Add validation" → "Write tests for invalid inputs, then make them pass"
- "Fix the bug" → "Write a test that reproduces it, then make it pass"
- "Refactor X" → "Ensure tests pass before and after"

For multi-step tasks, state a brief plan:
```
1. [Step] → verify: [check]
2. [Step] → verify: [check]
3. [Step] → verify: [check]
```

Strong success criteria let you loop independently. Weak criteria ("make it work") require constant clarification.

---

**These guidelines are working if:** fewer unnecessary changes in diffs, fewer rewrites due to overcomplication, and clarifying questions come before implementation rather than after mistakes.

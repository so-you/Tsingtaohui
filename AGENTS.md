# Repository Guidelines

## Project Structure & Module Organization

This repository currently contains product documentation for the Tsingtaohui project.

- `README.md`: project landing note.
- `docs/superpowers/specs/`: product and design specifications.
- `AGENTS.md`: contributor and agent guidance.

No application source, tests, or assets are present yet. When implementation begins, prefer conventional top-level directories such as `src/` for source code, `tests/` for automated tests, and `assets/` or `public/` for static files.

## Build, Test, and Development Commands

There is no build system configured yet. Use repository inspection commands while working:

- `rg --files`: list tracked project files quickly.
- `git status --short`: check local changes before editing or committing.
- `git diff`: review pending edits.

When a runtime is added, document the exact commands here, for example `npm run dev`, `npm test`, or `make build`.

## Coding Style & Naming Conventions

For Markdown documents:

- Use clear Markdown headings and short paragraphs.
- Prefer numbered lists for ordered processes and bullet lists for grouped facts.
- Use descriptive kebab-case filenames for specs, for example `2026-05-22-bonded-warehouse-ship-drone-delivery-product-spec.md`.
- Keep terminology consistent: use “H5”, “保税仓”, “船舶代理人”, “无人机”, and “海关同步” as established in the current spec.

For future code, follow the language formatter and linter adopted by the project. Do not introduce unrelated formatting churn.

## Testing Guidelines

No automated tests exist yet. For documentation changes, verify:

- Links and file paths are correct.
- Sections do not contain placeholders such as `TODO`, `TBD`, or `待定`.
- Requirements do not contradict the current product spec.

When code is introduced, add tests near the implementation or under `tests/`, and document the test command in this file.

## Commit & Pull Request Guidelines

Recent commits use concise, imperative English summaries, for example:

- `Add bonded warehouse ship delivery product spec`
- `Refine ship delivery product compliance and operations spec`

Continue this style: start with a verb, describe the change, and keep the subject focused.

Pull requests should include:

- A short summary of the change.
- The affected documents or modules.
- Verification performed, such as `rg` checks or test commands.
- Screenshots only when UI changes are introduced.

## Security & Configuration Tips

Do not commit credentials, API keys, private tokens, or real customer data. Product specs may describe sensitive integrations such as 海关接口 or QR token behavior, but should avoid exposing actual endpoints, secrets, or production identifiers.

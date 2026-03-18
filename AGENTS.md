# Code Navigator - Agent Instructions

## Code Structure Principles

### Separate parsing, resolution, and formatting

Code should be structured in distinct layers:

1. **Parsing** — reads raw input (bytecode, files) and produces a data structure. No formatting, no output.
2. **Resolution / tree-building** — takes the parsed data and a query, produces a result data structure (e.g. a tree of nodes). No formatting, no I/O.
3. **Formatting** — takes the result data structure and renders it to text, JSON, etc. No graph walking, no query logic.

Each layer is independently testable. Formatters never reach back into the parsed data to resolve more information — that belongs in the resolution layer. When two formatters (e.g. text and JSON) need the same data, they consume the same result structure rather than independently walking the source data.

### Why this matters

- Bugs are isolated to one layer. If the tree is wrong, it's the resolution layer. If the output looks wrong but the tree is correct, it's the formatter.
- Adding a new output format means writing only a formatter, not duplicating resolution logic.
- Tests for each layer are fast and focused — no need for integration-level setup to test formatting.

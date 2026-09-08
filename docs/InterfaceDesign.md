# Native interface visual target

The approved First Torch concept is the visual target for the native interface. A three-column layout alone does not satisfy it.

## Visual hierarchy

- Use a substantial charcoal frame with layered stone-like bevels and restrained surface variation.
- Centre a prominent warm-gold First Torch wordmark above the content.
- Present chapters as spacious dark cards with an ordinal and a clear gold selection border.
- Render quests as round pixel-edged medallions connected by direct dependency lines. Highlight selection with an amber ring.
- Give the selected quest title and explanation a generous detail panel, with inset prerequisite cards.
- Frame the footer consistently and retain visible keyboard focus and readable text.

Use the reference's roughly quarter-width chapter navigation, central path, and slightly wider detail panel. Adapt spacing and type size for small GUI resolutions without overlaps or clipped controls.

## Content and state

Render the synchronised guide definitions and preserve selection across reloads. Live progress bars, completion marks, locks, tasks, rewards, and tracking actions require server-authoritative state. A separately labelled client-only design preview may demonstrate those visuals with illustrative values; it must never write player progress or grant rewards. The preview fixture is not the migrated curriculum.

Quest nodes have icons, not sequence numbers: branching prerequisites are not a linear checklist. Dependency arrows point into the dependent node and stop outside its rim. Completed nodes use a prominent brass check badge. Omit redundant column headings and use the recovered space for cards and the graph. Preview details separate the objective, illustrated requirements, progress, and reward strip.

Quest medallions are capped at 28 logical pixels in both overview and reading mode. Their item artwork scales with the ring, up to 12 pixels, instead of keeping a fixed 16-pixel item inside increasingly small circles. Material-map regression tests require at least 14 pixels between connected rims, leaving space for the shaft and arrowhead. This does not reduce the large detail illustration, task-row icons or chapter icons. Manual task cards must name the practical exercise explicitly rather than implying that item possession needs manual confirmation.

Constructed neutral borders and medallions are permitted. Any Minecraft item or block imagery must be rendered using original assets from the targeted version. The generated concept itself is not a production texture.

## Visual acceptance

The browser now opens in live mode. Completed badges and task quantities come only from the server's progress observation. Unknown/unavailable progress is not displayed as a known zero. The separate design preview remains reachable through the display toggle. Live details scroll with the mouse wheel and manual confirmation uses the existing server-validated command; no completion is predicted locally. Completed quests expose a Claim rewards button until the server reports the batch claimed. Pending interrupted claims display a review-required state and cannot be retried from the UI.

The experimental reading layout activates only after explicit quest selection. At normal GUI widths it reduces chapters to a 52-pixel icon/number rail and the map to 27% of content width, leaving roughly 65% for details without reducing text size. An Overview button above the map restores the original layout while preserving selection. Selecting a chapter or guide returns to overview. Narrow GUIs retain their original column widths to avoid making the map unusable. Escape keeps its existing close behaviour. This experiment is based on the published checkpoint commit `14b2851`.

Inspect the native screen in both languages at the normal client size and a small GUI resolution. Compare the frame, heading, chapter cards, circular nodes, connecting lines, and detail hierarchy directly against the approved concept. A successful build establishes technical compatibility, not visual acceptance.

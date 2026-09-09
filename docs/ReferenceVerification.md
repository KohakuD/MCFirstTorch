# Reference milestone verification boundary

Status reconciled on 2026-09-09 for native 0.13.0-alpha.1 / Minecraft 26.1.2.

## Completed evidence

- Redstone: all seventeen practical circuit cards have original-asset illustrations. All delivered artwork batches have user acceptance. Material collection and generic bench preparation intentionally do not need circuit drawings.
- Creatures: 35 bilingual cards have recorded loot/source review in `CreatureReferenceSources.md`. Delivered reading batches, including Rabbit and the twelve End/aquatic/special cards, have user acceptance.
- Mechanics: 27 bilingual cards have recorded data/code review in `MechanicsReferenceSources.md`. Delivered reading, sequencing, illustration and navigation batches have user acceptance.
- All 62 reference cards have reviewed stable-ID links to related readings.
- Target-archive creature and mechanics scripts both passed again on 2026-09-09. These checks do not simulate entities or player interactions.

## Remaining verification, not new content

The broad milestone checkboxes deliberately remain open where the evidence is narrower than the requirement:

1. Redstone survival reproduction: explicitly record each illustrated circuit built from its current instructions in survival, including direction, wire range, delay, container state and pulse observations. Reading acceptance is not automatically proof of each physical build.
2. Creature facts: close the remaining behavioural and special-source claims beyond loot-table assertions (living interactions, equipment, transformations and environmental conditions). Preserve the distinction between source review and observed encounters.
3. Mechanics: finish any target-code checks not covered by the archive scripts. The 27-card bilingual trigger/result/danger/safe-experiment audit is complete; see `MechanicsReferenceSources.md`. Never require Anchor explosions, forced infection or death tests.

No additional reading playtest is required solely for this documentation reconciliation. Subsequent changes to player-facing instructions need their own focused acceptance.

## Repeatable archive checks

Run both scripts against the same original 26.1.2 client JAR:

```powershell
pwsh ./tools/verify-creature-loot.ps1 -MinecraftJar <target-client.jar>
pwsh ./tools/verify-mechanics-data.ps1 -MinecraftJar <target-client.jar>
```

Do not copy the game archive, worlds or player progress into Git.

## Versioned reference maintenance

Both PowerShell archive verifiers call `assert-reference-game-version.ps1` before reading facts. Its explicitly reviewed baseline is 26.1.2; it does not silently follow `gradle.properties`. Missing, malformed or mismatched version metadata fails closed. This is an accidental-version guard, not cryptographic archive authentication. Run `pwsh ./tools/test-reference-game-version.ps1` for five isolated positive/negative fixture checks.

For a deliberate Minecraft upgrade:

1. Preserve chapter/quest/task IDs and existing progress; do not regenerate the catalogue.
2. Review changed loot tables, recipes, tags, relevant behaviour code, structures and original artwork models against the new target. Update both languages together and record evidence in the source-review documents.
3. Update the reviewed baseline only after that comparison, then run both archive verifiers, artwork checks, native tests/build and pack validation.
4. Add focused in-game acceptance entries for changed instructions or behaviour. A new game version does not inherit old physical verification automatically.

This workflow does not close the remaining factual or survival verification listed above.

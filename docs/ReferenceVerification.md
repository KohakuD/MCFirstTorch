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

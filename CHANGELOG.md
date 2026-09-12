# Changelog

All notable player-facing changes are documented here.

## 0.14.0-alpha.1 — Unreleased

- Add a first installable Minecraft 1.21.1 development edition alongside 26.1.2, with separate IntelliJ profiles and target-labelled JARs sharing a Java 21 quest core.
- Port the native browser, original-game renderers, networking, tasks, rewards and persistence to NeoForge 21.1.248 / Java 21. In-game acceptance is still pending.
- Adapt both languages for Iron armour, available items and Netherite lodestones. Ordinary excursions no longer require the advanced lodestone branch. Preserve IDs for retained concepts and rewards; exclude the unavailable Creaking reference.
- Omit fifteen unverified newer-version captures in the backport until target-specific replacements are accepted. Native recipe models remain available.
- The 26.2 edition and returning-learner version filter remain planned and are not included yet.
## 0.13.0-beta.2 — 2026-09-10

- Align accessibility at the bottom right of the header, with the unlocked reference library immediately to its left.

- Remove the design-preview switch from the questbook header.
- Disable test completion and its server command, even with old development launch properties.
- Keep the welcome prompt pending when the questbook is opened before the introduction.

## 0.13.0-beta.1 — 2026-09-10

### Added

- A native, bilingual Minecraft learning course from the first shelter through the
  Nether, the End and independent exploration.
- Automatic and manual learning checks, claimable rewards, trophies, search,
  optional Redstone practice, a creature/mechanics reference library and
  returnable lesson links.
- Native Minecraft-model and screenshot-based instructional illustrations,
  keyboard navigation, quiet surfaces and an enlarged browser view.

### Changed

- First Torch is now distributed as an independent NeoForge mod with no FTB
  runtime dependency.
- The browser masthead now shows only the First Torch brand; guide-selection
  arrows and the guide subtitle were removed. The reference library remains
  available through its bookshelf icon.

### Compatibility

- Minecraft Java `26.1.2`
- NeoForge `26.1.2.84`
- Java `25`

## Earlier development

Earlier alpha work is retained in the Git history. It is not a supported public
release line.

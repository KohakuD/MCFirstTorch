# Native release licensing review

Reviewed 2026-09-10 for `0.13.0-alpha.1`. This is a technical inventory and release
risk assessment, not a legal clearance or a finding of infringement.

## Inventory

- Original First Torch Java, course text and documentation: repository MIT licence.
- Build configuration declares JUnit/Gson as test dependencies; no extra production
  library is bundled. The JAR boundary verifier allows only First Torch classes and
  resources plus metadata, LICENSE and NOTICE. It rejects nested JARs and foreign
  classes. Minecraft and NeoForge remain external required dependencies.
- Native `textures/questpics` contains 106 PNGs. They include illustrations assembled
  from target-game textures/models and owner gameplay screenshots, sometimes graded
  or annotated. Directory placement does not imply original ownership or MIT rights.
- The repository's historical FTB archive is excluded from the native JAR and has
  its own third-party terms; this review does not clear that archive for redistribution.

## Official terms consulted

- [Minecraft EULA](https://www.minecraft.net/en-us/eula), sections on software/content,
  mods and ownership: original mods may be distributed within the stated conditions;
  Minecraft content is not thereby owned by the mod author.
- [Minecraft Usage Guidelines](https://www.minecraft.net/en-us/usage-guidelines),
  All uses and Videos, streams, and screenshots: restrictions and attribution apply,
  with specific permission for gameplay captures. This is not a blanket permission
  to redistribute extracted game textures as a separate asset collection.

## Open publication decision

Do not mark the current raster collection legally cleared solely because no game
JAR is included or because the source code is MIT. A per-image provenance and
permitted-use review remains required. Recipe/circuit images assembled offline from
game assets warrant separate attention from screenshots.

Engineering approach approved by the owner on 2026-09-10: render recipe/circuit
illustrations from the player's installed Minecraft resources at runtime and ship
original layout data, preserving the original visual style. This reduces bundled
game-derived raster data but does not itself establish compliance for every asset.
Review gameplay screenshots, public branding, contact information and disclaimer
separately before publishing. Do not remove existing illustrations without approval.

No external publication or monetization enrolment was performed. Runtime migration
has reached 39 illustrations; see `LiveIllustrations.md`. Historical counts above
describe the reviewed source collection, not the reduced release payload. Recheck
official terms when preparing the actual release.

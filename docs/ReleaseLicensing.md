# Native release licensing review

Reviewed 2026-09-10 for `0.13.0-beta.1`. This is a technical inventory and release
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
has completed all 91 assembled illustrations, with visual acceptance recorded in
`LiveIllustrations.md`. The release retains 15 gameplay captures. Historical counts above
describe the reviewed source collection, not the reduced release payload. Recheck
official terms when preparing the actual release.

## Provenance correction: Village overview

The 15 retained raster resources must not all be described as unmodified gameplay
captures. `tools/generate-overworld-village-guide.py` installs
`tools/artwork/village_overview_generated.png`, not the preserved original
`tools/artwork/village_overview_reference.png`. The playtest log records the prior
approval of this screenshot-derived generated illustration. Its visual acceptance
does not make it an exact capture or settle publication rights.

The original screenshot remains available and was inspected. The owner explicitly
selected the UI-free generated variant over an original-pixel crop with the hotbar
and player hand, so no replacement is planned. This is a presentation decision,
not a legal clearance. Other capture provenance and public branding remain open.

## Release-page requirements

The clean-profile test was accepted on 2026-09-10. Before a public upload, the
owner selected the CurseForge Beta file type. The public repository and shared English
contact form are linked in the listing. The owner-approved voxel logo was created
with the built-in image generation tool on 2026-09-10 and is packaged as
`assets/firsttorch/branding/logo.png`; it is branding, not a lesson illustration.
Its concept is a central torch and an ascending voxel path from beginner to mastery.
The prepared bilingual description contains
the required non-official Minecraft disclaimer. See `CurseForgeUpload.md`; do not
treat this engineering checklist as legal advice or substitute it for a final owner review.

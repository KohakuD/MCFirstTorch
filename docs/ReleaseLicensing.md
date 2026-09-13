# Native release licensing review

Reviewed 2026-09-10 for `0.13.0-beta.1`. This is a technical inventory and release
risk assessment, not a legal clearance or a finding of infringement.

## Inventory

- Original First Torch Java, course text and documentation: MIT in LICENSE-CODE.
- Original visual contributions: CC BY 4.0 in LICENSE-ASSETS.md, approved by the owner on 2026-09-10 to match DistinctCraft and Living Paths. Minecraft and other third-party content is expressly excluded. Existing grants for earlier versions remain unaffected.
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

## 1.21.1 Beta preparation — 2026-09-12

The EULA and Usage Guidelines linked above were consulted again for this release. The screenshot permission and distinction between original mod work and underlying Minecraft content remain relevant. This review does not grant rights to Minecraft content or turn the generated village derivative into an exact gameplay capture.

The current packaged inventory is fifteen course rasters plus the original branding logo. The ninety-one retired offline diagrams are archived outside runtime resources. Native illustrations use installed game resources. Both JAR boundary checks pass; no game JAR, foreign classes, nested dependency JAR or historical FTB material is packaged. The owner approved reuse of the fifteen course images for 1.21.1 and 26.2; the village provenance exception above remains explicit. Original-asset licensing continues to exclude underlying Minecraft content.

The existing prepared listing has project/contact links and the non-official disclaimer. A live check of the actual CurseForge listing and submission remains pending because browser automation cannot initialise. No new publication or platform approval is asserted. This is the current technical review record; historical open items above must not be read as proof that a live listing was checked.

## 26.2 banner — 2026-09-12

The original wide `versions/26.2/src/main/resources/assets/firsttorch/branding/banner.png` was created with the built-in image generator for the owner-requested mod-list header. It is branding, not a lesson illustration or an exact Minecraft asset depiction. The square icon is preserved. Prompt: wide transparent FIRST TORCH wordmark in gold/amber voxel lettering, dark edges, glowing torch and ascending steps; no Minecraft wordmark or subtitle. Original visual contributions follow LICENSE-ASSETS.md; underlying third-party rights remain excluded.

## Per-file provenance register — 2026-09-13

This register records the exact release PNG payload. Attribution follows the existing project record; missing capture dates, original upload filenames and editing histories are not inferred. Owner-approved reuse covers the three targets.

- **Owner capture:** owner-supplied Minecraft gameplay screenshot, attributed to KohakuD in the project record. Original capture date/upload path and prior crop/colour changes are not individually documented. The existing official-terms review above is the recorded usage basis; underlying Minecraft content is excluded from the original-asset licence.
- **Village derivative:** generated from `tools/artwork/village_overview_reference.png`, with output at `tools/artwork/village_overview_generated.png`, installed by `tools/generate-overworld-village-guide.py`. Owner selected the UI-free version. It is not an exact gameplay capture; the existing publication-rights question remains unresolved.
- **Original branding:** generated for KohakuD with the built-in image tool and approved by the owner. Logo: 2026-09-10; banner: 2026-09-12. Concepts are recorded above. Original contributions follow CC BY 4.0, excluding third-party content.

| Packaged file under assets/firsttorch/ | Source/editing record | Targets | SHA-256 |
| --- | --- | --- | --- |
| `branding/banner.png` | Original branding | 26.2 | `3576675c3af0fe7e72b408c2ad8ca064c158d6f5031664e4502ff7ed2d9e903c` |
| `branding/logo.png` | Original branding | 1.21.1, 26.1.2, 26.2 | `1448789b7362182467924ea195a291bb1ab2e4746ca2f1673c44efa422fa7228` |
| `textures/questpics/blaze_spawner.png` | Owner capture | 1.21.1, 26.1.2, 26.2 | `e750fff412fb44e7c0eb2bad81b7d5191a5f78a42e551af1538b5f208ac245f6` |
| `textures/questpics/chorus_harvest.png` | Owner capture | 1.21.1, 26.1.2, 26.2 | `76de0f2da247382cdc78aae9f48f47a4d68f2b1fa14f2486a9cc55034838bb27` |
| `textures/questpics/end_arrival_platform_capture.png` | Owner capture | 1.21.1, 26.1.2, 26.2 | `c76591e9b0b6819c7ca6e92109d98812d87c3498f63f12db50c068df3cd135d2` |
| `textures/questpics/end_battlefield.png` | Owner capture | 1.21.1, 26.1.2, 26.2 | `7b9df412131a98b970ae6b84600fd019440d9f92a4f1337248fd79e2f08acdce` |
| `textures/questpics/end_city_search.png` | Owner capture | 1.21.1, 26.1.2, 26.2 | `510ff8c51566a46b13c27694fc2c7bc115f78de4a87b2d17da72bc8263b3049f` |
| `textures/questpics/end_exit_portal.png` | Owner capture | 1.21.1, 26.1.2, 26.2 | `c4abc7253a389f13438e814c6d99b02e2033c51f5200b9fefa6fc3bcdb78daee` |
| `textures/questpics/end_gateway_access.png` | Owner capture | 1.21.1, 26.1.2, 26.2 | `e62c95fcf9b29bf34f8361e32b4a78fa49a4197e2ae256416dbf88b7f652455e` |
| `textures/questpics/end_island_crossing.png` | Owner capture | 1.21.1, 26.1.2, 26.2 | `28aab52b54a49ef60e598c72152d0d98767813aad446eb62142ea62f7b7f9487` |
| `textures/questpics/end_portal_active_capture.png` | Owner capture | 1.21.1, 26.1.2, 26.2 | `94af465eaa9dd74fde61664af61b8690ed96925cb1fc658244640cfca8d298aa` |
| `textures/questpics/end_portal_room_capture.png` | Owner capture | 1.21.1, 26.1.2, 26.2 | `871e7e84a59c95c7dcca8feb985079cbe36a8dbb6e1d648573f5a04f81c9c789` |
| `textures/questpics/nether_fortress.png` | Owner capture | 1.21.1, 26.1.2, 26.2 | `a71db58beb7d8b1c85afc9f282b13e4dbe35ff24251fa78b229e3c2096d62601` |
| `textures/questpics/nether_wart_garden.png` | Owner capture | 1.21.1, 26.1.2, 26.2 | `aca37adaa40284bf95ca2368fcb3c7e788a540d211a3f107586eac448d6d553e` |
| `textures/questpics/pumpkin_view_capture.png` | Owner capture | 1.21.1, 26.1.2, 26.2 | `cde36695e0c6cd38b3f42146047d1fbadd37e329df9b5ad5b118f813fdad8db0` |
| `textures/questpics/stronghold_iron_door_capture.png` | Owner capture | 1.21.1, 26.1.2, 26.2 | `1415287bd76585288141f0cca7d6c9d8d0cfcea20903917d97ac177b4308201c` |
| `textures/questpics/village_overview.png` | Village derivative | 1.21.1, 26.1.2, 26.2 | `328dfb38aa4be82510aaee449aabb8479d78c02ec064f287b95471eb04472fce` |

The 91 runtime recipe/circuit illustrations use installed Minecraft resources and First Torch layout instructions; retired offline diagrams are not packaged. Missing individual capture/edit details remain undocumented rather than presumed complete.

On 2026-09-13 the owner confirmed all three current packages were uploaded and are undergoing platform review. File IDs and public availability are not independently confirmed; platform review is separate from provenance documentation.

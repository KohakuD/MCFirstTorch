# Clean-profile release acceptance

This is the final local release smoke test for `0.13.0-alpha.1`. It verifies a
fresh installation, not an upgrade and not a replacement for the intentionally
waived dedicated-server test.

## Preparation

1. Close Minecraft completely.
2. Create a separate CurseForge profile with Minecraft `26.1.2`, NeoForge
   `26.1.2.84` and Java `25`.
3. Copy only `firsttorch-0.13.0-alpha.1.jar` into that profile's `mods` folder.
   Do not copy development folders, resource packs, worlds or an old First Torch
   JAR.
4. Start a new single-player world and keep a second new world available for a
   restart/persistence check.

## Acceptance pass

1. Confirm the Mods screen lists First Torch and no FTB mod is required.
2. Start each new world. The welcome prompt appears once per player/world; choose
   Later in one world, restart it, and confirm that the prompt does not repeat.
3. In the other world, open First Torch with the key below Escape or from the
   pause-menu entry. Confirm German and English text, the native browser, search,
   images, reference library and settings open without errors.
4. Complete a small early path and claim one reward. Restart the world and confirm
   quest completion, reward claim state and XP/inventory are still present.
5. Build version, Minecraft version and NeoForge version must match the values
   above. Keep the resulting JAR unchanged for the later CurseForge upload.

The development-only completion control is absent from an installed JAR. This is
expected. Do not use a test world or a copied save as proof of a clean profile.

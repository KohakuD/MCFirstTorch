# Archived native illustrations

These 91 PNGs were superseded by native Minecraft item/block/entity and GUI renderers. They are retained unchanged for reference and provenance, outside all resource and packaging roots. This is separate from the retired FTB pack archive.

- `questpics/`: historical raster diagrams; not loaded or packaged by First Torch.
- Active screenshots remain in `src/main/resources/assets/firsttorch/textures/questpics/` (15 owner-approved files).
- Historical diagram generators under `tools/generate-*.py` now write here. Their target-specific asset requirements still apply; they are not part of the build. Screenshot preparation tools keep their active destinations.
- Existing `firsttorch:textures/questpics/...` identifiers remain in quest definitions and renderer catalogs: they select the live renderer and must not be rewritten to archive paths.
- Existing repository licences and attribution apply. Archiving does not grant new asset rights.

The move was verified with SHA-256 before and after each file. No image was regenerated or removed.
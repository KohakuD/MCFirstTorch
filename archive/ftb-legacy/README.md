# Archived FTB pack and abandoned import research

Archived on 2026-09-10 at the owner's request. This directory is historical material,
not part of the active First Torch mod. No FTB importer is planned.

- `manifest.json` and `overrides/`: frozen 0.9.1 pack, including its original guide
  resource pack. The active mod owns its own resources under `src/main/resources`.
- `tools/`: retired pack validation/build/instance-update scripts and abandoned
  read-only import-key research. None are run by active CI or the native build.
- `docs/`: former installation instructions, migration research and historical
  Roadmap/Development/Curriculum snapshots. Their checklists and paths describe the
  previous project layout and must not be treated as current instructions.
- `docs/RepositoryReadme.md`: previous repository overview, for historical context.

To inspect the old pack, use this directory as its root. The archived validator and
pack builder resolve their inputs relative to `tools/` and may be run explicitly:

```powershell
pwsh ./archive/ftb-legacy/tools/validate-pack.ps1
pwsh ./archive/ftb-legacy/tools/build-pack.ps1
```

Output stays in this archive's ignored `build/` directory. These commands build an
FTB launcher pack, **not** the native mod. Do not run the archived instance updater
against a native profile. No installed profiles or player saves were moved here.

For exact pre-archive history, inspect Git commit `970d92a`. Do not restore the whole
repository over current work to retrieve one historical file. Active development
instructions live in the repository-root README and AGENTS files.

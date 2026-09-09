# FTB-to-native progress migration design

Status: design only. No importer, migration command or automatic world conversion exists.
Installing the native JAR does not import FTB progress. Existing native saves require no
FTB migration. Do not run a migration against the owner's current native test world.

## Verified native boundary

- `WorldProgressData` stores version-1 completion sets by canonical player UUID.
  Tasks and quests use stable 16-character uppercase hexadecimal IDs. Invalid,
  duplicate or unsupported-version records fail decoding without partial recovery.
- `ServerProgressRepository` refuses to replace an existing unreadable save and
  preserves historical IDs when merging evaluated progress.
- `RewardJournal` is separate from completion storage. It reserves whole-quest
  payouts before delivery and distinguishes pending from complete transactions.
  Pending payouts must not be retried automatically.
- Native reward claims are whole-quest operations. An FTB record of an individual
  reward being claimed cannot simply become a native unclaimed quest.
- Native progress is per player; shared native team progress is not implemented.

These facts are established by the current repository, not by an inspection of an
FTB player-save format. The retained JSON5 files are lesson definitions, not proof
of where or how the pinned FTB version stores player/team progress or reward claims.

## First supported scope

Target the retained First Torch 0.9.1 definitions and the pinned FTB versions only.
Use an explicit offline conversion of a copied world, never a login-time import.
Keep the source world untouched and keep FTB dependencies out of the native runtime.
The first implementation should support one explicitly mapped source identity and
one target UUID; team fan-out requires a separately reviewed policy.

Before implementing the source adapter, create disposable FTB test saves covering:
an untouched quest, partial task counts, completed tasks, completed quests, unclaimed
rewards, partially claimed rewards, fully claimed rewards, two team members and a
team membership change. Inspect the pinned FTB implementation and compare these
saves across a clean shutdown/reopen. Document the actual paths, schema and ownership
semantics. Do not guess them from older FTB releases or names in a directory.

## Proposed workflow

1. Stop both clients and servers. Back up the complete source and destination worlds,
   including player data, native completion data and reward journals. Record hashes.
2. Read the source without modifying it. Validate version, record sizes, IDs,
   ownership and reward evidence; reject malformed or ambiguous records.
3. Generate a dry-run report with source/target UUIDs, definition fingerprints,
   matched/unmatched IDs, task changes, proposed completions and reward conflicts.
   Never identify players by display name alone.
4. Require explicit owner approval of identity mapping and every unresolved conflict.
   An unresolved team or reward record blocks application, not the read-only report.
5. Build and validate a staged destination with a migration receipt. Bind the receipt
   to the input hashes, target UUID, converter version and exact proposed changes.
6. Apply only to the stopped copied world. Completion data and reward state must be
   installed as one recoverable transaction. A partial transaction must prevent the
   destination from loading until recovery or rollback completes.
7. Reopen the copy and verify lesson states, rewards, isolation and reconnect behaviour.
   Keep the original world and complete backup until the owner accepts the result.

The current runtime has no migration-transaction guard or migration receipt support.
Those are prerequisites for an apply mode, not guarantees provided by this document.

## Completion mapping

- Match stable IDs and reviewed task semantics, never chapter order or translated titles.
- Preserve existing native completion history; do not replace it with the source sets.
- Native-only lessons remain as they were. Unknown source IDs appear in the report
  and retained receipt, rather than being silently discarded or activated.
- A matching quest ID alone does not prove matching task requirements. Changed or
  split tasks need an explicit reviewed mapping; otherwise leave them unresolved.
- Import only verified completed tasks. Native completion sets cannot represent an
  old partial numeric count. Report that limitation; current inventory observations
  may subsequently update live quantities through ordinary runtime evaluation.
- Do not invent prerequisite completions to make an imported quest claimable.
  Completion and reward eligibility remain separate, including early-recorded tasks.

## Reward policy

Migration itself grants no items or XP and does not replay completion celebrations.
Preserve native pending/complete claims; conflicts with source evidence block apply.

For an unchanged reward set, verified fully claimed source rewards may map to an
explicit migrated-claimed state. Verified wholly unclaimed rewards may remain
claimable only after ordinary native completion and prerequisite checks pass.
Partial claims, unknown ownership, changed reward sets or missing claim evidence
must not silently become fully claimable or fully claimed. Report them for review.

A future implementation needs provenance-aware imported claim records (or a reviewed
equivalent) and tests. Do not hand-write ordinary payout journal files to pretend
that the native server delivered historical rewards. Do not use a completion reset
or the development completion button as a migration mechanism.

## Required verification before shipping

- Pinned-version source fixtures and explicit unsupported-version rejection.
- Stable-ID/semantic mapping, unknown IDs, changed tasks and partial counts.
- UUID mismatch, teams and two-player isolation; no implicit team-to-everyone copy.
- All reward cases above, including changed amounts and existing pending claims.
- Repeating the same accepted migration is a no-op; changed inputs invalidate approval.
- Failure injection between every write, corruption rejection and full rollback.
- No source writes; unrelated destination data remains byte-for-byte unchanged.
- Copied-world playtest in both languages, claim/restart and a second-player check.

Only after these checks should documentation offer an actual migration command.

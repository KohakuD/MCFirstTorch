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

These native facts are established by the current repository. The initial pinned
FTB inspection below adds source-format evidence, but not full migration validation.
The retained quest-definition JSON5 files are not player progress.

## Pinned FTB inspection (2026-09-09)

Read-only inspection used the locally installed `ftb-quests-neoforge-26.1.2.7.jar`:
SHA-256 `2A45C5E220885CC5D0EE757BC8BEDC3B6AF6B82015DD851BA5C0027CF3ACD32B`.
`javap -p -c -classpath <jar> <class>` inspected these classes without running
Minecraft or loading the mod. One existing retained-profile progress file was read
to corroborate the representation; no player identifiers or save contents are
copied into Git. This sample is not a controlled test fixture.

| Evidence | Confirmed behaviour |
| --- | --- |
| `ServerQuestFile` static initialiser; `TeamData.saveIfChanged` | World-relative `ftbquests` directory; team UUID determines the file. The observed file has a dashed UUID filename and `.json5` extension. |
| `TeamData.toJson` | Writes version `1`, undashed team `uuid`, `name`, `lock`, `rewards_blocked`, `task_progress`, `started`, `completed`, `repeatable`, `completion_count`, `claimed_rewards`, `player_data`. |
| `FTBQCodecs` static initialiser | Progress/start/completion/repeatable maps use long keys and long values; completion counts use long keys and integer values. |
| Existing sample | Task and completion map keys are decimal strings, not the hexadecimal IDs used in native definitions. Preserve exact 64-bit integers; never convert through floating-point numbers. |
| `TeamData.getCompletedTime` / `getRewardClaimTime` | Completion/claim timestamps are distinct from task quantities. Claim time zero is treated as absent. |
| `QuestKey.forReward` | Team rewards use the nil UUID in their claim key; other rewards use the supplied player UUID. The key's ID is the reward ID, not the quest ID. |
| `QuestKey.fromString` | Reads an undashed UUID from the first 32 characters and an object code beginning at character 33. A strict adapter must also verify the separator and canonical code format before accepting a key. |

The compressed JSON codec operations are logical serialization choices, not evidence
that the `.json5` file is a compressed binary archive. The sample is ordinary text.
Do not treat every entry in `completed` as a quest: classify IDs against definitions;
the data model records completion of quest objects, not just native quest records.

Remaining source work: finish FTB Teams ownership/membership persistence checks,
definition reward flags and load/error behaviour. Then generate
the controlled fixtures below. In particular, a nil-UUID team claim must not be
mistaken for an unknown player or automatically copied to every target player.

### Key conversion and team follow-up (2026-09-10)

`QuestKey`'s string-concatenation recipe confirms a colon separator.
`QuestObjectBase.getCodeString(long)` uses `%016X`. The canonical claim key is
therefore an undashed lowercase UUID, colon and 16-character uppercase reward code.

`tools/ftb_progress_keys.py` implements only this key inspection and exact decimal
object-ID conversion using Python's standard library. It accepts explicit strings,
prints JSON to stdout and has no filesystem read/write or import operation.
Noncanonical keys and IDs outside the native signed-long range are rejected rather
than silently repaired. A decoded key does not prove membership or reward eligibility.
Object IDs still need classification against the definitions, including reserved IDs.

```powershell
python -B tools/ftb_progress_keys.py decimal-id 9007199254740993
python -B tools/ftb_progress_keys.py claim-key 00000000000000000000000000000000:10A0B0C0D0E00001
python -B -m unittest discover -s tools -p test_ftb_progress_keys.py
```

Six synthetic tests cover precision beyond 2^53, signed-long boundaries, malformed
input, personal claims and nil-UUID team claims. They do not validate a JSON5 reader
or a complete source save. No complete migration dry-run report exists yet.

The locally inspected `ftb-teams-neoforge-26.1.2.4.jar` has SHA-256
`7A467DF980BBB9D051CCB6AA96D40548E776886BD9DC8D5B2684CD5FFAC76F0C`.
`TeamManagerImpl` uses the world-relative `ftbteams` resource. `AbstractTeam.toJson`
includes `id`, `type` and `ranks`. `PlayerTeam` adds `player_name` and maintains an
effective team separately; `TeamManagerImpl.loadAllTeams` calls `setEffectiveTeam`
after loading teams and known players. `AbstractTeamBase.getMembers` uses ranks.
Thus a player-name field or personal-team filename is insufficient proof of the
active progress owner. Exact rank filtering, conflicting memberships and party
join/leave progress transfer still require code review and controlled fixtures.

### Party transition audit and scope decision (2026-09-10)

The pinned Quests binary's `FTBQuestsEventHandler.playerChangedTeam` delegates to
`ServerQuestFile.playerChangedTeam`. When the previous team is personal, the new
team is a party and the player is not its owner, the latter calls
`newData.mergeData(previousData)`. That method processes task progress, started and
completed maps, claims and per-player data. The owner case does not enter this
branch; party creation must be reviewed separately before generalising it.

For a party-to-personal transition, this handler calls only
`newData.mergeClaimedRewards(previousData)`, not `mergeData`. It then synchronises
team data. This is bytecode-path evidence, not a controlled join/leave playtest or
a claim that no other event handler can affect state.

The retained book sets `default_reward_team: false`; this default does not remove
the need to inspect per-reward overrides and historical claim records.

**Owner decision pending:** should the first supported importer be limited to
verified personal progress, or also assign shared party progress to native players?
The recommended first scope is personal progress only, explicitly rejecting active
party sources and unresolved membership/history rather than importing stale personal
files. Shared-progress import needs a policy for recipients, personal versus shared
claims and existing native progress. It must not be enabled by inferring ownership
from the party owner's UUID. Native shared team progression remains a separate feature.

No apply mode or automatic party-to-player mapping has been implemented.

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

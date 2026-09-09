# Native progress safety review

Reviewed for `0.13.0-alpha.1` on 2026-09-10. LAN smoke acceptance is recorded in
`MultiplayerAcceptance.md`. Dedicated-server testing is waived, not passed.

## Backup and restore boundary

Close the world and Minecraft cleanly before copying the entire world directory.
Keep the original backup unchanged and test recovery using a separate copy.
Restore the complete world from the same snapshot, including player inventories,
experience, advancement data and all First Torch data. Do not mix newer player
files with older quest data or restore only the reward journal.

First Torch uses server-global SavedData identifiers `firsttorch:progress` and
`firsttorch:welcome`, and `data/firsttorch/reward_claims/<player UUID>/` for reward
reservations/completions. Copying only the mod JAR does not copy player progress.

`RewardClaims` writes a forced, create-new reservation before mutating inventory
or XP, then writes completion. Interrupted reservations block automatic retry.
This prevents repeat payout attempts but is not an atomic transaction with
Minecraft's player save: a crash can lose an unsaved payout even when its journal
is complete. Preserve the world and journal for diagnosis; never delete claim files
to retry an uncertain payout. Whole-world backups are the recovery boundary.

Automated journal-copy coverage verifies completed and pending records survive a
copy and still block repeats. It does not simulate full-world crash recovery.

## Version changes and identity

- Keep stable quest/task/reward IDs. `ServerProgressRepository.remember` retains
  historical completions when definitions are temporarily absent.
- Changing rewards under an already-claimed quest ID does not grant a second payout.
- Progress/welcome codecs require schema version 1; the journal requires its known
  header. Unsupported or corrupt existing data is rejected, not silently reset.
- Before an upgrade, save a complete stopped-world backup and retain the previous
  JAR. Test the new version on a copy; do not downgrade an upgraded world in place.
- State is world- and UUID-specific. Different accounts are separate learners.
  No scoreboard-team sharing, automatic merging or future team migration is promised.
  Any future team system needs an explicit policy before implementation.

## Death and respawn

Source review found no death-triggered deletion: progress and welcome are UUID
keyed, and reward journals use the same UUID after player-entity replacement.
`ServerTaskEvents` checks/synchronises every 20 player ticks. There is no dedicated
respawn handler; allow about one second for the new entity's first refresh.
Completed observations remain completed; ordinary Minecraft item/XP loss is not
undone by First Torch and does not make an already claimed reward claimable again.

Pending in-game acceptance, using a disposable world (no reset of the main world):

1. Complete one quest and claim its reward; complete another but leave its reward
   unclaimed. Note both states and, in LAN, the second player's state.
2. Die and respawn. After about one second, both completions must remain; only the
   previously unclaimed reward may be claimed. The second player must be unchanged.
3. Claim that reward once, reconnect and check both claims remain unavailable.
   An acknowledged welcome must not reappear.

An unacknowledged welcome interrupted by death remains an additional edge case:
the client normally retains its pending prompt, but no respawn-specific re-offer
exists. This needs a separate interruption test, not an assumed pass.

Broader interrupted-save/reload and actual whole-world restore tests remain open.

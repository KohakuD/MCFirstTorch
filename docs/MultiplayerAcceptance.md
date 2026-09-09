# Two-client native smoke test

Status: partially accepted on 2026-09-10. The user confirmed independent welcomes
and separate quest progress with two accounts using the supplied native test JAR.
Host type was not specified. Reward recipients, duplicate-claim prevention,
reconnect/restart persistence and claim-all isolation remain unconfirmed.
Storage tests are not a substitute for this network/player test.

Use a disposable world and two distinct player accounts, both with the same native
First Torch build, Minecraft 26.1.2 and NeoForge 26.1.2.84. Do not reset the owner's
existing world. Two windows using the same account do not test player isolation.
Begin on LAN if convenient; record that result separately from a dedicated server.
Do not use the development force-completion control for this test.

1. Player A enters first and dismisses the welcome. Player B joins afterwards:
   B must still receive their own welcome. Dismiss B's welcome too.
2. Both open First Torch. A confirms the first Welcome reading task. B's copy must
   remain incomplete, with no completion notice or unlock caused by A's action.
3. A claims that quest's reward. Record A/B XP before and after: only A receives
   the defined reward. Repeating A's claim must not pay again.
4. B independently confirms and claims the same quest. Only B receives its reward;
   A's completion and claim state remain unchanged.
5. Disconnect and reconnect each player. Neither welcome repeats; both completions
   and claims persist. Then stop the host cleanly, reopen and repeat the checks.
6. When two rewards are independently available, check the title-bar claim-all chest:
   it must use only the clicking player's eligible rewards and inventory space.

Record build, LAN/dedicated host, both outcomes and any console/log errors. Report
UUIDs privately only if diagnosis needs them; never commit player saves or logs.
Dedicated-server startup/restart and broader fault/reload/death tests remain separate
release gates even if the LAN smoke test passes.

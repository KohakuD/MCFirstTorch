# Redstone practical acceptance

The optional Redstone route is technically verified against Minecraft `26.1.2`.
The remaining acceptance is a single in-game practical pass in German and English.
Use a Creative test world if gathering the materials would distract from checking
the explanations. The development completion button may finish a card afterwards,
but must not be used as proof that the circuit worked.

## One practical pass

1. **Basics:** place a Lever, Redstone Dust and Redstone Lamp. Confirm that the
   short line works; then make a longer line and see the signal stop after 15 dust.
   Compare Lever, Button and Pressure Plate inputs.
2. **Repeaters:** build a one-Repeater line. Check its arrow faces from input to
   output, renews the signal after 15 dust, and that the selected delay is visible.
3. **Comparators:** point a Comparator out of a Chest. Compare an empty chest with
   a partly and more fully filled chest, and confirm the displayed lamp response.
4. **Pistons:** push a block with a Piston, then use a Sticky Piston to pull it
   back. Check the illustrated front direction before placing either one.
5. **Observers:** place the face towards a changing block, then observe its brief
   output-side pulse on a Lamp. Confirm face and output side are not confused.
6. **Iron Door:** place an Iron Door with one Stone Pressure Plate on each side.
   Walk through both directions and confirm it closes safely after leaving the
   plate.

For each chapter, check its instructions, diagram and manual confirmation in both
languages. Report only a wrong result, unclear direction, missing artwork or a
completion/reward issue; no screenshots, survival gathering, progress reset or
death test is required.

## Technical evidence

`RedstoneBasicsTest`, `RedstoneSignalsTest`, `RedstoneMotionTest`,
`RedstoneDoorTest` and `RedstoneArtworkCoverageTest` passed on 2026-09-10.
They verify gating, automatic-material versus manual-observation boundaries,
target container signal behaviour, Observer output axis, 12-block piston limit,
door material requirements, and artwork coverage. They do not simulate a player's
world build.

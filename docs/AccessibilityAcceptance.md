# Native accessibility acceptance

Status: source review complete for search, reference index and native buttons;
in-game keyboard/narrator acceptance pending for `0.13.0-alpha.1`.

Buttons inherit native input and narration and draw hover/focus using the same
visible selection treatment. Search initially focuses its EditBox, accepts Enter,
retains focus after no results, and now immediately narrates the no-results message.
The reference index supports Page Up/Down and Home/End; its first visible row gets
focus after scrolling. Accessibility options use Minecraft's option widgets.
These are code observations, not proof of screen-reader or visual usability.

## Combined in-game check

Use the current build and an existing test world with the reference library unlocked.
No progress reset is required. Enable Minecraft's narrator for menus through
Accessibility, then test without mouse clicks:

1. Open First Torch. Tab and Shift+Tab move a visible focus indicator through
   controls; icon-only controls announce their function. Enter activates them.
2. Open Search. Type several characters continuously, search for an absent term,
   and hear the no-results message. Correct the term without clicking; Enter opens
   a result. Escape from Search returns to the quest book.
3. Open the Bookshelf reference index. Use Page Down/Up and Home/End, select a
   chapter with Enter, and verify Escape returns without losing control.
4. Open trophies and return; open accessibility/settings and return. No keyboard
   trap should require the mouse. Toggle enlarged view and quiet surfaces and
   confirm focused controls remain visible and readable.
5. Check an open, locked and completed quest: narration distinguishes their state.
   Repeat the checks in German and English, noting any untranslated keys or clipping.

The test does not require replaying the course or reclaiming rewards. Record any
unavailable narration (for example missing system speech support) as untested,
not passed. Player preferences can be restored after the check.

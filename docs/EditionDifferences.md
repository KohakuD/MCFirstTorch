# Edition differences

Initial comparison: Minecraft 1.21.1 to 26.1.2. Minecraft target order is independent of First Torch publication dates. This is a reviewed seed for the returning-learner history, not an implemented filter or a claim that every Minecraft change has a lesson.

## Availability

| Quest ID | Lesson | 1.21.1 | 26.1.2 |
| --- | --- | --- | --- |
| `1CA0B0C0D0E00003` | Creaking | Unavailable; excluded | Available |

The shared Trial Chambers chapter `70A7B8C9D0E1F203` retains Breeze and Bogged in both editions. Its newer title/description also mentions the Pale Garden; the chapter is not itself a newly introduced lesson.

## Substantive revisions to retained lessons

| Quest IDs | Difference in the newer edition | Comparison treatment |
| --- | --- | --- |
| `4A28C6E10D735BF9`, `6C4AE8F31D957B20` | Copper becomes a route to early armour instead of only the older building/Brush uses | Revised lessons |
| `18A6D3F90C754BE2` | Copper armour recipes/material option and Copper-or-Iron entry prerequisite; the older edition illustrates Iron and requires Iron | Revised lesson, same completion/reward identity |
| `59CBED086F24A137` | Lodestone costs Iron instead of Netherite | Revised recipe lesson |
| `15E7C9A42B806DF3`, `12E8A5C74F309BD6` | The Lodestone branch can sit earlier in the route; in 1.21.1 it is optional and excursions require the ordinary Compass | Changed sequencing; provide prerequisite context instead of presenting Stonecutter crafting as new |
| `38D24F61E9DA3570` | Pale Oak is an additional valid boat material | Revised available-material information |
| `4BBE2FCA33A44E31` | Copper Chests join the guarded-container examples | Revised safety reference |

## Supporting differences that must not become false new lessons

- `1A62D8E30C745BF9`, `49CBF6082DA57E13`, `34FAC7E96152BDF8`: Copper tool references and expanded item tags support the equipment changes. Decide whether to link these as context when designing the comparison view; they do not teach an entirely new mining or packing system.
- `0BED012A8146C359`: the Lodestone advancement changes from `minecraft:nether/use_lodestone` to `minecraft:adventure/use_lodestone`. The observation adapter change alone is not a new learner action.
- `6D91380C6EA4B2F5`: an ordinary Chest replaces the newer Bundle reward in the backport while retaining reward ID `5AEFB70D418269C3`. A reward substitution alone must not re-complete the quest or grant an extra claim.
- Bundle-to-Chest icons, removal of a Creaking reference link, changed chapter/trophy captions, explicit version wording in the Shulker explanation (`6FEC02D106FBA924`) and screenshot reuse are presentation/compatibility differences, not novelty events.
- `0B875B8819CB3C72`: the newer Shulker Box explanation also warns that a Copper Chest cannot substitute for an ordinary Chest. The actual crafting recipe is unchanged.

## Remaining implementation

Translate this record into explicit per-lesson target availability and substantive revision metadata when implementing the version filter. Do not derive novelty from arbitrary JSON/text diffs, modified dates, screenshots or reward changes. The cumulative 1.21.1-to-26.2 view will combine these reviewed changes with the separately reviewed 26.2 additions, deduplicate quest IDs, and retain one progress/reward state.
## Minecraft 26.2 additions

The optional Sulfur Caves reference chapter `7A26200000000001` introduces five genuinely new lessons, available only in 26.2:

| Quest ID | Topic |
| --- | --- |
| `2A26200000000001` | Recognising Sulfur Caves and preserving an exit |
| `2A26200000000002` | Avoiding sulfur gas and geysers |
| `2A26200000000003` | Hanging Sulfur Spike hazards |
| `2A26200000000004` | Caution around absorbed Sulfur Cube blocks |
| `2A26200000000005` | Optional Sulfur/Cinnabar building materials |

These are reading confirmations, not completed physical experiments. Each unlocks after the existing reference introduction; none gates existing course quests or grants items. The future 1.21.1-to-26.2 comparison includes these IDs alongside the substantive 26.1.2 revisions above. The comparison filter itself is not yet implemented.

The existing reading introduction `32B4C6D8E0F21357` gains substantive 26.2 Friends List guidance (menu/default key/settings and personal quest progress). Record it as a revised lesson for the future comparison, not a new quest ID.

## Implemented comparison core

`FirstTorchEditionHistory` records the reviewed semantic changes in the shared Java 21 core. `EditionHistory` uses explicit Minecraft ordering, accepts publication eligibility from the future runtime catalogue, intersects changes with the loaded target quests, deduplicates repeated revisions, and returns separate changed/context ID sets. Prerequisite closure includes ALL/ANY alternatives as navigable background; it never grants completion or rewards. No filtered GuideSnapshot is constructed, so original validation and server eligibility remain intact.

The tested 1.21.1-to-26.2 selection contains 13 changed lessons; 26.1.2-to-26.2 contains six (five sulfur cards plus the revised reading introduction). Recipe/route context, reward substitutions and cosmetic fixes do not count as new lessons. UI integration, current-target detection, published baseline catalogue, per-player preference storage and accessible prerequisite navigation remain outstanding.

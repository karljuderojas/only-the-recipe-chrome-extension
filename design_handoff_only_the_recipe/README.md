# Handoff: Only The Recipe — Cream & Terracotta UI

## Overview

This is a complete UI redesign for **Only The Recipe**, an Android app that strips SEO noise from recipe pages and saves a personal recipe library on-device. The redesign replaces the current Material-blue/Roboto interface with an editorial, cookbook-inspired aesthetic targeted at young adults and home cooks.

Five screens are designed end-to-end: **Library, Recipe Detail, Grocery List, Empty State, Settings.** The information architecture (bottom-tab nav between Recipes and Grocery, paste-URL → extract flow, save/share/notes per recipe) matches the existing app — only the visual layer changes.

## About the Design Files

The files under `design/` are **design references created in HTML/React** — prototypes showing intended look and behavior, **not production code to copy directly**.

The task is to **recreate these designs inside the existing Android app** (`only-the-recipe/android/`) using its native Java/Kotlin + Android view system. The current app uses Material Components; you'll be replacing those theme colors, typography, and several screen layouts. Do not introduce React, web views, or new third-party UI libraries — keep it native Android.

Open `design/Only The Recipe.html` in a browser to see all five screens at full size; pan/zoom the canvas, click any artboard title or the expand icon for fullscreen view. The "Archived directions" section below the primary set contains two earlier explorations (Swiss, Recipe-book) — for context only, do not implement them.

## Fidelity

**High-fidelity.** Exact colors, type sizes, spacing, and copy are specified in this README. Match them pixel-perfectly. Where this README and the HTML disagree, the README wins.

## Design Tokens

All values are exact. Use them as the basis for a theme/resource file (e.g. `colors.xml`, `dimens.xml`, `type.xml`).

### Colors

| Token | Hex | Usage |
|---|---|---|
| `bg` | `#FAF8F3` | App background (cream paper) |
| `paper` | `#F3EFE5` | Hero placeholder / secondary surface |
| `ink` | `#1F1A14` | Primary text |
| `muted` | `#8A7F6D` | Secondary text, icons, meta |
| `rule` | `#E6E0D0` | Hairline dividers, borders |
| `accent` | `#C2602F` | Terracotta — primary accent, links, CTAs, active tab |
| `accentSoft` | `#F1DCCC` | Note card background |
| `noteInk` | `#5A3520` | Text inside the note card |
| `surface` | `#FFFFFF` | URL input field background |

Status bar tint matches `bg` (`#FAF8F3`) with dark icons.

### Typography

Three families. Self-host the variable fonts under `res/font/` and register in `font` resources.

| Family | Source | Weights used |
|---|---|---|
| **Cormorant Garamond** | Google Fonts | 500 regular, 500 italic, 600 italic |
| **Manrope** | Google Fonts | 400, 500, 600, 700 |
| **JetBrains Mono** | Google Fonts | 400, 500, 600, 700 |

#### Type scale (sp on Android — values below are px from the design; treat 1:1 as sp)

| Role | Family | Size | Weight | Style | Line height | Letter-spacing |
|---|---|---|---|---|---|---|
| Display (Library/Settings/Grocery titles) | Cormorant Garamond | 40 | 500 | italic | 1.0 (≈40) | -1.0 px (about -0.025 em) |
| Display small (Recipe title) | Cormorant Garamond | 30 | 500 | normal | 1.05 | -0.5 px |
| Heading (Empty state title, "Your shelf is bare") | Cormorant Garamond | 22 | 500 | italic | 1.15 | 0 |
| Section label ("Saved", "Ingredients", "Method") | Cormorant Garamond | 18 | 500 | italic | 1.0 | 0 |
| Recipe row title | Cormorant Garamond | 19 | 500 | normal | 1.15 | 0 |
| Settings row label | Cormorant Garamond | 16 | 500 | normal | 1.2 | 0 |
| Timing value (Prep/Cook/Total numbers) | Cormorant Garamond | 16 | 500 | normal | 1.0 | 0 |
| Method numeral | Cormorant Garamond | 22 | 500 | italic | 1.0 | 0 |
| Note card body | Cormorant Garamond | 13 | 500 | italic | 1.5 | 0 |
| Body | Manrope | 13.5 | 400 | normal | 1.4 | 0 |
| Body small | Manrope | 12.5 | 400 | normal | 1.55 | 0 |
| Grocery item | Manrope | 14 | 400 | normal | 1.3 | 0 |
| Recipe meta (source name) | Manrope | 11 | 400 | normal | 1.3 | 0 |
| **Mono caption** (e.g. "NO. 01 — LIBRARY") | JetBrains Mono | 10 | 400 | uppercase | 1.0 | +0.18 px (~+0.018 em) |
| Mono number/qty (ingredient quantities, row indices, dates) | JetBrains Mono | 10–11 | 400–500 | normal | 1.4 | 0 |
| Mono button label ("GET →", "+ LIST", "CLEAR DONE") | JetBrains Mono | 11 | 500 | uppercase | 1.0 | +0.12 px |
| Mono note kicker ("COOK'S NOTE") | JetBrains Mono | 9 | 500 | uppercase | 1.0 | +0.2 px |
| Timing label ("PREP", "COOK", "TOTAL") | JetBrains Mono | 9 | 400 | uppercase | 1.0 | +0.16 px |

Letter-spacing values are converted from the design's px-based `letter-spacing` at the listed font size. On Android use `letterSpacing` in em (px ÷ font-size).

### Spacing

| Token | px |
|---|---|
| `space-2` | 2 |
| `space-4` | 4 |
| `space-6` | 6 |
| `space-8` | 8 |
| `space-10` | 10 |
| `space-12` | 12 |
| `space-14` | 14 |
| `space-16` | 16 |
| `space-18` | 18 |
| `space-22` | 22 (default screen horizontal padding) |
| `space-24` | 24 |
| `space-26` | 26 |

Horizontal screen padding is **22 dp** on Library / Grocery / Settings. Recipe Detail uses **18 dp** on the toolbar/hero and **22 dp** on the body.

### Borders & radii

- Hairline rules: **1 dp solid `rule`**
- Dotted rules between list items: **1 dp dotted `rule`** (use Material `MaterialDivider` with dash pattern, or a custom Drawable)
- URL input border: **1 dp solid `rule`**, corner radius **2 dp** (deliberately sharp)
- Settings toggle pill: 36×20 dp track, **10 dp radius**; 16 dp thumb, **8 dp radius**
- Grocery checkbox: **16 dp circle**, 1.4 dp stroke (uses `muted` when unchecked, `accent` when checked with white check icon)
- Empty state circle: 64 dp, **32 dp radius**, 1 dp dashed border in `muted`
- The hero image area on Recipe Detail has **no rounded corners** — just a 1 dp `rule` border

### Shadows

None. The aesthetic is flat — depth is built from rules, dotted dividers, and typographic hierarchy.

### Icons

Custom stroke icons (24×24 viewBox in the design, render at 14–20 dp). Replicate as `VectorDrawable`s. Stroke-only, 1.5–1.6 dp stroke, round caps + joins. The set used:

- `book` — recipe library / Recipes tab
- `cart` — grocery list / Grocery tab
- `paste` — URL input leading icon (clipboard outline)
- `arrow` — short right-arrow used after "Get"
- `back` — left arrow on toolbars
- `share` — up-arrow out of a tray
- `settings` — gear (only for the cog at top-right of Library)
- `check` — checkmark inside filled grocery checkboxes (white on `accent`)

Reference SVG paths are in `design/data.jsx` (`Ico.*`). Convert each to a 24-dp `VectorDrawable` with `tint` controlled by theme so we can recolor for active/inactive states.

## Screens

All screens are designed at **360 × 740 dp** (frame size, excluding Android system bars). The status bar and gesture nav are system-rendered, not part of the design.

The **bottom tab bar is present on every screen** (Library, Recipe Detail, Grocery, Empty, Settings) so users can hop between Recipes and Grocery from anywhere — note this is a change from the current app, where the Recipe Detail hides tabs.

---

### 1. Library

**Purpose:** Browse saved recipes. Default landing screen.

**Layout (top → bottom):**

1. **Header block** (padding `24 22 0 22`)
   - Row 1: mono caption "NO. 01 — LIBRARY" (left), gear icon 16 dp (right). `align-items: flex-end`, 4 dp margin-bottom.
   - Display title: "Only the" / line break / "recipe." — Cormorant Garamond italic 40/40, letter-spacing -1, color `ink`. Two physical lines.
   - Tagline: "A quiet shelf of recipes you actually use — nothing more." — Manrope 12.5/19, color `muted`, max-width 240 dp, margins `12 0 18 0`.
   - URL input row: see "URL input" component below.
2. **Section header "Saved"** (padding `26 22 10 22`)
   - Cormorant Garamond italic 18 (left) — flex 1 hairline rule — JetBrains Mono 10 count "5" (right). 12 dp gap, vertically centered.
3. **Recipe rows** — repeated. Each row:
   - Padding `14 22 14 22`, border-top 1 dp `rule` (except first which has none).
   - Three columns with 14 dp gap, `align-items: flex-start`:
     - **Index**: JetBrains Mono 10, color `muted`, right-aligned, min-width 22, padding-top 4. Format `01`, `02`, etc.
     - **Title + meta** (flex 1, min-width 0):
       - Title: Cormorant Garamond 19/22, weight 500, color `ink`.
       - Meta row (4 dp margin-top, 8 dp gap, centered): Manrope 11 source domain · 2-dp dot (bg `muted`) · JetBrains Mono 10 date (e.g. "May 12").
     - **Share icon**: 14 dp, color `muted`, padding-top 4.
4. **Bottom tab bar** — see "Bottom tabs" component.

**Content (exact order):**

```
01  Lebanese Manakish Dough        cleobuttera.com · May 12
02  Bistek Tagalog                 kawalingpinoy.com · Apr 27
03  Chicken Adobo                  panlasangpinoy.com · Apr 25
04  Weeknight Shakshuka            nytimes.com · Apr 19
05  Brown Butter Miso Cookies      bonappetit.com · Apr 11
```

Real data should be sorted newest-first (matching current app behaviour).

---

### 2. Recipe Detail

**Purpose:** Show a single saved recipe — hero, timing, ingredients, method, cook's note. Reachable from any Library row.

**Layout:**

1. **Toolbar** (padding `14 18 6 18`, no border)
   - Left: back-arrow 13 dp + "LIBRARY" mono 10, uppercase, color `muted`. 6 dp gap.
   - Right: "+ LIST" mono 10, uppercase, color `accent`. Tap = add this recipe's ingredients to Grocery.
2. **Hero** (margin `8 18 0 18`, height 150, border 1 dp `rule`)
   - Striped placeholder in the design — production should display the actual `og:image` extracted with the recipe. Cover-fit, no rounded corners. If no image, fall back to a `paper`-colored block with a small mono caption like "no image".
3. **Title block** (padding `18 22 4 22`)
   - Kicker: "LEBANESE · 6 PIECES" — mono 10, uppercase, color `accent`, letter-spacing +0.18.
   - Title: Cormorant Garamond 30/32, weight 500, letter-spacing -0.5, margin-top 6.
   - Attribution: "via cleobuttera.com" — Cormorant Garamond italic 13, color `muted`, margin-top 10.
4. **Timing strip** (margin `16 22 0 22`, border-top + border-bottom 1 dp `rule`)
   - Three equal columns separated by 1 dp `rule` vertical dividers. Each column: padding `10 0`, center-aligned.
     - Mono 9 uppercase label ("PREP" / "COOK" / "TOTAL"), color `muted`.
     - Cormorant Garamond 16 value (e.g. "15 min" / "12 min" / "2 hr 27 min"), margin-top 2.
5. **Ingredients section** (padding `20 22 10 22`)
   - Header row: italic 18 "Ingredients" + flex hairline rule (8 dp margin-bottom).
   - Each ingredient row: 7 dp top/bottom padding, 1 dp **dotted** `rule` border-top (none on the first row), 14 dp gap, font-size 13.5/19.
     - Quantity column: JetBrains Mono 11, color `accent`, min-width 60, padding-top 1. Extract numeric prefix + short unit (e.g. "3 cups", "1 tbsp", "½ tsp").
     - Name column: flex 1, Manrope 13.5, color `ink`.
6. **Method section** (padding `12 22 24 22`)
   - Same header pattern as Ingredients, "Method".
   - Each step (12 dp margin-bottom, 14 dp gap):
     - Numeral: Cormorant Garamond 22 italic, color `accent`, line-height 1, min-width 22.
     - Body: Manrope 13.5/21, color `ink`.
   - **Cook's note card** (margin-top 14, padding `12 14`)
     - Background: `accentSoft`, 2 dp left border in `accent`, no other borders, no radius.
     - Kicker: JetBrains Mono 9 uppercase "COOK'S NOTE", color `accent`, letter-spacing +0.2, margin-bottom 4.
     - Body: Cormorant Garamond italic 13/19.5, color `noteInk`.
7. **Bottom tab bar** (Recipes tab active because the user navigated here from Library).

---

### 3. Grocery List

**Purpose:** Aggregated, checkable shopping list pulled from "+ List" actions on recipes.

**Layout:**

1. **Header block** (padding `24 22 0 22`)
   - Mono caption "NO. 02 — MARKET" (4 dp margin-bottom).
   - Display title: "Grocery" / "list." — Cormorant Garamond italic 40/40, letter-spacing -1.
   - **Status row** (margins `14 0 18 0`, 10 dp gap, centered):
     - Left: "{N} TO BUY" — JetBrains Mono 10 uppercase, color `accent`. N = count of unchecked items.
     - Middle: flex 1 hairline rule.
     - Right: "CLEAR DONE" — mono 10 uppercase, color `muted`. Tap = delete checked items.
2. **Item list** (no outer padding; rows have their own)
   - First row has a **solid** 1 dp `rule` top border; subsequent rows use **dotted** 1 dp `rule`.
   - Row: padding `11 22`, 14 dp gap, items vertically centered. Checked rows render at **opacity 0.45**.
     - Checkbox: 16 dp circle, 1.4 dp border. Unchecked = `muted` border + transparent fill. Checked = `accent` border + `accent` fill + white check icon (10 dp).
     - Label: Manrope 14/18.2. When checked: `line-through` (decoration color `muted`).
3. **Bottom tab bar** (Grocery tab active).

**Content (exact order, currently checked items marked ✓):**

```
   3 cups bread flour
✓  1 cup lukewarm water
   1 tbsp dried active yeast
✓  1 tsp salt
✓  ½ tsp sugar
   3 tbsp olive oil
   2 tbsp za'atar blend
   ½ cup olive oil (for topping)
   1 lb beef sirloin
   ½ cup soy sauce
   4 cloves garlic
✓  1 large onion
```

---

### 4. Empty State (Library)

Shown in place of the recipe rows when the user has zero saved recipes.

**Layout:**

1. Same header block + URL input as Library (1 above).
2. **Empty body** (flex 1, center both axes, padding `60 36`)
   - Dashed 64 dp circle: 1 dp dashed `muted` border, contains the `book` icon at 28 dp, color `muted`. 18 dp margin-bottom.
   - Title: Cormorant Garamond italic 22/25, weight 500, "Your shelf is bare."
   - Body: Manrope 12.5/19, color `muted`, max-width 220 dp, margin-top 10. Copy: *"Paste a link above, or share a recipe from your browser. We'll keep just the parts you actually need."*
3. Bottom tab bar (Recipes tab active).

---

### 5. Settings

**Layout:**

1. **Top toolbar** (padding `24 22 0 22`)
   - "← BACK" — back-icon 13 dp + mono 10 uppercase, color `muted`. 6 dp gap, 8 dp margin-bottom.
2. **Display title** "Settings." — Cormorant Garamond italic 40/40, letter-spacing -1.
3. **Tagline** "Everything is stored on this device." — Manrope 12.5/19, color `muted`, margins `10 0 18 0`.
4. **Section: PREFERENCES** (mono 10 uppercase, color `muted`, padding `10 22 4 22`)
   - Setting row pattern: padding `14 22`, 14 dp gap, border-top 1 dp `rule`, items vertically centered.
     - Label: Cormorant Garamond 16/19.2, weight 500, color `ink`.
     - Hint: Manrope 11/14.3, color `muted`, 2 dp margin-top.
     - Control: right-aligned.
   - Rows:
     1. **Use metric units** — hint "Convert measurements as recipes are rendered" — toggle ON.
     2. **Default servings** — hint "Scaling applied to ingredient quantities" — picker showing "AS WRITTEN →" (mono 11 uppercase, color `accent`).
     3. **Show cook's notes** — hint "Author tips at the end of each recipe" — toggle ON.
5. **Section: DATA** (same heading style, padding `22 22 4 22`)
   - **Export library** — hint "Save all recipes as a .json file" — "EXPORT →" picker.
   - **Clear grocery list** — hint "Removes every item" — "CLEAR →" picker.
6. **Version footer** (text-align center, padding `36 22 22 22`): "Only the Recipe · v1.1" — JetBrains Mono 10, color `muted`, letter-spacing +0.18.
7. Bottom tab bar (Recipes tab active).

**Toggle control spec:**

- Track: 36 × 20 dp, 10 dp radius, 2 dp inner padding.
- Thumb: 16 × 16 dp, 8 dp radius, white fill.
- OFF: track `rule`, thumb left-aligned.
- ON: track `accent`, thumb right-aligned.
- No shadow, no animation other than a 150 ms linear cross-fade of track color and thumb x-position.

---

## Shared Components

### Bottom tabs

Sticky at bottom of every screen.

- Container: border-top 1 dp `rule`, background `bg`, padding `10 0 6 0`, flex row.
- Two tabs, each: flex 1, column layout, `align-items: center`, 4 dp gap.
  - Icon: 20 dp (book for Recipes, cart for Grocery), `currentColor`.
  - Label: Cormorant Garamond 13, letter-spacing +0.2 px. Inactive: weight 500 normal style, color `muted`. Active: weight 600 **italic**, color `accent`.
- No filled background, no underline.

### URL input

- Container: flex row, background `surface` (white), 1 dp solid `rule` border, **2 dp** corner radius, padding `10 12`, gap 10 dp.
- Leading icon: paste icon 14 dp, color `muted`.
- Placeholder/value: flex 1, JetBrains Mono 12, color `muted` when empty.
- Trailing CTA: "GET →" — JetBrains Mono 11 uppercase, letter-spacing +0.1, color `accent`. Tappable area should extend to the full right side of the container.

### Section header rule

Pattern used for "Saved", "Ingredients", "Method": label (Cormorant Garamond italic 18) — flex hairline — optional right-side count or action (mono 10, color depends on emphasis). 12 dp gap, items vertically centered (baseline for the ingredient/method headers).

### Note card (cook's note)

Padding `12 14`, no border-radius, 2 dp left border in `accent`, background `accentSoft`. Mono kicker, italic body. See Recipe Detail spec.

## Interactions & Behavior

These should already be wired up in the current app — only the visual layer changes. Behaviours to preserve / add:

- **URL paste → Get**: triggers the existing three-signal extraction cascade (JSON-LD → plugin → heuristic DOM). Show a loading state (not designed in this set — match existing behaviour; a thin progress bar under the toolbar is acceptable).
- **Recipe row tap**: navigate to Recipe Detail.
- **Recipe row long-press**: existing delete-with-confirm dialog.
- **Share icon (Library row)**: existing plain-text share intent.
- **"+ List" (Recipe Detail)**: add all ingredients to Grocery, show a transient confirmation (e.g. snackbar styled to theme — `bg` background, `ink` text, no radius, hairline `rule` border).
- **Grocery item tap**: toggle done. Animate opacity (0.45) and `line-through` over ~120 ms.
- **Grocery item long-press**: existing remove-with-confirm.
- **"Clear done"**: existing batch remove.
- **Settings toggle**: instant; persist immediately. No confirmation.
- **Settings picker rows** ("Default servings", export, clear): open existing modals/dialogs.
- **Bottom tab tap**: navigate within the app. Active tab gets the italic+accent treatment; do not reload the Library if it's already visible.
- **No swipe-down to refresh**, no pull-to-refresh, no rounded blob fab.

## State Management

No new state vs. current app. The redesign is purely presentational on top of the existing storage layer (`SharedPreferences` / room / whatever is already in use). Settings keys to expect (current app):

- `useMetricUnits: Boolean`
- `defaultServings: enum or int` (new — wire to an existing default if not present)
- `showAuthorNotes: Boolean` (new)

## Assets

- **Icons**: see "Icons" under Design Tokens. Convert from `design/data.jsx`'s `Ico.*` SVG sources to Android `VectorDrawable`s.
- **Fonts**: download from Google Fonts and bundle in `res/font/`. Cormorant Garamond and Lora variable fonts are available; Manrope and JetBrains Mono ship with subset weights — bundle 400/500/600/700.
- **No bitmap assets** are required from this handoff. Recipe hero photos come from the existing extraction pipeline.

## Files

Under `design/`:

- `Only The Recipe.html` — open this to see all five screens in a pan/zoom design canvas. The primary section ("Only the Recipe — Cream & Terracotta") is the target.
- `v1-editorial.jsx` — the chosen direction. Contains every V1 screen as a React component with exact tokens and structure.
- `data.jsx` — sample recipe + grocery data and the SVG icon set (`Ico.*`).
- `android-frame.jsx` — phone bezel used for presentation; **not part of the design** — ignore.
- `design-canvas.jsx` — pan/zoom wrapper used for presentation; **not part of the design** — ignore.
- `app.jsx` — wires everything into the canvas.
- `v2-swiss.jsx`, `v3-recipebook.jsx` — earlier explorations kept for reference. **Do not implement.**

## Implementation Notes

- The current Android codebase lives at `only-the-recipe/android/` in the project root. Inspect `android/app/src/main/res/values/themes.xml`, `colors.xml`, and the existing layouts before starting — re-theme the existing screens rather than building parallel ones.
- Replace the `Roboto` and `system-ui` font stack with the three families above. Apply via theme attributes (`textAppearance*`) rather than per-view.
- The deliberately small corner radii (2 dp on the URL input, 0 on the hero/note card) are intentional. Resist the urge to soften them to the M3 default.
- "+ List" and other small button affordances are **typographic only** (mono caps, accent color) — no chip, no pill, no filled background. This is the load-bearing aesthetic move.
- Hairline rules + dotted rules are part of the system. Use a custom `Divider` view with a `PathEffect` for the dotted variant.
- Maintain `accent` color exactly as `#C2602F`. It's the only colored element in most screens and any drift will be obvious.

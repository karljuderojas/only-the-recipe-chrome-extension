# Only The Recipe

A Chrome extension that strips SEO noise from recipe pages — no ads, no life stories, no popups. Just the recipe. It also builds a personal library of recipes as you browse, saved entirely on your device.

---

## Table of Contents

**Chrome Extension**
- [Installation](#installation)
- [How to Use](#how-to-use)
- [Your Recipe Library](#your-recipe-library)
- [Cached View](#cached-view)
- [Adding New Sites](#adding-new-sites)

**Android App**
- [Android — Installation](#android--installation)
- [Android — How to Use](#android--how-to-use)
  - [Extracting a recipe from a URL](#extracting-a-recipe-from-a-url)
  - [Sharing directly from Chrome](#sharing-directly-from-chrome-or-any-browser)
  - [Your recipe library](#your-recipe-library)
  - [Grocery list](#grocery-list)
  - [Personal notes](#personal-notes)
  - [Unit conversion](#unit-conversion)
  - [Settings](#settings)

**General**
- [Features](#features)
- [Storage](#storage)
- [File Structure](#file-structure)
- [License](#license)

---

## Installation

Only The Recipe is not yet on the Chrome Web Store. Install it manually in a few steps — no technical knowledge required.

### Step 1 — Download the extension

Download the `only-the-recipe` folder to your computer. Keep it somewhere permanent (your Documents folder works well) — Chrome loads the extension directly from this folder, so don't move or delete it after installing.

### Step 2 — Open Chrome Extensions

In Chrome, navigate to:

```
chrome://extensions
```

Or go to the Chrome menu (⋮) → **Extensions** → **Manage Extensions**.

### Step 3 — Enable Developer Mode

In the top-right corner of the Extensions page, toggle **Developer mode** on. A new row of buttons will appear.

### Step 4 — Load the extension

Click **Load unpacked**, then select the `only-the-recipe` folder. The extension will appear in your list with its icon.

### Step 5 — Pin it to your toolbar (recommended)

Click the puzzle piece icon (🧩) in the Chrome toolbar, find **Only The Recipe**, and click the pin icon. This makes the library icon always visible.

> After any code changes, return to `chrome://extensions` and click the **refresh** button (↺) on the Only The Recipe card to reload it.

---

## How to Use

### Cleaning up a recipe page

1. Visit any recipe page — AllRecipes, Serious Eats, Budget Bytes, Tasty, and most food blogs work automatically.
2. A **🍽 Only The Recipe** button appears in the bottom-right corner of the page.
3. Click it. The page collapses to show only what matters: the hero image, ingredients, equipment (if listed), instructions, and any author notes or tips.
4. To restore the original page at any time, click **✕ Show full page** at the top of the clean view. The page restores instantly — no reload.

### Saving a recipe

While in the clean view, click **Save recipe**. The button confirms "Saved!" — the recipe is now in your local library. You can save as many as you like.

---

## Your Recipe Library

Click the Only The Recipe icon in the Chrome toolbar to open your library.

### List view

All your saved recipes are listed newest first, with the date saved. Click any recipe to open its detail view. Click **×** to delete it.

### Detail view

Shows the full recipe exactly as extracted:

| Section | What's included |
|---|---|
| Hero image | The recipe's primary photo |
| Title | Recipe name |
| Timing | Total, prep, and cook times |
| Ingredients | Full ingredient list |
| Equipment | Tools needed (when listed by the site) |
| Instructions | Step-by-step, with section headings if present |
| Author Notes | Tips, variations, or notes left by the recipe author |
| My Notes | A free-form text area for your own personal notes — auto-saved when you leave the field |

Click **Open cached view** to open the recipe in a full browser tab (see below).

---

## Cached View

The cached view opens your saved recipe as a clean, full-page document — useful for reading while cooking, printing, or sharing. It contains:

- Hero image
- Full recipe (ingredients, equipment, instructions)
- Author notes
- Your personal notes (in a highlighted block at the bottom)
- A link back to the original source page

The cached view is entirely local — it reads from your saved library and does not fetch anything from the internet.

---

## Adding New Sites

Only The Recipe works on most sites automatically using structured data detection. If a site isn't detected, you can add it with a one-line entry in `plugins/plugin.json`:

```json
{ "site": "example.com", "container": ".recipe-card" }
```

**How to find the container selector:**

1. On the recipe page, right-click the recipe content area and choose **Inspect**
2. In Chrome DevTools, hover over elements until the one that wraps the full recipe (ingredients + instructions) is highlighted
3. Look at its `class` or `id` attribute — that's your selector
4. Use `.classname` for a class, or `#idname` for an ID

After editing `plugin.json`, reload the extension at `chrome://extensions`.

---

## Android — Installation

The Android app lets you extract and save recipes on your phone. It works the same way as the extension — paste a URL, or share directly from Chrome — with no ads, no life story, just the recipe.

### Step 1 — Download the APK

The APK is built automatically on every push to GitHub and published to the **Releases** page.

1. Open the repo on GitHub and click **Releases** (right sidebar).
2. Expand the latest release and download the `.apk` file.

### Step 2 — Transfer to your phone (if needed)

If you downloaded the APK on a computer:

- **USB:** Connect your phone and copy the APK to it.
- **Email / Google Drive:** Send or upload the APK and open it on your phone.
- **Direct download:** Open the Releases page in your phone's browser and download directly.

### Step 3 — Allow installation from unknown sources

Because this APK isn't from the Play Store, Android requires a one-time permission:

1. Open the APK file on your phone.
2. Android will prompt you to allow installation from this source — tap **Settings**, enable it, then go back and tap **Install**.
3. The exact wording varies by Android version and browser/file manager, but the flow is the same on all devices.

### Step 4 — Install and open

Tap **Install**, then **Open**. The app is now on your home screen as **Only The Recipe**.

> The APK is a debug build, fine for personal use. The app checks for newer versions automatically each time it launches and will prompt you to download if one is available.

---

## Android — How to Use

### Extracting a recipe from a URL

1. Open the app.
2. Paste a recipe URL into the text box at the top and tap **Get** (or press the keyboard's Go key).
3. The page loads in the app and the recipe is extracted automatically — the same three-signal cascade as the Chrome extension (JSON-LD → plugin fingerprint → heuristic DOM).
4. The clean recipe view appears: hero image, timing, ingredients, equipment (if listed), instructions, and author notes.
5. Tap **Save Recipe** to add it to your library.

### Sharing directly from Chrome (or any browser)

This is the fastest way to use the app while browsing.

1. Find a recipe in Chrome (or any browser) and tap the **Share** button.
2. In the share sheet, tap **Only The Recipe**.
3. The recipe loads immediately — the library screen is skipped entirely.
4. Tap **Save Recipe** if you want to keep it.
5. Press **Back** and you return to Chrome, right where you left off.

> The app registers itself as a share target for any text/URL shared from any app, not just Chrome.

### Your recipe library

Tap **← Back** from any recipe to return to the library. Saved recipes are listed newest first, showing the recipe title and the date saved.

- **Open a saved recipe:** Tap its row.
- **Share a recipe:** Tap **Share** on the right side of its row to send the recipe as plain text.
- **Delete a recipe:** Long-press its row — a confirmation dialog appears before anything is removed.

### Grocery list

Tap the **Grocery List** tab in the bottom navigation bar.

- **Add ingredients:** Open a recipe and tap **+ List** in the toolbar. All ingredients are added to the list at once.
- **Check off items:** Tap any item to toggle it. Checked items are crossed out.
- **Remove an item:** Long-press it for a confirmation prompt.
- **Clear completed:** Tap **Clear done** in the top-right corner to remove all checked items at once.

### Personal notes

When viewing a saved recipe, a notes field appears at the bottom of the screen. Type anything — substitutions, tweaks, how it turned out. Notes are saved automatically when you leave the field.

### Unit conversion

Open **Settings** (top-right of the home screen) and toggle **Use Metric Units**. When enabled, all recipes are displayed with metric measurements — temperatures in °C, volumes in ml/L, weights in g/kg. The original saved data is never changed; conversion is applied on the fly each time a recipe is rendered.

### Settings

Tap **Settings** in the top-right corner of the home screen.

| Setting | Details |
|---|---|
| Use Metric Units | Convert all recipe measurements to metric when enabled |
| Version | Shows the currently installed app version |

### What gets extracted

| Section | Details |
|---|---|
| Hero image | The recipe's primary photo (`og:image` or largest visible image) |
| Title | Recipe name |
| Timing | Prep, cook, and total times |
| Yield | Serving size or quantity |
| Ingredients | Full ingredient list |
| Equipment | Tools needed (when listed by the site) |
| Instructions | Step-by-step, with section headings when present |
| Author Notes | Tips, variations, and cook's notes from the recipe author |

---

## Features

**Chrome extension**

| Feature | Details |
|---|---|
| Clean view | Hides everything except the recipe using Shadow DOM — ads, sidebars, footers, related posts |
| Live view | Inherits the site's own fonts and background color so the recipe looks at home |
| Three-signal extraction | Tries Schema.org JSON-LD first, then known plugin selectors, then heuristic DOM scoring |
| Author notes | Captures tips, variations, and cook's notes left by the recipe author |
| Equipment | Extracts tools and equipment when listed |
| Hero image | Uses the page's `og:image` (the canonical recipe photo) |
| Reversible | The original page is never modified — restore with one click, no reload |
| Personal library | Saved locally with `chrome.storage.local` — no account, no sync, no tracking |
| Personal notes | Editable per recipe, auto-saved on blur |
| Cached view | Full-page standalone recipe template, readable offline |
| First-launch guide | Welcome page opens automatically on install |

**Android app**

| Feature | Details |
|---|---|
| URL extraction | Paste any recipe URL — same three-signal cascade as the extension |
| Share target | Accept URLs shared from Chrome or any browser directly into the app |
| Recipe library | Recipes saved on-device as JSON; newest first, long-press to delete |
| Share recipe | Send any saved recipe as formatted plain text via any share target |
| Grocery list | Add all ingredients from a recipe to a shared checklist in one tap |
| Unit conversion | Global metric toggle in Settings converts temperatures, volumes, and weights on the fly |
| Personal notes | Per-recipe notes field, auto-saved on blur |
| Update checker | Checks GitHub Releases on launch and prompts to download if a newer APK is available |

---

## Storage

Recipes are stored locally in Chrome's built-in storage (`chrome.storage.local`). The soft cap is **50 MB**, which is enough for several hundred recipes. The extension logs a console warning when storage exceeds 85% of that limit.

Nothing is sent to any server. No analytics, no telemetry, no accounts.

---

## File Structure

```
only-the-recipe/
├── manifest.json               MV3 manifest — permissions, scripts, icons
├── content/
│   ├── extractor.js            Three-signal recipe extraction cascade
│   ├── isolator.js             Shadow DOM isolation, restore bar, clean view HTML
│   └── browser.js              Entry point — detects recipe, injects trigger button
├── background/
│   └── service-worker.js       Save handler, storage quota check, first-launch tab
├── storage/
│   └── storage.js              chrome.storage.local helpers (get, save, update, delete)
├── popup/
│   ├── popup.html              List view + detail view markup
│   ├── popup.js                Navigation, rendering, notes save
│   └── popup.css               Popup styles (320px wide)
├── cache/
│   ├── cache.html              Standalone cached recipe page
│   └── cache.js                Recipe rendering logic for cached view
├── onboarding/
│   └── welcome.html            First-launch instructions page
├── plugins/
│   └── plugin.json             Per-site CSS selector registry
└── icons/
    ├── icon16.png
    ├── icon48.png
    └── icon128.png
```

---

## License

MIT

// Android version: chrome.runtime removed, plugin data inlined, sections encoded as "§:Name" strings

const INGREDIENT_RE = /ingredient/i;
const INSTRUCTION_RE = /instruction|direction|step|method/i;
const RECIPE_RE = /recipe|wprm|tasty|mv-recipe/i;
const NOTE_RE = /^(notes?|tips?|cook'?s?\s+notes?|chef'?s?\s+notes?|hints?|variations?)$/i;
const HEADING_RE = /^H[1-4]$/;

const PLUGINS = [
  { site: "allrecipes.com",     container: ".recipe-content" },
  { site: "seriouseats.com",    container: ".recipe-card" },
  { site: "tasty.co",           container: ".recipe" },
  { site: "foodnetwork.com",    container: ".o-RecipeInfo" },
  { site: "bonappetit.com",     container: ".recipe__body" },
  { site: "epicurious.com",     container: ".recipe-content" },
  { site: "thekitchn.com",      container: ".Recipe" },
  { site: "smittenkitchen.com", container: ".recipe-content" },
  { site: "budgetbytes.com",    container: ".wprm-recipe-container" },
  { site: "pinchofyum.com",     container: ".wprm-recipe-container" }
];

// --- Signal 1: JSON-LD ---

function extractFromJsonLd() {
  const scripts = document.querySelectorAll('script[type="application/ld+json"]');
  for (const script of scripts) {
    try {
      const data = JSON.parse(script.textContent);
      const entries = Array.isArray(data) ? data : [data, ...(data['@graph'] || [])];
      for (const entry of entries) {
        if (entry['@type'] === 'Recipe') return normalizeJsonLd(entry);
      }
    } catch (_) {}
  }
  return null;
}

function normalizeJsonLd(data) {
  return {
    title:        data.name || '',
    description:  data.description || '',
    ingredients:  data.recipeIngredient || [],
    instructions: flattenInstructions(data.recipeInstructions || []),
    equipment:    extractEquipment(data),
    authorNotes:  extractAuthorNotes(null),
    timingPrep:   data.prepTime  || '',
    timingCook:   data.cookTime  || '',
    timingTotal:  data.totalTime || '',
    recipeYield:  data.recipeYield ? String(data.recipeYield) : '',
    imageUrl:     findHeroImage(null) || extractImage(data.image),
    sourceUrl:    location.href,
    source:       'json-ld',
  };
}

function flattenInstructions(raw) {
  if (!raw.length) return [];
  if (typeof raw[0] === 'string') return raw;
  const steps = [];
  for (const item of raw) {
    if (item['@type'] === 'HowToStep') {
      steps.push(item.text || '');
    }
    if (item['@type'] === 'HowToSection') {
      steps.push('§:' + (item.name || ''));  // § prefix marks a section header
      for (const s of item.itemListElement || []) steps.push(s.text || '');
    }
  }
  return steps;
}

function extractEquipment(data) {
  const raw = data.tool || data.recipeEquipment || [];
  const items = Array.isArray(raw) ? raw : [raw];
  return items.map(item => (typeof item === 'string' ? item : item.name || '')).filter(Boolean);
}

function extractImage(img) {
  if (!img) return '';
  if (typeof img === 'string') return img;
  if (Array.isArray(img)) return extractImage(img[0]);
  return img.url || '';
}

// --- Hero image ---

function findHeroImage(containerEl) {
  const og =
    document.querySelector('meta[property="og:image"]')?.content ||
    document.querySelector('meta[name="twitter:image"]')?.content ||
    document.querySelector('meta[name="twitter:image:src"]')?.content;
  if (og) return og;

  if (!containerEl) return '';
  let best = null;
  let bestArea = 0;
  containerEl.querySelectorAll('img').forEach(img => {
    const w = img.naturalWidth  || img.offsetWidth;
    const h = img.naturalHeight || img.offsetHeight;
    const area = w * h;
    if (area > bestArea && w > 150) { bestArea = area; best = img.src; }
  });
  return best || '';
}

// --- Author notes ---

function extractAuthorNotes(container) {
  const root = container || document;

  const pluginSelectors = [
    '.wprm-recipe-notes',
    '.wprm-recipe-notes-container',
    '.tasty-recipes-notes',
    '.tasty-recipes-notes-body',
    '.mv-create-notes',
    '[class*="recipe-notes"]',
    '[class*="recipe-note"]:not(textarea)',
    '[id*="recipe-notes"]',
    '[class*="notes-section"]',
  ];

  for (const sel of pluginSelectors) {
    const el = root.querySelector(sel);
    const text = el?.textContent.trim();
    if (text) return text;
  }

  for (const h of root.querySelectorAll('h2, h3, h4')) {
    if (!NOTE_RE.test(h.textContent.trim())) continue;
    const parts = [];
    let next = h.nextElementSibling;
    while (next && !HEADING_RE.test(next.tagName)) {
      const text = next.textContent.trim();
      if (text) parts.push(text);
      next = next.nextElementSibling;
    }
    if (parts.length) return parts.join('\n\n');
  }

  return '';
}

// --- Signal 2: Plugin fingerprinting (inlined data, no chrome.runtime) ---

function extractFromPlugin() {
  const host = location.hostname.replace(/^www\./, '');
  const match = PLUGINS.find(p => host.includes(p.site));
  if (!match) return null;
  const container = document.querySelector(match.container);
  if (!container) return null;
  return extractFromContainer(container, 'plugin');
}

// --- Signal 3: Heuristic DOM scoring ---

function extractFromHeuristic() {
  let bestScore = 0;
  let bestEl = null;

  for (const el of document.querySelectorAll('article, section, div, main')) {
    let score = 0;
    if (RECIPE_RE.test(el.className + el.id)) score += 20;
    for (const list of el.querySelectorAll('ul, ol')) {
      const context = list.className + list.id + (list.previousElementSibling?.textContent || '');
      const text = list.textContent;
      if (INGREDIENT_RE.test(context)) score += 15;
      if (INSTRUCTION_RE.test(context)) score += 15;
      if (/\d\s*(cup|tbsp|tsp|oz|lb|g|ml|kg)/i.test(text)) score += 10;
    }
    if (score > bestScore) { bestScore = score; bestEl = el; }
  }

  if (bestScore < 25 || !bestEl) return null;
  return extractFromContainer(bestEl, 'heuristic');
}

function extractFromContainer(container, source) {
  const title = container.querySelector('h1, h2, h3')?.textContent.trim() || document.title;
  const ingredientItems = [];
  const instructionItems = [];
  const equipmentItems = [];

  container.querySelectorAll('li').forEach(li => {
    if (li.closest('[class*="ingredient"], [id*="ingredient"]')) {
      ingredientItems.push(li.textContent.trim()); return;
    }
    if (li.closest('[class*="instruction"], [class*="direction"], [class*="step"], [id*="step"]')) {
      instructionItems.push(li.textContent.trim()); return;
    }
    if (li.closest('[class*="equipment"], [id*="equipment"], [class*="tool"], [id*="tool"]')) {
      equipmentItems.push(li.textContent.trim());
    }
  });

  container.querySelectorAll('[class*="equipment"] [class*="name"], [class*="tool"] [class*="name"]').forEach(el => {
    const text = el.textContent.trim();
    if (text && !equipmentItems.includes(text)) equipmentItems.push(text);
  });

  return {
    title,
    description:  '',
    ingredients:  ingredientItems,
    instructions: instructionItems,
    equipment:    equipmentItems,
    authorNotes:  extractAuthorNotes(container),
    timingPrep:   '',
    timingCook:   '',
    timingTotal:  '',
    recipeYield:  '',
    imageUrl:     findHeroImage(container),
    sourceUrl:    location.href,
    source,
  };
}

// --- Entry point for Android bridge ---

try {
  const recipe = extractFromJsonLd() || extractFromPlugin() || extractFromHeuristic();
  if (window.Android) {
    window.Android.onRecipeExtracted(recipe ? JSON.stringify(recipe) : null);
  }
} catch (e) {
  if (window.Android) {
    window.Android.onRecipeExtracted(null);
  }
}

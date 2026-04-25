// Three-signal extraction cascade: JSON-LD → Plugin fingerprint → Heuristic DOM scoring

const INGREDIENT_RE = /ingredient/i;
const INSTRUCTION_RE = /instruction|direction|step|method/i;
const RECIPE_RE = /recipe|wprm|tasty|mv-recipe/i;
const NOTE_RE = /^(notes?|tips?|cook'?s?\s+notes?|chef'?s?\s+notes?|hints?|variations?)$/i;
const HEADING_RE = /^H[1-4]$/;

// Strip HTML markup from fields that some sites (e.g. Serious Eats) embed in JSON-LD text values.
// Removes images and captions before extracting text so photo credits don't bleed into steps.
function cleanText(str) {
  if (!str || typeof str !== 'string') return '';
  if (!str.includes('<')) return str.trim();
  const div = document.createElement('div');
  div.innerHTML = str;
  div.querySelectorAll('img, figure, figcaption, noscript, picture, [class*="caption"], [class*="credit"]').forEach(el => el.remove());
  const text = div.textContent.replace(/\s+/g, ' ').trim();
  // Fallback: strip any raw tag markup that survived DOM parsing (e.g. inside noscript, malformed tags)
  return text.includes('<') ? text.replace(/<[^>]*>/g, '').replace(/\s+/g, ' ').trim() : text;
}

// Reject steps that look like category labels or related-recipe titles rather than instructions.
// Real steps are long, or contain punctuation/digits; junk steps are short noun phrases.
function isRealStep(text) {
  if (!text) return false;
  if (text.length > 80) return true;
  return /[.,;:!?]|\d/.test(text);
}

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
    title:       cleanText(data.name || ''),
    description: cleanText(data.description || ''),
    ingredients: (data.recipeIngredient || []).map(cleanText).filter(Boolean),
    instructions: flattenInstructions(data.recipeInstructions || []),
    equipment:   extractEquipment(data),
    authorNotes: extractAuthorNotes(null),
    timing: {
      prep:  data.prepTime  || '',
      cook:  data.cookTime  || '',
      total: data.totalTime || '',
    },
    yield:    data.recipeYield || '',
    imageUrl: findHeroImage(null) || extractImage(data.image),
    sourceUrl: location.href,
    source: 'json-ld',
  };
}

function flattenInstructions(raw) {
  if (!raw.length) return [];
  if (typeof raw[0] === 'string') return raw.map(cleanText).filter(isRealStep);
  const steps = [];
  for (const item of raw) {
    if (item['@type'] === 'HowToStep') {
      const text = cleanText(item.text || '');
      if (isRealStep(text)) steps.push(text);
    }
    if (item['@type'] === 'HowToSection') {
      const sectionSteps = (item.itemListElement || [])
        .map(s => cleanText(s.text || ''))
        .filter(isRealStep);
      if (sectionSteps.length) {
        steps.push({ section: item.name || '' });
        steps.push(...sectionSteps);
      }
    }
  }
  return steps;
}

function extractEquipment(data) {
  const raw = data.tool || data.recipeEquipment || [];
  const items = Array.isArray(raw) ? raw : [raw];

  // Some sites (e.g. Serious Eats) put author/contributor names in the tool field
  const authorNames = new Set();
  const authors = Array.isArray(data.author) ? data.author : (data.author ? [data.author] : []);
  authors.forEach(a => {
    const name = (typeof a === 'string' ? a : (a.name || '')).trim().toLowerCase();
    if (name) authorNames.add(name);
  });

  return items
    .map(item => (typeof item === 'string' ? item : item.name || '').trim())
    .filter(s => s.length > 2 && !authorNames.has(s.toLowerCase()));
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

// --- Signal 2: Plugin fingerprinting ---

async function extractFromPlugin() {
  try {
    const url = chrome.runtime.getURL('plugins/plugin.json');
    const res = await fetch(url);
    const { plugins } = await res.json();
    const host = location.hostname.replace(/^www\./, '');
    const match = plugins.find(p => host.includes(p.site));
    if (!match) return null;
    const container = document.querySelector(match.container);
    if (!container) return null;
    return extractFromContainer(container, 'plugin');
  } catch (_) {
    return null;
  }
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
    const text = li.textContent.trim();
    if (!text) return;
    if (li.closest('[class*="ingredient"], [id*="ingredient"]')) {
      ingredientItems.push(text); return;
    }
    if (li.closest('[class*="instruction"], [class*="direction"], [class*="step"], [id*="step"]')) {
      if (isRealStep(text)) instructionItems.push(text); return;
    }
    if (li.closest('[class*="equipment"], [id*="equipment"], [class*="tool"], [id*="tool"]')) {
      if (text.length > 2) equipmentItems.push(text);
    }
  });

  container.querySelectorAll('[class*="equipment"] [class*="name"], [class*="tool"] [class*="name"]').forEach(el => {
    const text = el.textContent.trim();
    if (text.length > 2 && !equipmentItems.includes(text)) equipmentItems.push(text);
  });

  return {
    title,
    description: '',
    ingredients: ingredientItems,
    instructions: instructionItems,
    equipment:   equipmentItems,
    authorNotes: extractAuthorNotes(container),
    timing: {},
    yield: '',
    imageUrl: findHeroImage(container),
    sourceUrl: location.href,
    source,
  };
}

// --- Public API ---

async function extractRecipe() {
  return extractFromJsonLd() || await extractFromPlugin() || extractFromHeuristic();
}

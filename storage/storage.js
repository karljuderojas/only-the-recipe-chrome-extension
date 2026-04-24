// Shared utilities — available to popup and cache (both load this file first)

function htmlEscape(str) {
  return String(str ?? '')
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;')
    .replace(/'/g, '&#39;');
}

function formatDuration(iso) {
  if (!iso || !iso.startsWith('PT')) return iso || '';
  const h = iso.match(/(\d+)H/)?.[1];
  const m = iso.match(/(\d+)M/)?.[1];
  const parts = [];
  if (h) parts.push(`${h} hr`);
  if (m) parts.push(`${m} min`);
  return parts.join(' ') || iso;
}

function buildTimingMeta(recipe) {
  const parts = [];
  if (recipe.timing?.total) parts.push(`Total: ${formatDuration(recipe.timing.total)}`);
  if (recipe.timing?.prep)  parts.push(`Prep: ${formatDuration(recipe.timing.prep)}`);
  if (recipe.timing?.cook)  parts.push(`Cook: ${formatDuration(recipe.timing.cook)}`);
  if (recipe.yield)         parts.push(recipe.yield);
  return parts;
}

// chrome.storage.local helpers

const Storage = {
  async getAll() {
    const { recipes = [] } = await chrome.storage.local.get('recipes');
    return recipes;
  },

  async getById(id) {
    const recipes = await this.getAll();
    return recipes.find(r => r.savedAt === id) || null;
  },

  async updateNotes(id, notes) {
    const { recipes = [] } = await chrome.storage.local.get('recipes');
    const idx = recipes.findIndex(r => r.savedAt === id);
    if (idx < 0) return;
    recipes[idx].notes = notes;
    await chrome.storage.local.set({ recipes });
  },

  async delete(id) {
    const { recipes = [] } = await chrome.storage.local.get('recipes');
    await chrome.storage.local.set({ recipes: recipes.filter(r => r.savedAt !== id) });
  },

  async clear() {
    await chrome.storage.local.remove('recipes');
  },
};

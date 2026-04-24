// Opens welcome page on first install
chrome.runtime.onInstalled.addListener(({ reason }) => {
  if (reason === 'install') {
    chrome.tabs.create({ url: chrome.runtime.getURL('onboarding/welcome.html') });
  }
});

// Listens for messages from content scripts and popup

chrome.runtime.onMessage.addListener((msg, _sender, sendResponse) => {
  if (msg.type === 'SAVE_RECIPE') {
    saveRecipe(msg.recipe).then(() => sendResponse({ ok: true }));
    return true; // keep channel open for async response
  }
});

async function saveRecipe(recipe) {
  const { recipes = [] } = await chrome.storage.local.get('recipes');
  const existing = recipes.findIndex(r => r.sourceUrl === recipe.sourceUrl);
  if (existing >= 0) {
    recipes[existing] = { ...recipes[existing], ...recipe, savedAt: Date.now() };
  } else {
    recipes.unshift({ ...recipe, savedAt: Date.now(), notes: '' });
  }
  await chrome.storage.local.set({ recipes });
  warnIfNearQuota();
}

async function warnIfNearQuota() {
  const bytes = await chrome.storage.local.getBytesInUse('recipes');
  const quota = chrome.storage.local.QUOTA_BYTES;
  if (bytes / quota > 0.85) {
    console.warn(`Only The Recipe: storage at ${Math.round(bytes / quota * 100)}% capacity`);
  }
}

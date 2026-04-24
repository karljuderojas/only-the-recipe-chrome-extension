// Runs on every page. Loads extractor modules and fires isolation on match.

(async function () {
  // Only run once per page load
  if (window.__onlyTheRecipeActive) return;
  window.__onlyTheRecipeActive = true;

  // Lazy-load helpers by injecting script tags into the page isn't possible in MV3 content scripts.
  // Instead, all three files are declared in manifest content_scripts and share this scope.

  const recipe = await extractRecipe();
  if (!recipe) return;

  // Inject a toolbar button into the page for the user to trigger clean view manually
  injectTriggerButton(recipe);
})();

function injectTriggerButton(recipe) {
  const btn = document.createElement('button');
  btn.id = 'rc-trigger';
  btn.textContent = '🍽 Only The Recipe';
  Object.assign(btn.style, {
    position:   'fixed',
    bottom:     '20px',
    right:      '20px',
    zIndex:     '2147483647',
    padding:    '10px 16px',
    background: '#2563eb',
    color:      '#fff',
    border:     'none',
    borderRadius: '24px',
    fontSize:   '14px',
    fontFamily: 'system-ui, sans-serif',
    cursor:     'pointer',
    boxShadow:  '0 2px 8px rgba(0,0,0,0.25)',
  });

  btn.addEventListener('click', () => {
    btn.remove();
    isolateRecipe(recipe);
  });

  document.body.appendChild(btn);
}

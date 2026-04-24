// Shadow DOM isolation: hides page siblings, wraps recipe in a shadow root, injects restore bar

let shadowHost = null;
let hiddenElements = [];

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

function isolateRecipe(recipe) {
  if (shadowHost) return;
  const container = findRecipeContainer();
  collapsePageChrome(container);
  shadowHost = buildShadowView(recipe);
}

function findRecipeContainer() {
  return (
    document.querySelector('[itemtype*="Recipe"]') ||
    document.querySelector('.wprm-recipe-container, .tasty-recipe, .mv-recipe-block') ||
    document.body
  );
}

function collapsePageChrome(anchor) {
  let el = anchor;
  while (el && el !== document.body) {
    const parent = el.parentElement;
    if (parent) {
      for (const sibling of parent.children) {
        if (sibling !== el && sibling.style.display !== 'none') {
          sibling.dataset.rcHidden = 'true';
          sibling.style.display = 'none';
          hiddenElements.push(sibling);
        }
      }
    }
    el = parent;
  }
}

function buildShadowView(recipe) {
  const host = document.createElement('div');
  host.id = 'only-the-recipe-host';
  document.body.insertBefore(host, document.body.firstChild);

  const shadow = host.attachShadow({ mode: 'open' });
  const inheritedFont  = getComputedStyle(document.body).fontFamily;
  const inheritedColor = getComputedStyle(document.body).color;
  const inheritedBg    = getComputedStyle(document.body).backgroundColor;

  shadow.innerHTML = buildHTML(recipe, inheritedFont, inheritedColor, inheritedBg);

  shadow.getElementById('rc-restore').addEventListener('click', restorePage);
  shadow.getElementById('rc-save').addEventListener('click', () => {
    chrome.runtime.sendMessage({ type: 'SAVE_RECIPE', recipe });
    const btn = shadow.getElementById('rc-save');
    btn.textContent = 'Saved!';
    btn.disabled = true;
  });

  return host;
}

function buildHTML(recipe, font, color, bg) {
  const ingredients = recipe.ingredients.map(i => `<li>${htmlEscape(i)}</li>`).join('');
  const equipment   = (recipe.equipment || []).map(e => `<li>${htmlEscape(e)}</li>`).join('');
  const authorNotes = recipe.authorNotes || '';

  const instructions = recipe.instructions.map(step => {
    if (typeof step === 'object' && step.section) return `<h3>${htmlEscape(step.section)}</h3>`;
    return `<li>${htmlEscape(step)}</li>`;
  });
  const instructionHtml = wrapInOl(instructions);

  const timing = Object.entries(recipe.timing)
    .filter(([, v]) => v)
    .map(([k, v]) => `<span><strong>${k}:</strong> ${htmlEscape(formatDuration(v))}</span>`)
    .join(' &nbsp;·&nbsp; ');

  return `
    <style>
      :host { all: initial; display: block; }
      #rc-wrap {
        font-family: ${font};
        color: ${color};
        background: ${bg || '#fff'};
        max-width: 760px;
        margin: 0 auto;
        padding: 24px 20px 60px;
        line-height: 1.6;
      }
      #rc-bar {
        display: flex;
        align-items: center;
        gap: 10px;
        margin-bottom: 24px;
        font-size: 13px;
      }
      #rc-restore, #rc-save {
        padding: 6px 14px;
        border: 1px solid currentColor;
        border-radius: 4px;
        background: transparent;
        cursor: pointer;
        font: inherit;
        color: inherit;
      }
      #rc-save { background: #2563eb; color: #fff; border-color: #2563eb; }
      #rc-save:disabled { opacity: 0.6; cursor: default; }
      h1 { margin-top: 0; }
      .rc-timing { font-size: 14px; color: #666; margin-bottom: 16px; }
      ul, ol { padding-left: 1.4em; }
      li { margin-bottom: 6px; }
      .rc-hero {
        width: 100%;
        height: 280px;
        object-fit: cover;
        border-radius: 12px;
        margin-bottom: 24px;
        display: block;
      }
      .rc-author-notes {
        background: #f0f4ff;
        border-left: 3px solid #2563eb;
        padding: 12px 16px;
        margin-top: 24px;
        border-radius: 4px;
        font-size: 14px;
        line-height: 1.7;
        white-space: pre-line;
      }
      .rc-author-notes strong {
        display: block;
        margin-bottom: 6px;
        font-size: 12px;
        text-transform: uppercase;
        letter-spacing: 0.05em;
        opacity: 0.6;
      }
    </style>
    <div id="rc-wrap">
      <div id="rc-bar">
        <button id="rc-restore">✕ Show full page</button>
        <button id="rc-save">Save recipe</button>
      </div>
      ${recipe.imageUrl ? `<img class="rc-hero" src="${htmlEscape(recipe.imageUrl)}" alt="${htmlEscape(recipe.title)}">` : ''}
      <h1>${htmlEscape(recipe.title)}</h1>
      ${timing ? `<p class="rc-timing">${timing}</p>` : ''}
      ${recipe.description ? `<p>${htmlEscape(recipe.description)}</p>` : ''}
      ${recipe.ingredients.length ? `<h2>Ingredients</h2><ul>${ingredients}</ul>` : ''}
      ${equipment ? `<h2>Equipment</h2><ul>${equipment}</ul>` : ''}
      ${recipe.instructions.length ? `<h2>Instructions</h2>${instructionHtml}` : ''}
      ${authorNotes ? `<div class="rc-author-notes"><strong>Author Notes</strong>${htmlEscape(authorNotes)}</div>` : ''}
    </div>
  `;
}

function wrapInOl(items) {
  let html = '<ol>';
  for (const item of items) {
    html += item.startsWith('<h3>') ? `</ol>${item}<ol>` : item;
  }
  return html + '</ol>';
}

function restorePage() {
  for (const el of hiddenElements) {
    el.style.display = '';
    delete el.dataset.rcHidden;
  }
  hiddenElements = [];
  if (shadowHost) {
    shadowHost.remove();
    shadowHost = null;
  }
}

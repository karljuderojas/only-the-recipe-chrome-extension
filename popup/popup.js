let currentRecipe = null;

// --- Views ---

function showList() {
  document.getElementById('list-view').style.display = 'block';
  document.getElementById('detail-view').style.display = 'none';
  document.getElementById('header-back').style.display = 'none';
  currentRecipe = null;
}

function showDetail(recipe) {
  currentRecipe = recipe;
  document.getElementById('list-view').style.display = 'none';
  document.getElementById('detail-view').style.display = 'flex';
  document.getElementById('header-back').style.display = 'block';

  const heroEl = document.getElementById('detail-hero');
  heroEl.innerHTML = recipe.imageUrl
    ? `<img src="${htmlEscape(recipe.imageUrl)}" alt="${htmlEscape(recipe.title)}">`
    : '';

  document.getElementById('detail-title').textContent = recipe.title;
  document.getElementById('detail-meta').textContent = buildTimingMeta(recipe).join(' · ');

  document.getElementById('detail-ingredients').innerHTML =
    recipe.ingredients.map(i => `<li>${htmlEscape(i)}</li>`).join('');

  const equipment = recipe.equipment || [];
  const eqHead = document.getElementById('detail-equipment-head');
  const eqList = document.getElementById('detail-equipment');
  eqHead.style.display = equipment.length ? 'block' : 'none';
  eqList.innerHTML = equipment.map(e => `<li>${htmlEscape(e)}</li>`).join('');

  document.getElementById('detail-instructions').innerHTML =
    recipe.instructions.map(step => {
      if (typeof step === 'object' && step.section)
        return `<li><strong>${htmlEscape(step.section)}</strong></li>`;
      return `<li>${htmlEscape(step)}</li>`;
    }).join('');

  const authorNotes = recipe.authorNotes || '';
  const authorNotesHead = document.getElementById('detail-author-notes-head');
  const authorNotesEl   = document.getElementById('detail-author-notes');
  authorNotesHead.style.display = authorNotes ? 'block' : 'none';
  authorNotesEl.style.display   = authorNotes ? 'block' : 'none';
  authorNotesEl.textContent = authorNotes;

  document.getElementById('detail-notes').value = recipe.notes || '';
}

// --- List ---

async function renderList() {
  const recipes = await Storage.getAll();
  const listEl  = document.getElementById('recipe-list');
  const emptyEl = document.getElementById('empty-state');

  if (!recipes.length) {
    emptyEl.style.display = 'block';
    listEl.innerHTML = '';
    return;
  }

  emptyEl.style.display = 'none';
  listEl.innerHTML = recipes.map(r => `
    <div class="recipe-row" data-id="${r.savedAt}">
      <div class="recipe-row-info">
        <div class="recipe-title">${htmlEscape(r.title)}</div>
        <div class="recipe-meta">${formatDate(r.savedAt)}</div>
      </div>
      <button class="delete-btn" data-delete="${r.savedAt}" title="Delete">×</button>
    </div>
  `).join('');

  listEl.querySelectorAll('.recipe-row').forEach(row => {
    row.addEventListener('click', async e => {
      if (e.target.closest('.delete-btn')) return;
      const recipe = await Storage.getById(Number(row.dataset.id));
      if (recipe) showDetail(recipe);
    });
  });

  listEl.querySelectorAll('.delete-btn').forEach(btn => {
    btn.addEventListener('click', async e => {
      e.stopPropagation();
      await Storage.delete(Number(btn.dataset.delete));
      renderList();
    });
  });
}

function formatDate(ts) {
  return new Date(ts).toLocaleDateString(undefined, { month: 'short', day: 'numeric', year: 'numeric' });
}

// --- Events ---

document.getElementById('back-btn').addEventListener('click', () => {
  if (currentRecipe) {
    Storage.updateNotes(currentRecipe.savedAt, document.getElementById('detail-notes').value);
  }
  showList();
  renderList();
});

document.getElementById('detail-notes').addEventListener('blur', () => {
  if (!currentRecipe) return;
  Storage.updateNotes(currentRecipe.savedAt, document.getElementById('detail-notes').value);
});

document.getElementById('open-cached').addEventListener('click', () => {
  if (!currentRecipe) return;
  const url = chrome.runtime.getURL('cache/cache.html') + '?id=' + currentRecipe.savedAt;
  chrome.tabs.create({ url });
});

// --- Init ---
renderList();

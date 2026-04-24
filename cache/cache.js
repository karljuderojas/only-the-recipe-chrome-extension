(async function () {
  const params = new URLSearchParams(location.search);
  const id = Number(params.get('id'));
  const recipe = id ? await Storage.getById(id) : null;

  if (!recipe) {
    document.getElementById('not-found').style.display = 'block';
    return;
  }

  document.title = htmlEscape(recipe.title) + ' — Only The Recipe';

  const sourceLink = document.getElementById('source-link');
  const hostname = new URL(recipe.sourceUrl).hostname;
  sourceLink.innerHTML = `<a href="${htmlEscape(recipe.sourceUrl)}" target="_blank">${htmlEscape(hostname)}</a>`;

  const metaParts = buildTimingMeta(recipe);

  const ingredients = recipe.ingredients.map(i => `<li>${htmlEscape(i)}</li>`).join('');
  const equipment   = (recipe.equipment || []).map(e => `<li>${htmlEscape(e)}</li>`).join('');
  const instructions = recipe.instructions.map(step => {
    if (typeof step === 'object' && step.section)
      return `</ol><h3>${htmlEscape(step.section)}</h3><ol>`;
    return `<li>${htmlEscape(step)}</li>`;
  }).join('');

  const authorNotes = recipe.authorNotes || '';

  document.getElementById('recipe-body').innerHTML = `
    ${recipe.imageUrl ? `<img class="hero" src="${htmlEscape(recipe.imageUrl)}" alt="${htmlEscape(recipe.title)}">` : ''}
    <h1>${htmlEscape(recipe.title)}</h1>
    ${metaParts.length ? `<p class="meta">${metaParts.map(htmlEscape).join(' &nbsp;·&nbsp; ')}</p>` : ''}
    ${recipe.description ? `<p>${htmlEscape(recipe.description)}</p>` : ''}
    ${recipe.ingredients.length ? `<h2>Ingredients</h2><ul>${ingredients}</ul>` : ''}
    ${equipment ? `<h2>Equipment</h2><ul>${equipment}</ul>` : ''}
    ${recipe.instructions.length ? `<h2>Instructions</h2><ol>${instructions}</ol>` : ''}
    ${authorNotes ? `<div class="author-notes"><strong>Author Notes</strong>${htmlEscape(authorNotes)}</div>` : ''}
    ${recipe.notes ? `<div class="notes-block">${htmlEscape(recipe.notes)}</div>` : ''}
  `;
})();

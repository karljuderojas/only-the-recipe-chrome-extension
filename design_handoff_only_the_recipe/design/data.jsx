// Shared recipe data + tiny icon set used across all three variants.

const RECIPES = [
  {
    id: 'manakish',
    title: 'Lebanese Manakish Dough',
    source: 'cleobuttera.com',
    date: 'May 12',
    full_date: 'May 12, 2026',
    prep: '15 min', cook: '12 min', total: '2 hr 27 min', yield: '6 pieces',
    hero_label: 'manakish · flat-lay',
    ingredients: [
      '3 cups bread flour',
      '1 cup lukewarm water',
      '1 tbsp dried active yeast',
      '1 tsp salt',
      '½ tsp sugar',
      '3 tbsp olive oil',
      '2 tbsp za\u2019atar blend',
      '½ cup olive oil (for topping)',
    ],
    steps: [
      'Bloom yeast in lukewarm water with sugar for 5 minutes until foamy.',
      'Mix flour and salt in a large bowl. Pour in yeast water and 3 tbsp olive oil.',
      'Knead 8 minutes until smooth and elastic. Cover; rise 2 hours, doubled in size.',
      'Punch down, divide into 6 balls. Roll each to ¼" thick rounds.',
      'Whisk za\u2019atar with ½ cup olive oil. Spread over rounds.',
      'Bake at 450°F for 8–12 minutes until edges are golden.',
    ],
    note: 'Chilling the dough overnight develops a deeper flavor and easier-to-roll texture.',
  },
  {
    id: 'bistek',
    title: 'Bistek Tagalog',
    source: 'kawalingpinoy.com',
    date: 'Apr 27',
    full_date: 'Apr 27, 2026',
    prep: '20 min', cook: '25 min', total: '45 min', yield: '4 servings',
    hero_label: 'beefsteak · skillet',
    ingredients: [
      '1 lb beef sirloin, thinly sliced',
      '½ cup soy sauce',
      '¼ cup calamansi or lemon juice',
      '4 cloves garlic, minced',
      '1 large onion, sliced into rings',
      '2 tbsp vegetable oil',
      '1 tsp ground black pepper',
      '1 tsp brown sugar',
    ],
    steps: [
      'Marinate beef in soy sauce, calamansi juice, garlic and pepper for 30 minutes.',
      'Heat oil. Sear marinated beef in batches, set aside.',
      'Sauté onion rings until just translucent, remove.',
      'Pour marinade into pan with sugar; simmer 5 minutes.',
      'Return beef to pan, simmer 10 minutes until tender.',
      'Top with reserved onion rings before serving.',
    ],
    note: 'Slicing the beef thinly across the grain is the difference between tender and chewy.',
  },
  {
    id: 'adobo',
    title: 'Chicken Adobo',
    subtitle: 'Filipino-Style Braised Chicken',
    source: 'panlasangpinoy.com',
    date: 'Apr 25',
    full_date: 'Apr 25, 2026',
    prep: '10 min', cook: '40 min', total: '50 min', yield: '4 servings',
    hero_label: 'adobo · braised',
    ingredients: [
      '2 lbs chicken thighs, bone-in',
      '½ cup soy sauce',
      '½ cup white vinegar',
      '1 head garlic, peeled and crushed',
      '3 bay leaves',
      '1 tbsp whole peppercorns',
      '1 cup water',
      '2 tbsp vegetable oil',
    ],
    steps: [
      'Combine chicken, soy, vinegar, garlic, bay and peppercorns. Marinate 1 hour.',
      'Pour everything into a heavy pot with water. Bring to a boil.',
      'Lower heat, simmer covered 30 minutes until chicken is tender.',
      'Remove chicken, pat dry. Brown in hot oil 3 minutes per side.',
      'Return to sauce, simmer uncovered 8 minutes until glossy.',
      'Serve over jasmine rice with extra sauce spooned over.',
    ],
    note: 'Don\u2019t stir the vinegar until it boils — it tames the sharpness.',
  },
  {
    id: 'shakshuka',
    title: 'Weeknight Shakshuka',
    source: 'nytimes.com',
    date: 'Apr 19',
    full_date: 'Apr 19, 2026',
    prep: '8 min', cook: '22 min', total: '30 min', yield: '2 servings',
    hero_label: 'shakshuka · cast iron',
    ingredients: [], steps: [], note: '',
  },
  {
    id: 'miso-cookies',
    title: 'Brown Butter Miso Cookies',
    source: 'bonappetit.com',
    date: 'Apr 11',
    full_date: 'Apr 11, 2026',
    prep: '20 min', cook: '14 min', total: '34 min', yield: '18 cookies',
    hero_label: 'cookies · baking sheet',
    ingredients: [], steps: [], note: '',
  },
];

const GROCERY = [
  { text: '3 cups bread flour', done: false },
  { text: '1 cup lukewarm water', done: true },
  { text: '1 tbsp dried active yeast', done: false },
  { text: '1 tsp salt', done: true },
  { text: '½ tsp sugar', done: true },
  { text: '3 tbsp olive oil', done: false },
  { text: '2 tbsp za\u2019atar blend', done: false },
  { text: '½ cup olive oil (for topping)', done: false },
  { text: '1 lb beef sirloin', done: false },
  { text: '½ cup soy sauce', done: false },
  { text: '4 cloves garlic', done: false },
  { text: '1 large onion', done: true },
];

// ── Tiny icon primitives. Pass color via currentColor on the wrapper. ──
const Ico = {
  search: (s = 16) => (
    <svg width={s} height={s} viewBox="0 0 20 20" fill="none" stroke="currentColor" strokeWidth="1.6" strokeLinecap="round">
      <circle cx="9" cy="9" r="6"/><path d="M13.5 13.5L17 17"/>
    </svg>
  ),
  plus: (s = 16) => (
    <svg width={s} height={s} viewBox="0 0 20 20" fill="none" stroke="currentColor" strokeWidth="1.6" strokeLinecap="round">
      <path d="M10 4v12M4 10h12"/>
    </svg>
  ),
  paste: (s = 16) => (
    <svg width={s} height={s} viewBox="0 0 20 20" fill="none" stroke="currentColor" strokeWidth="1.6" strokeLinecap="round" strokeLinejoin="round">
      <rect x="5" y="3" width="10" height="3" rx="1"/>
      <path d="M5 5H4a1 1 0 00-1 1v10a1 1 0 001 1h12a1 1 0 001-1V6a1 1 0 00-1-1h-1"/>
    </svg>
  ),
  arrow: (s = 16) => (
    <svg width={s} height={s} viewBox="0 0 20 20" fill="none" stroke="currentColor" strokeWidth="1.6" strokeLinecap="round" strokeLinejoin="round">
      <path d="M4 10h12M11 5l5 5-5 5"/>
    </svg>
  ),
  back: (s = 16) => (
    <svg width={s} height={s} viewBox="0 0 20 20" fill="none" stroke="currentColor" strokeWidth="1.6" strokeLinecap="round" strokeLinejoin="round">
      <path d="M16 10H4M9 5L4 10l5 5"/>
    </svg>
  ),
  book: (s = 20) => (
    <svg width={s} height={s} viewBox="0 0 20 20" fill="none" stroke="currentColor" strokeWidth="1.5" strokeLinejoin="round">
      <path d="M3 4a1 1 0 011-1h12a1 1 0 011 1v12a1 1 0 01-1 1H4a1 1 0 01-1-1V4z"/>
      <path d="M3 14h14M7 3v14"/>
    </svg>
  ),
  cart: (s = 20) => (
    <svg width={s} height={s} viewBox="0 0 20 20" fill="none" stroke="currentColor" strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round">
      <path d="M2 3h2l2 11h10l2-7H5"/>
      <circle cx="8" cy="17" r="1.2"/><circle cx="15" cy="17" r="1.2"/>
    </svg>
  ),
  share: (s = 16) => (
    <svg width={s} height={s} viewBox="0 0 20 20" fill="none" stroke="currentColor" strokeWidth="1.6" strokeLinecap="round" strokeLinejoin="round">
      <path d="M10 13V3M6 7l4-4 4 4M4 13v3a1 1 0 001 1h10a1 1 0 001-1v-3"/>
    </svg>
  ),
  settings: (s = 18) => (
    <svg width={s} height={s} viewBox="0 0 20 20" fill="none" stroke="currentColor" strokeWidth="1.5" strokeLinecap="round">
      <circle cx="10" cy="10" r="2.5"/>
      <path d="M10 2v2M10 16v2M2 10h2M16 10h2M4.3 4.3l1.4 1.4M14.3 14.3l1.4 1.4M4.3 15.7l1.4-1.4M14.3 5.7l1.4-1.4"/>
    </svg>
  ),
  check: (s = 14) => (
    <svg width={s} height={s} viewBox="0 0 14 14" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
      <path d="M3 7l3 3 5-6"/>
    </svg>
  ),
  clock: (s = 14) => (
    <svg width={s} height={s} viewBox="0 0 16 16" fill="none" stroke="currentColor" strokeWidth="1.5" strokeLinecap="round">
      <circle cx="8" cy="8" r="6"/><path d="M8 5v3.2L10 10"/>
    </svg>
  ),
  flame: (s = 14) => (
    <svg width={s} height={s} viewBox="0 0 16 16" fill="none" stroke="currentColor" strokeWidth="1.5" strokeLinejoin="round">
      <path d="M8 14c2.8 0 5-2 5-4.5 0-2-1.5-3-2.5-4.5C9 3 9 1.5 8 1c0 2-3 3-3 6 0 2 1 3 1 3.5S5 11 5 12c0 1.4 1.2 2 3 2z"/>
    </svg>
  ),
  user: (s = 14) => (
    <svg width={s} height={s} viewBox="0 0 16 16" fill="none" stroke="currentColor" strokeWidth="1.5" strokeLinecap="round">
      <circle cx="8" cy="6" r="2.8"/><path d="M3 14c0-2.5 2.2-4.2 5-4.2s5 1.7 5 4.2"/>
    </svg>
  ),
};

// Striped hero placeholder — keeps designs honest about the "drop a photo here"
// expectation without faking food imagery.
function HeroStripe({ label, bg, stripe, fg, font = 'JetBrains Mono, ui-monospace, monospace' }) {
  return (
    <div style={{
      width: '100%', height: '100%', position: 'relative',
      background: `repeating-linear-gradient(135deg, ${bg} 0 14px, ${stripe} 14px 28px)`,
      display: 'flex', alignItems: 'flex-end', padding: 14,
      fontFamily: font, fontSize: 10, color: fg, letterSpacing: 0.04, textTransform: 'uppercase',
    }}>
      <span style={{ background: bg, padding: '4px 8px', boxShadow: `0 0 0 1px ${stripe}` }}>{label}</span>
    </div>
  );
}

Object.assign(window, { RECIPES, GROCERY, Ico, HeroStripe });

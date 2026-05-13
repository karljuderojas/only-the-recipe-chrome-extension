// V2 — Sage & Mustard (Swiss-warm)
// Geometric sans + monospace accents. Forest green ink, mustard accent on
// warm white. Disciplined grid, hairline rules, label-driven hierarchy.

const V2 = {
  bg: '#f4f1ea',
  paper: '#ebe7dc',
  ink: '#2f3e2a',
  inkDim: '#5a6754',
  muted: '#8a8a7a',
  rule: '#d8d2c2',
  accent: '#a8862a',
  accentInk: '#5a4a18',
  sans: '"Manrope", system-ui, sans-serif',
  mono: '"JetBrains Mono", ui-monospace, monospace',
};

function V2Chrome({ children, hideTabs = false, active = 'recipes' }) {
  return (
    <div style={{
      flex: 1, display: 'flex', flexDirection: 'column', background: V2.bg,
      fontFamily: V2.sans, color: V2.ink,
    }}>
      <div style={{ flex: 1, overflow: 'hidden', position: 'relative' }}>{children}</div>
      {!hideTabs && <V2Tabs active={active} />}
    </div>
  );
}

function V2Tabs({ active }) {
  const item = (key, label, idx) => {
    const on = active === key;
    return (
      <div key={key} style={{
        flex: 1, padding: '14px 0 12px', textAlign: 'center',
        position: 'relative', color: on ? V2.ink : V2.muted,
      }}>
        <div style={{
          fontFamily: V2.mono, fontSize: 10, letterSpacing: 0.2,
          textTransform: 'uppercase', fontWeight: 600,
          color: on ? V2.accent : V2.muted,
        }}>0{idx}</div>
        <div style={{
          marginTop: 3, fontSize: 13, fontWeight: on ? 700 : 500, letterSpacing: -0.1,
        }}>{label}</div>
        {on && <div style={{
          position: 'absolute', top: 0, left: '50%', transform: 'translateX(-50%)',
          width: 28, height: 2, background: V2.accent,
        }} />}
      </div>
    );
  };
  return (
    <div style={{
      borderTop: `1px solid ${V2.rule}`, background: V2.bg,
      display: 'flex',
    }}>
      {item('recipes', 'Library', 1)}
      <div style={{ width: 1, background: V2.rule }} />
      {item('grocery', 'Grocery', 2)}
    </div>
  );
}

function V2Header({ section, title, right }) {
  return (
    <div style={{ padding: '20px 20px 14px', borderBottom: `1px solid ${V2.rule}` }}>
      <div style={{
        display: 'flex', alignItems: 'center', justifyContent: 'space-between',
        marginBottom: 12,
      }}>
        <div style={{
          fontFamily: V2.mono, fontSize: 10, letterSpacing: 0.22,
          textTransform: 'uppercase', color: V2.muted,
        }}>{section}</div>
        {right}
      </div>
      <div style={{
        fontSize: 26, fontWeight: 800, letterSpacing: -0.6, lineHeight: 1,
      }}>{title}</div>
    </div>
  );
}

// ── Screen: Library ─────────────────────────────────────────
function V2Library() {
  return (
    <V2Chrome active="recipes">
      <V2Header
        section="Only the recipe / v1.1"
        title="Library."
        right={<div style={{ color: V2.muted }}>{Ico.settings(18)}</div>}
      />

      {/* URL input — sharp, hairline */}
      <div style={{
        margin: '14px 20px 0',
        background: '#fff', border: `1px solid ${V2.rule}`,
        display: 'flex', alignItems: 'center', overflow: 'hidden',
      }}>
        <div style={{ padding: '0 12px', color: V2.muted }}>{Ico.paste(14)}</div>
        <div style={{
          flex: 1, padding: '11px 0', fontFamily: V2.mono, fontSize: 11, color: V2.muted,
        }}>https://...</div>
        <button style={{
          background: V2.ink, color: V2.bg, border: 'none',
          padding: '0 16px', alignSelf: 'stretch',
          fontFamily: V2.mono, fontSize: 11, letterSpacing: 0.18,
          textTransform: 'uppercase', fontWeight: 600,
          display: 'flex', alignItems: 'center', gap: 6,
        }}>Extract <span>{Ico.arrow(12)}</span></button>
      </div>

      {/* meta row */}
      <div style={{
        display: 'flex', alignItems: 'center', gap: 10, padding: '18px 20px 8px',
        fontFamily: V2.mono, fontSize: 10, color: V2.muted, letterSpacing: 0.16,
        textTransform: 'uppercase',
      }}>
        <span>Saved · {RECIPES.length}</span>
        <span style={{ flex: 1, borderTop: `1px dashed ${V2.rule}` }} />
        <span>Newest ↓</span>
      </div>

      {/* recipe rows */}
      <div style={{ padding: '0 20px' }}>
        {RECIPES.map((r, i) => (
          <div key={r.id} style={{
            display: 'flex', gap: 14, padding: '14px 0',
            borderTop: i === 0 ? `1px solid ${V2.rule}` : `1px solid ${V2.rule}`,
          }}>
            <div style={{
              width: 48, height: 48, flexShrink: 0,
              border: `1px solid ${V2.rule}`,
              overflow: 'hidden',
            }}>
              <HeroStripe label="" bg={V2.paper} stripe={V2.bg} fg={V2.muted} font={V2.mono} />
            </div>
            <div style={{ flex: 1, minWidth: 0 }}>
              <div style={{
                fontFamily: V2.mono, fontSize: 9, letterSpacing: 0.2,
                textTransform: 'uppercase', color: V2.accent,
                display: 'flex', gap: 10, alignItems: 'center',
              }}>
                <span>R-{String(i + 1).padStart(3, '0')}</span>
                <span style={{ color: V2.muted }}>{r.date}</span>
              </div>
              <div style={{
                fontSize: 15, fontWeight: 700, letterSpacing: -0.2,
                marginTop: 4, lineHeight: 1.2,
              }}>{r.title}</div>
              <div style={{
                fontSize: 11, color: V2.muted, marginTop: 4,
              }}>{r.source}</div>
            </div>
          </div>
        ))}
      </div>
    </V2Chrome>
  );
}

// ── Screen: Recipe ──────────────────────────────────────────
function V2Recipe() {
  const r = RECIPES[0];
  return (
    <V2Chrome hideTabs>
      <div style={{ overflow: 'auto', height: '100%' }}>
        {/* toolbar */}
        <div style={{
          display: 'flex', alignItems: 'center', justifyContent: 'space-between',
          padding: '14px 18px', borderBottom: `1px solid ${V2.rule}`,
        }}>
          <div style={{
            display: 'flex', alignItems: 'center', gap: 6,
            fontFamily: V2.mono, fontSize: 10, color: V2.ink, letterSpacing: 0.16,
            textTransform: 'uppercase', fontWeight: 600,
          }}>
            {Ico.back(13)}<span>Back</span>
          </div>
          <div style={{ display: 'flex', gap: 14 }}>
            <span style={{ color: V2.inkDim }}>{Ico.share(15)}</span>
            <span style={{
              fontFamily: V2.mono, fontSize: 10, color: V2.accent, letterSpacing: 0.18,
              textTransform: 'uppercase', fontWeight: 700,
              display: 'flex', alignItems: 'center', gap: 4,
            }}>{Ico.plus(11)} List</span>
          </div>
        </div>

        {/* hero */}
        <div style={{ height: 130 }}>
          <HeroStripe label={r.hero_label} bg={V2.paper} stripe={V2.bg} fg={V2.inkDim} font={V2.mono} />
        </div>

        {/* title */}
        <div style={{ padding: '18px 20px 14px' }}>
          <div style={{
            fontFamily: V2.mono, fontSize: 10, letterSpacing: 0.22,
            textTransform: 'uppercase', color: V2.accent,
          }}>R-001 · Bread &amp; Flat</div>
          <div style={{
            fontSize: 26, fontWeight: 800, letterSpacing: -0.6, lineHeight: 1.05,
            marginTop: 8,
          }}>{r.title}</div>
        </div>

        {/* timing grid */}
        <div style={{
          display: 'grid', gridTemplateColumns: '1fr 1fr 1fr 1fr',
          borderTop: `1px solid ${V2.rule}`, borderBottom: `1px solid ${V2.rule}`,
        }}>
          {[['Prep', r.prep], ['Cook', r.cook], ['Total', r.total], ['Yield', r.yield]].map(([l, v], i) => (
            <div key={l} style={{
              padding: '12px 10px',
              borderLeft: i === 0 ? 'none' : `1px solid ${V2.rule}`,
            }}>
              <div style={{
                fontFamily: V2.mono, fontSize: 9, letterSpacing: 0.18,
                textTransform: 'uppercase', color: V2.muted,
              }}>{l}</div>
              <div style={{
                fontSize: 14, fontWeight: 700, marginTop: 3, letterSpacing: -0.2,
              }}>{v}</div>
            </div>
          ))}
        </div>

        {/* ingredients */}
        <V2SectionHead label="01" title="Ingredients" right={`${r.ingredients.length} items`} />
        <div style={{ padding: '0 20px' }}>
          {r.ingredients.slice(0, 6).map((ing, i) => {
            const m = ing.match(/^([\d¼½¾⅓⅔/.\s]+)(\w?\w?\w?)\s+(.+)$/);
            const qty = m ? (m[1] + (m[2] || '')).trim() : '';
            const rest = m ? m[3] : ing;
            return (
              <div key={i} style={{
                display: 'flex', alignItems: 'baseline', gap: 14,
                padding: '8px 0',
                borderTop: i === 0 ? 'none' : `1px solid ${V2.rule}`,
              }}>
                <div style={{
                  fontFamily: V2.mono, fontSize: 11, fontWeight: 700, color: V2.accent,
                  minWidth: 64,
                }}>{qty}</div>
                <div style={{ flex: 1, fontSize: 13.5, lineHeight: 1.4 }}>{rest}</div>
              </div>
            );
          })}
        </div>

        {/* method */}
        <V2SectionHead label="02" title="Method" right={`${r.steps.length} steps`} />
        <div style={{ padding: '4px 20px 20px' }}>
          {r.steps.slice(0, 3).map((s, i) => (
            <div key={i} style={{
              display: 'flex', gap: 14, padding: '10px 0',
              borderTop: i === 0 ? 'none' : `1px solid ${V2.rule}`,
            }}>
              <div style={{
                fontFamily: V2.mono, fontSize: 10, fontWeight: 700,
                color: V2.accent, paddingTop: 3,
                minWidth: 22,
              }}>0{i + 1}</div>
              <div style={{ flex: 1, fontSize: 13, lineHeight: 1.55 }}>{s}</div>
            </div>
          ))}
        </div>
      </div>
    </V2Chrome>
  );
}

function V2SectionHead({ label, title, right }) {
  return (
    <div style={{
      display: 'flex', alignItems: 'baseline', gap: 12,
      padding: '20px 20px 10px',
    }}>
      <div style={{
        fontFamily: V2.mono, fontSize: 10, fontWeight: 700, color: V2.accent,
        letterSpacing: 0.18,
      }}>§{label}</div>
      <div style={{ fontSize: 16, fontWeight: 800, letterSpacing: -0.3 }}>{title}</div>
      <div style={{ flex: 1, borderTop: `1px solid ${V2.rule}` }} />
      <div style={{
        fontFamily: V2.mono, fontSize: 9, color: V2.muted, letterSpacing: 0.18,
        textTransform: 'uppercase',
      }}>{right}</div>
    </div>
  );
}

// ── Screen: Grocery ─────────────────────────────────────────
function V2Grocery() {
  const remaining = GROCERY.filter((g) => !g.done);
  const done = GROCERY.filter((g) => g.done);
  return (
    <V2Chrome active="grocery">
      <V2Header
        section="Only the recipe / market"
        title="Grocery."
        right={
          <div style={{
            display: 'flex', alignItems: 'center', gap: 8,
            fontFamily: V2.mono, fontSize: 10, letterSpacing: 0.16,
            textTransform: 'uppercase', color: V2.accent,
          }}>
            <span style={{ width: 6, height: 6, borderRadius: 3, background: V2.accent }} />
            {remaining.length} open
          </div>
        }
      />

      <div style={{
        padding: '14px 20px 6px',
        fontFamily: V2.mono, fontSize: 10, letterSpacing: 0.2,
        textTransform: 'uppercase', color: V2.muted,
      }}>To buy</div>
      <div style={{ padding: '0 20px' }}>
        {remaining.map((item, i) => (
          <V2GroceryRow key={i} item={item} first={i === 0} />
        ))}
      </div>

      {done.length > 0 && (
        <>
          <div style={{
            padding: '20px 20px 6px',
            fontFamily: V2.mono, fontSize: 10, letterSpacing: 0.2,
            textTransform: 'uppercase', color: V2.muted,
            display: 'flex', justifyContent: 'space-between',
          }}>
            <span>Done · {done.length}</span>
            <span style={{ color: V2.accent }}>Clear ↻</span>
          </div>
          <div style={{ padding: '0 20px' }}>
            {done.map((item, i) => (
              <V2GroceryRow key={i} item={item} first={i === 0} />
            ))}
          </div>
        </>
      )}
    </V2Chrome>
  );
}

function V2GroceryRow({ item, first }) {
  return (
    <div style={{
      display: 'flex', alignItems: 'center', gap: 12,
      padding: '11px 0',
      borderTop: first ? `1px solid ${V2.rule}` : `1px solid ${V2.rule}`,
    }}>
      <div style={{
        width: 18, height: 18, flexShrink: 0,
        border: `1.4px solid ${item.done ? V2.accent : V2.inkDim}`,
        background: item.done ? V2.accent : 'transparent',
        color: '#fff', display: 'flex', alignItems: 'center', justifyContent: 'center',
      }}>{item.done && Ico.check(11)}</div>
      <div style={{
        flex: 1, fontSize: 14, fontWeight: 500, letterSpacing: -0.1,
        textDecoration: item.done ? 'line-through' : 'none',
        color: item.done ? V2.muted : V2.ink,
      }}>{item.text}</div>
    </div>
  );
}

window.V2 = { Library: V2Library, Recipe: V2Recipe, Grocery: V2Grocery };

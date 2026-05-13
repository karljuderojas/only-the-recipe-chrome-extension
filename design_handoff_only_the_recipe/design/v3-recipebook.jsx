// V3 — Cocoa & Tan (Recipe-book)
// All-Lora serif, warm paper, deep cocoa ink, tan accent. Cozy, friendly,
// like a well-loved cookbook. Soft shadows, generous serif numerals.

const V3 = {
  bg: '#f6f2e9',
  paper: '#ece6d4',
  ink: '#3d2817',
  inkSoft: '#6b5340',
  muted: '#9c8a76',
  rule: '#dccfb6',
  accent: '#9b6b3f',
  accentSoft: '#e8d8c0',
  serif: '"Lora", Georgia, serif',
  mono: '"JetBrains Mono", ui-monospace, monospace',
};

function V3Chrome({ children, hideTabs = false, active = 'recipes' }) {
  return (
    <div style={{
      flex: 1, display: 'flex', flexDirection: 'column', background: V3.bg,
      fontFamily: V3.serif, color: V3.ink,
    }}>
      <div style={{ flex: 1, overflow: 'hidden', position: 'relative' }}>{children}</div>
      {!hideTabs && <V3Tabs active={active} />}
    </div>
  );
}

function V3Tabs({ active }) {
  const item = (key, label, icon) => {
    const on = active === key;
    return (
      <div key={key} style={{
        flex: 1, display: 'flex', flexDirection: 'column', alignItems: 'center',
        gap: 4, color: on ? V3.accent : V3.muted, position: 'relative', padding: '10px 0 4px',
      }}>
        {on && (
          <div style={{
            position: 'absolute', top: 0, left: '50%', transform: 'translateX(-50%)',
            fontFamily: V3.serif, fontStyle: 'italic', fontSize: 10, color: V3.accent,
            marginTop: -2,
          }}>•</div>
        )}
        <div>{icon}</div>
        <div style={{
          fontFamily: V3.serif, fontSize: 13, fontStyle: 'italic', fontWeight: 500,
        }}>{label}</div>
      </div>
    );
  };
  return (
    <div style={{
      borderTop: `1px solid ${V3.rule}`, background: V3.bg,
      display: 'flex',
    }}>
      {item('recipes', 'recipes', Ico.book(18))}
      {item('grocery', 'grocery', Ico.cart(18))}
    </div>
  );
}

// Decorative ornament — three dots
function Ornament({ color = V3.accent }) {
  return (
    <div style={{
      display: 'flex', alignItems: 'center', justifyContent: 'center', gap: 6, color,
    }}>
      <span style={{ width: 3, height: 3, borderRadius: 2, background: color, opacity: 0.5 }} />
      <span style={{ width: 4, height: 4, borderRadius: 2, background: color }} />
      <span style={{ width: 3, height: 3, borderRadius: 2, background: color, opacity: 0.5 }} />
    </div>
  );
}

// ── Screen: Library ─────────────────────────────────────────
function V3Library() {
  return (
    <V3Chrome active="recipes">
      <div style={{ padding: '24px 24px 0', textAlign: 'center' }}>
        <div style={{
          fontFamily: V3.serif, fontStyle: 'italic', fontSize: 12,
          letterSpacing: 0.4, color: V3.muted, textTransform: 'lowercase',
        }}>~ a little cookbook ~</div>
        <h1 style={{
          margin: '8px 0 0', fontFamily: V3.serif, fontSize: 30, lineHeight: 1.05,
          fontWeight: 500, color: V3.ink,
        }}>Only the Recipe</h1>
        <div style={{ marginTop: 10 }}><Ornament /></div>
      </div>

      {/* URL bar */}
      <div style={{
        margin: '20px 22px 0', padding: '12px 14px',
        background: '#fffdf7', border: `1px solid ${V3.rule}`, borderRadius: 6,
        display: 'flex', alignItems: 'center', gap: 10,
        boxShadow: `inset 0 -2px 0 ${V3.rule}`,
      }}>
        <span style={{ color: V3.muted }}>{Ico.paste(14)}</span>
        <span style={{
          flex: 1, fontFamily: V3.serif, fontStyle: 'italic', fontSize: 13,
          color: V3.muted,
        }}>paste a recipe link…</span>
        <span style={{
          color: V3.accent, fontFamily: V3.serif, fontSize: 13, fontStyle: 'italic',
          fontWeight: 600, display: 'flex', alignItems: 'center', gap: 4,
        }}>fetch {Ico.arrow(12)}</span>
      </div>

      <div style={{
        margin: '22px 24px 12px', display: 'flex', alignItems: 'baseline', gap: 10,
      }}>
        <h2 style={{
          margin: 0, fontFamily: V3.serif, fontStyle: 'italic', fontSize: 17,
          fontWeight: 500, color: V3.inkSoft,
        }}>My recipes</h2>
        <div style={{ flex: 1, borderTop: `1px dotted ${V3.rule}` }} />
        <div style={{
          fontFamily: V3.serif, fontStyle: 'italic', fontSize: 12, color: V3.muted,
        }}>{RECIPES.length} saved</div>
      </div>

      <div style={{ padding: '0 22px' }}>
        {RECIPES.map((r, i) => (
          <div key={r.id} style={{
            display: 'flex', alignItems: 'center', gap: 14,
            padding: '12px 0',
            borderTop: i === 0 ? 'none' : `1px dotted ${V3.rule}`,
          }}>
            <div style={{
              width: 44, height: 44, flexShrink: 0,
              borderRadius: 22, overflow: 'hidden',
              border: `1px solid ${V3.rule}`,
            }}>
              <HeroStripe label="" bg={V3.paper} stripe={V3.bg} fg={V3.muted} font={V3.mono} />
            </div>
            <div style={{ flex: 1, minWidth: 0 }}>
              <div style={{
                fontFamily: V3.serif, fontSize: 16, lineHeight: 1.2, fontWeight: 500,
              }}>{r.title}</div>
              <div style={{
                fontFamily: V3.serif, fontStyle: 'italic', fontSize: 12,
                color: V3.muted, marginTop: 2,
              }}>saved {r.full_date}</div>
            </div>
            <div style={{
              color: V3.accent, fontFamily: V3.serif, fontStyle: 'italic',
              fontSize: 12,
            }}>share</div>
          </div>
        ))}
      </div>
    </V3Chrome>
  );
}

// ── Screen: Recipe ──────────────────────────────────────────
function V3Recipe() {
  const r = RECIPES[0];
  return (
    <V3Chrome hideTabs>
      <div style={{ overflow: 'auto', height: '100%' }}>
        {/* toolbar */}
        <div style={{
          display: 'flex', alignItems: 'center', justifyContent: 'space-between',
          padding: '14px 18px 6px',
        }}>
          <div style={{
            display: 'flex', alignItems: 'center', gap: 6,
            fontFamily: V3.serif, fontStyle: 'italic', fontSize: 13,
            color: V3.inkSoft,
          }}>{Ico.back(14)}<span>back to shelf</span></div>
          <div style={{
            fontFamily: V3.serif, fontStyle: 'italic', fontSize: 13,
            color: V3.accent, display: 'flex', alignItems: 'center', gap: 4,
          }}>{Ico.plus(13)} to list</div>
        </div>

        {/* hero */}
        <div style={{
          margin: '10px 18px 0', height: 140, borderRadius: 6, overflow: 'hidden',
          border: `1px solid ${V3.rule}`,
        }}>
          <HeroStripe label={r.hero_label} bg={V3.paper} stripe={V3.bg} fg={V3.inkSoft} font={V3.mono} />
        </div>

        {/* title block */}
        <div style={{ padding: '18px 24px 4px', textAlign: 'center' }}>
          <div style={{
            fontFamily: V3.serif, fontStyle: 'italic', fontSize: 12,
            color: V3.accent, letterSpacing: 0.3,
          }}>a recipe for</div>
          <h1 style={{
            margin: '4px 0 0', fontFamily: V3.serif, fontSize: 26, lineHeight: 1.1,
            fontWeight: 500, color: V3.ink,
          }}>{r.title}</h1>
          <div style={{
            marginTop: 8, fontFamily: V3.serif, fontStyle: 'italic', fontSize: 12,
            color: V3.muted,
          }}>adapted from {r.source}</div>
          <div style={{ marginTop: 14 }}><Ornament /></div>
        </div>

        {/* timing pills */}
        <div style={{
          display: 'flex', gap: 10, padding: '16px 24px 4px', justifyContent: 'center',
          flexWrap: 'wrap',
        }}>
          {[
            ['prep', r.prep, Ico.clock(11)],
            ['cook', r.cook, Ico.flame(11)],
            ['serves', r.yield, Ico.user(11)],
          ].map(([l, v, ic]) => (
            <div key={l} style={{
              display: 'flex', alignItems: 'center', gap: 6,
              padding: '6px 12px', background: V3.accentSoft, borderRadius: 14,
              fontFamily: V3.serif, fontSize: 11, color: V3.ink,
            }}>
              <span style={{ color: V3.accent }}>{ic}</span>
              <span style={{ fontStyle: 'italic', color: V3.inkSoft }}>{l}</span>
              <span style={{ fontWeight: 600 }}>{v}</span>
            </div>
          ))}
        </div>

        {/* ingredients */}
        <div style={{ padding: '20px 24px 0' }}>
          <h2 style={{
            margin: 0, fontFamily: V3.serif, fontStyle: 'italic', fontSize: 19,
            fontWeight: 500, color: V3.ink,
          }}>Ingredients</h2>
          <div style={{ marginTop: 10 }}>
            {r.ingredients.slice(0, 6).map((ing, i) => {
              const m = ing.match(/^([\d¼½¾⅓⅔/.\s]+)(\w?\w?\w?)\s+(.+)$/);
              const qty = m ? (m[1] + (m[2] || '')).trim() : '';
              const rest = m ? m[3] : ing;
              return (
                <div key={i} style={{
                  display: 'flex', gap: 10, alignItems: 'baseline',
                  padding: '7px 0', borderBottom: `1px dotted ${V3.rule}`,
                  fontFamily: V3.serif, fontSize: 14,
                }}>
                  <span style={{
                    fontFamily: V3.serif, fontStyle: 'italic', fontWeight: 600,
                    color: V3.accent, minWidth: 64,
                  }}>{qty}</span>
                  <span style={{ flex: 1, lineHeight: 1.4 }}>{rest}</span>
                </div>
              );
            })}
          </div>
        </div>

        {/* method */}
        <div style={{ padding: '22px 24px 16px' }}>
          <h2 style={{
            margin: '0 0 14px', fontFamily: V3.serif, fontStyle: 'italic', fontSize: 19,
            fontWeight: 500, color: V3.ink,
          }}>Method</h2>
          {r.steps.slice(0, 3).map((s, i) => (
            <div key={i} style={{ display: 'flex', gap: 14, marginBottom: 12 }}>
              <div style={{
                fontFamily: V3.serif, fontSize: 28, fontStyle: 'italic',
                color: V3.accent, lineHeight: 0.95, minWidth: 24,
                fontWeight: 500,
              }}>{i + 1}.</div>
              <div style={{
                flex: 1, fontFamily: V3.serif, fontSize: 14, lineHeight: 1.55,
                color: V3.ink,
              }}>{s}</div>
            </div>
          ))}
          <div style={{
            margin: '20px 0 0', padding: '14px 16px',
            background: V3.accentSoft, borderRadius: 6,
            fontFamily: V3.serif, fontSize: 13, lineHeight: 1.5,
            color: '#5a3a20',
          }}>
            <div style={{
              fontStyle: 'italic', color: V3.accent, fontSize: 12, marginBottom: 4,
            }}>— a small note —</div>
            {r.note}
          </div>
        </div>
      </div>
    </V3Chrome>
  );
}

// ── Screen: Grocery ─────────────────────────────────────────
function V3Grocery() {
  const remaining = GROCERY.filter((g) => !g.done).length;
  return (
    <V3Chrome active="grocery">
      <div style={{ padding: '22px 24px 0', textAlign: 'center' }}>
        <div style={{
          fontFamily: V3.serif, fontStyle: 'italic', fontSize: 12,
          letterSpacing: 0.4, color: V3.muted, textTransform: 'lowercase',
        }}>~ off to the market ~</div>
        <h1 style={{
          margin: '8px 0 0', fontFamily: V3.serif, fontSize: 28, lineHeight: 1,
          fontWeight: 500,
        }}>Grocery list</h1>
        <div style={{ marginTop: 10 }}><Ornament /></div>
      </div>

      <div style={{
        margin: '18px 22px 12px',
        display: 'flex', alignItems: 'center', justifyContent: 'space-between',
      }}>
        <div style={{
          fontFamily: V3.serif, fontStyle: 'italic', fontSize: 13, color: V3.inkSoft,
        }}>{remaining} things to gather</div>
        <div style={{
          fontFamily: V3.serif, fontStyle: 'italic', fontSize: 12, color: V3.accent,
        }}>clear done</div>
      </div>

      <div style={{ padding: '0 24px' }}>
        {GROCERY.map((item, i) => (
          <div key={i} style={{
            display: 'flex', alignItems: 'center', gap: 14,
            padding: '11px 0',
            borderTop: `1px dotted ${V3.rule}`,
            opacity: item.done ? 0.5 : 1,
          }}>
            <div style={{
              width: 18, height: 18, borderRadius: '50%',
              border: `1.5px solid ${item.done ? V3.accent : V3.inkSoft}`,
              background: item.done ? V3.accent : 'transparent',
              color: '#fff', display: 'flex', alignItems: 'center', justifyContent: 'center',
              flexShrink: 0,
            }}>{item.done && Ico.check(10)}</div>
            <div style={{
              flex: 1, fontFamily: V3.serif, fontSize: 14.5, lineHeight: 1.3,
              textDecoration: item.done ? 'line-through' : 'none',
              fontStyle: item.done ? 'italic' : 'normal',
            }}>{item.text}</div>
          </div>
        ))}
      </div>
    </V3Chrome>
  );
}

window.V3 = { Library: V3Library, Recipe: V3Recipe, Grocery: V3Grocery };

// V1 — Cream & Terracotta (Editorial)
// Magazine-y. Cormorant Garamond display + Manrope body. Cream paper,
// terracotta accent, generous whitespace, deliberate text hierarchy.

const V1 = {
  bg: '#faf8f3',
  paper: '#f3efe5',
  ink: '#1f1a14',
  muted: '#8a7f6d',
  rule: '#e6e0d0',
  accent: '#c2602f',
  accentSoft: '#f1dccc',
  serif: '"Cormorant Garamond", "Cormorant", Georgia, serif',
  sans: '"Manrope", system-ui, sans-serif',
  mono: '"JetBrains Mono", ui-monospace, monospace',
};

function V1Chrome({ children, hideTabs = false, active = 'recipes' }) {
  return (
    <div style={{
      flex: 1, display: 'flex', flexDirection: 'column', background: V1.bg,
      fontFamily: V1.sans, color: V1.ink,
    }}>
      <div style={{ flex: 1, overflow: 'hidden', position: 'relative' }}>{children}</div>
      {!hideTabs && <V1Tabs active={active} />}
    </div>
  );
}

function V1Tabs({ active }) {
  const item = (key, label, icon) => {
    const on = active === key;
    return (
      <div key={key} style={{
        flex: 1, display: 'flex', flexDirection: 'column', alignItems: 'center',
        gap: 4, color: on ? V1.accent : V1.muted,
      }}>
        <div style={{ color: 'inherit' }}>{icon}</div>
        <div style={{
          fontFamily: V1.serif, fontSize: 13, letterSpacing: 0.2,
          fontStyle: on ? 'italic' : 'normal',
          fontWeight: on ? 600 : 500,
        }}>{label}</div>
      </div>
    );
  };
  return (
    <div style={{
      borderTop: `1px solid ${V1.rule}`, background: V1.bg,
      display: 'flex', padding: '10px 0 6px',
    }}>
      {item('recipes', 'Recipes', Ico.book(20))}
      {item('grocery', 'Grocery', Ico.cart(20))}
    </div>
  );
}

// ── Screen: Library ─────────────────────────────────────────
function V1Library() {
  return (
    <V1Chrome active="recipes">
      <div style={{ padding: '24px 22px 0' }}>
        <div style={{
          display: 'flex', alignItems: 'flex-end', justifyContent: 'space-between',
          marginBottom: 4,
        }}>
          <div style={{
            fontFamily: V1.mono, fontSize: 10, letterSpacing: 0.18, textTransform: 'uppercase',
            color: V1.muted,
          }}>No. 01 — Library</div>
          <div style={{ color: V1.muted }}>{Ico.settings(16)}</div>
        </div>
        <h1 style={{
          margin: 0, fontFamily: V1.serif, fontSize: 40, lineHeight: 1, letterSpacing: -1,
          fontStyle: 'italic', fontWeight: 500,
        }}>Only the<br/>recipe.</h1>
        <p style={{
          margin: '12px 0 18px', fontSize: 12.5, lineHeight: 1.55, color: V1.muted, maxWidth: 240,
        }}>A quiet shelf of recipes you actually use — nothing more.</p>

        {/* URL bar */}
        <div style={{
          display: 'flex', alignItems: 'center', gap: 10,
          padding: '10px 12px', background: '#fff',
          border: `1px solid ${V1.rule}`, borderRadius: 2,
        }}>
          <span style={{ color: V1.muted }}>{Ico.paste(14)}</span>
          <span style={{ flex: 1, fontSize: 12, color: V1.muted, fontFamily: V1.mono }}>
            paste a recipe url
          </span>
          <span style={{
            color: V1.accent, fontFamily: V1.mono, fontSize: 11,
            letterSpacing: 0.1, textTransform: 'uppercase',
          }}>Get →</span>
        </div>
      </div>

      <div style={{
        display: 'flex', alignItems: 'center', gap: 12,
        padding: '26px 22px 10px',
      }}>
        <div style={{
          fontFamily: V1.serif, fontStyle: 'italic', fontSize: 18,
        }}>Saved</div>
        <div style={{ flex: 1, height: 1, background: V1.rule }} />
        <div style={{ fontFamily: V1.mono, fontSize: 10, color: V1.muted }}>5</div>
      </div>

      <div>
        {RECIPES.map((r, i) => (
          <div key={r.id} style={{
            padding: '14px 22px',
            borderTop: i === 0 ? 'none' : `1px solid ${V1.rule}`,
            display: 'flex', alignItems: 'flex-start', gap: 14,
          }}>
            <div style={{
              fontFamily: V1.mono, fontSize: 10, color: V1.muted, paddingTop: 4,
              minWidth: 22, textAlign: 'right',
            }}>{String(i + 1).padStart(2, '0')}</div>
            <div style={{ flex: 1, minWidth: 0 }}>
              <div style={{
                fontFamily: V1.serif, fontSize: 19, fontWeight: 500, lineHeight: 1.15,
                color: V1.ink,
              }}>{r.title}</div>
              <div style={{
                marginTop: 4, fontSize: 11, color: V1.muted,
                display: 'flex', gap: 8, alignItems: 'center',
              }}>
                <span>{r.source}</span>
                <span style={{ width: 2, height: 2, borderRadius: 1, background: V1.muted }} />
                <span style={{ fontFamily: V1.mono, fontSize: 10 }}>{r.date}</span>
              </div>
            </div>
            <div style={{ color: V1.muted, paddingTop: 4 }}>{Ico.share(14)}</div>
          </div>
        ))}
      </div>
    </V1Chrome>
  );
}

// ── Screen: Recipe detail ───────────────────────────────────
function V1Recipe() {
  const r = RECIPES[0];
  return (
    <V1Chrome active="recipes">
      <div style={{ overflow: 'auto', height: '100%' }}>
        {/* toolbar */}
        <div style={{
          display: 'flex', alignItems: 'center', justifyContent: 'space-between',
          padding: '14px 18px 6px',
        }}>
          <div style={{
            display: 'flex', alignItems: 'center', gap: 6,
            fontFamily: V1.mono, fontSize: 10, color: V1.muted, letterSpacing: 0.1,
            textTransform: 'uppercase',
          }}>
            <span>{Ico.back(13)}</span><span>Library</span>
          </div>
          <div style={{
            fontFamily: V1.mono, fontSize: 10, color: V1.accent, letterSpacing: 0.12,
            textTransform: 'uppercase',
          }}>+ List</div>
        </div>

        {/* hero */}
        <div style={{
          margin: '8px 18px 0', height: 150,
          border: `1px solid ${V1.rule}`,
        }}>
          <HeroStripe label={r.hero_label} bg={V1.paper} stripe={V1.bg} fg={V1.muted} font={V1.mono} />
        </div>

        {/* title block */}
        <div style={{ padding: '18px 22px 4px' }}>
          <div style={{
            fontFamily: V1.mono, fontSize: 10, letterSpacing: 0.18,
            textTransform: 'uppercase', color: V1.accent,
          }}>Lebanese · 6 pieces</div>
          <h1 style={{
            margin: '6px 0 0', fontFamily: V1.serif, fontSize: 30, lineHeight: 1.05,
            letterSpacing: -0.5, fontWeight: 500,
          }}>{r.title}</h1>
          <div style={{
            margin: '10px 0 0', fontFamily: V1.serif, fontStyle: 'italic', fontSize: 13,
            color: V1.muted, lineHeight: 1.5,
          }}>via {r.source}</div>
        </div>

        {/* timing strip */}
        <div style={{
          display: 'flex', gap: 0, margin: '16px 22px 0',
          borderTop: `1px solid ${V1.rule}`, borderBottom: `1px solid ${V1.rule}`,
        }}>
          {[['Prep', r.prep], ['Cook', r.cook], ['Total', r.total]].map(([l, v], i) => (
            <div key={l} style={{
              flex: 1, padding: '10px 0', textAlign: 'center',
              borderLeft: i === 0 ? 'none' : `1px solid ${V1.rule}`,
            }}>
              <div style={{
                fontFamily: V1.mono, fontSize: 9, color: V1.muted, letterSpacing: 0.16,
                textTransform: 'uppercase',
              }}>{l}</div>
              <div style={{ fontFamily: V1.serif, fontSize: 16, marginTop: 2 }}>{v}</div>
            </div>
          ))}
        </div>

        {/* ingredients */}
        <div style={{ padding: '20px 22px 10px' }}>
          <div style={{
            display: 'flex', alignItems: 'baseline', gap: 10, marginBottom: 8,
          }}>
            <div style={{
              fontFamily: V1.serif, fontStyle: 'italic', fontSize: 18,
            }}>Ingredients</div>
            <div style={{ flex: 1, height: 1, background: V1.rule }} />
          </div>
          {r.ingredients.slice(0, 6).map((ing, i) => {
            const m = ing.match(/^([\d¼½¾⅓⅔/.\s]+)(\w?\w?\w?)\s+(.+)$/);
            const qty = m ? (m[1] + (m[2] || '')).trim() : '';
            const rest = m ? m[3] : ing;
            return (
              <div key={i} style={{
                display: 'flex', gap: 14, padding: '7px 0',
                borderTop: i === 0 ? 'none' : `1px dotted ${V1.rule}`,
                fontSize: 13.5, lineHeight: 1.4,
              }}>
                <div style={{
                  fontFamily: V1.mono, fontSize: 11, color: V1.accent, minWidth: 60, paddingTop: 1,
                }}>{qty}</div>
                <div style={{ flex: 1 }}>{rest}</div>
              </div>
            );
          })}
        </div>

        {/* method */}
        <div style={{ padding: '12px 22px 24px' }}>
          <div style={{
            display: 'flex', alignItems: 'baseline', gap: 10, marginBottom: 10,
          }}>
            <div style={{
              fontFamily: V1.serif, fontStyle: 'italic', fontSize: 18,
            }}>Method</div>
            <div style={{ flex: 1, height: 1, background: V1.rule }} />
          </div>
          {r.steps.slice(0, 3).map((s, i) => (
            <div key={i} style={{ display: 'flex', gap: 14, marginBottom: 12 }}>
              <div style={{
                fontFamily: V1.serif, fontSize: 22, fontStyle: 'italic',
                color: V1.accent, lineHeight: 1, minWidth: 22,
              }}>{i + 1}</div>
              <div style={{ flex: 1, fontSize: 13.5, lineHeight: 1.55 }}>{s}</div>
            </div>
          ))}
          <div style={{
            margin: '14px 0 0', padding: '12px 14px',
            background: V1.accentSoft, borderLeft: `2px solid ${V1.accent}`,
            fontFamily: V1.serif, fontStyle: 'italic', fontSize: 13, lineHeight: 1.5,
            color: '#5a3520',
          }}>
            <span style={{
              fontFamily: V1.mono, fontStyle: 'normal', fontSize: 9,
              letterSpacing: 0.2, textTransform: 'uppercase', display: 'block',
              marginBottom: 4, color: V1.accent,
            }}>Cook&rsquo;s note</span>
            {r.note}
          </div>
        </div>
      </div>
    </V1Chrome>
  );
}

// ── Screen: Grocery list ────────────────────────────────────
function V1Grocery() {
  const remaining = GROCERY.filter((g) => !g.done).length;
  return (
    <V1Chrome active="grocery">
      <div style={{ padding: '24px 22px 0' }}>
        <div style={{
          fontFamily: V1.mono, fontSize: 10, letterSpacing: 0.18, textTransform: 'uppercase',
          color: V1.muted, marginBottom: 4,
        }}>No. 02 — Market</div>
        <h1 style={{
          margin: 0, fontFamily: V1.serif, fontSize: 40, lineHeight: 1, letterSpacing: -1,
          fontStyle: 'italic', fontWeight: 500,
        }}>Grocery<br/>list.</h1>
        <div style={{
          margin: '14px 0 18px', display: 'flex', alignItems: 'center', gap: 10,
        }}>
          <div style={{
            fontFamily: V1.mono, fontSize: 10, color: V1.accent, letterSpacing: 0.16,
            textTransform: 'uppercase',
          }}>{remaining} to buy</div>
          <div style={{ flex: 1, height: 1, background: V1.rule }} />
          <div style={{
            fontFamily: V1.mono, fontSize: 10, color: V1.muted, letterSpacing: 0.16,
            textTransform: 'uppercase',
          }}>Clear done</div>
        </div>
      </div>

      <div>
        {GROCERY.map((item, i) => (
          <div key={i} style={{
            display: 'flex', alignItems: 'center', gap: 14,
            padding: '11px 22px',
            borderTop: i === 0 ? `1px solid ${V1.rule}` : `1px dotted ${V1.rule}`,
            opacity: item.done ? 0.45 : 1,
          }}>
            <div style={{
              width: 16, height: 16, borderRadius: '50%',
              border: `1.4px solid ${item.done ? V1.accent : V1.muted}`,
              background: item.done ? V1.accent : 'transparent',
              color: '#fff', display: 'flex', alignItems: 'center', justifyContent: 'center',
            }}>{item.done && Ico.check(10)}</div>
            <div style={{
              flex: 1, fontSize: 14, lineHeight: 1.3,
              textDecoration: item.done ? 'line-through' : 'none',
              textDecorationColor: V1.muted,
            }}>{item.text}</div>
          </div>
        ))}
      </div>
    </V1Chrome>
  );
}

// ── Screen: Empty library ───────────────────────────────────
function V1Empty() {
  return (
    <V1Chrome active="recipes">
      <div style={{ padding: '24px 22px 0' }}>
        <div style={{
          display: 'flex', alignItems: 'flex-end', justifyContent: 'space-between',
          marginBottom: 4,
        }}>
          <div style={{
            fontFamily: V1.mono, fontSize: 10, letterSpacing: 0.18, textTransform: 'uppercase',
            color: V1.muted,
          }}>No. 01 — Library</div>
          <div style={{ color: V1.muted }}>{Ico.settings(16)}</div>
        </div>
        <h1 style={{
          margin: 0, fontFamily: V1.serif, fontSize: 40, lineHeight: 1, letterSpacing: -1,
          fontStyle: 'italic', fontWeight: 500,
        }}>Only the<br/>recipe.</h1>
        <p style={{
          margin: '12px 0 18px', fontSize: 12.5, lineHeight: 1.55, color: V1.muted, maxWidth: 240,
        }}>A quiet shelf of recipes you actually use — nothing more.</p>

        <div style={{
          display: 'flex', alignItems: 'center', gap: 10,
          padding: '10px 12px', background: '#fff',
          border: `1px solid ${V1.rule}`, borderRadius: 2,
        }}>
          <span style={{ color: V1.muted }}>{Ico.paste(14)}</span>
          <span style={{ flex: 1, fontSize: 12, color: V1.muted, fontFamily: V1.mono }}>
            paste a recipe url
          </span>
          <span style={{
            color: V1.accent, fontFamily: V1.mono, fontSize: 11,
            letterSpacing: 0.1, textTransform: 'uppercase',
          }}>Get →</span>
        </div>
      </div>

      <div style={{
        flex: 1, display: 'flex', flexDirection: 'column', alignItems: 'center',
        justifyContent: 'center', textAlign: 'center', padding: '60px 36px',
      }}>
        <div style={{
          width: 64, height: 64, borderRadius: 32,
          border: `1px dashed ${V1.muted}`,
          display: 'flex', alignItems: 'center', justifyContent: 'center',
          color: V1.muted, marginBottom: 18,
        }}>{Ico.book(28)}</div>
        <div style={{
          fontFamily: V1.serif, fontSize: 22, fontStyle: 'italic', fontWeight: 500,
          lineHeight: 1.15,
        }}>Your shelf is bare.</div>
        <div style={{
          marginTop: 10, fontSize: 12.5, color: V1.muted, lineHeight: 1.55, maxWidth: 220,
        }}>Paste a link above, or share a recipe from your browser. We&rsquo;ll keep just the parts you actually need.</div>
      </div>
    </V1Chrome>
  );
}

// ── Screen: Settings ───────────────────────────────────────
function V1Settings() {
  const Row = ({ label, hint, control }) => (
    <div style={{
      display: 'flex', alignItems: 'center', gap: 14,
      padding: '14px 22px', borderTop: `1px solid ${V1.rule}`,
    }}>
      <div style={{ flex: 1 }}>
        <div style={{ fontFamily: V1.serif, fontSize: 16, fontWeight: 500 }}>{label}</div>
        {hint && <div style={{ fontSize: 11, color: V1.muted, marginTop: 2 }}>{hint}</div>}
      </div>
      {control}
    </div>
  );
  const Toggle = ({ on }) => (
    <div style={{
      width: 36, height: 20, borderRadius: 10, padding: 2,
      background: on ? V1.accent : V1.rule,
      display: 'flex', alignItems: 'center',
      justifyContent: on ? 'flex-end' : 'flex-start',
    }}>
      <div style={{ width: 16, height: 16, borderRadius: 8, background: '#fff' }} />
    </div>
  );
  const Pick = ({ value }) => (
    <div style={{
      fontFamily: V1.mono, fontSize: 11, color: V1.accent,
      letterSpacing: 0.1, textTransform: 'uppercase',
      display: 'flex', alignItems: 'center', gap: 4,
    }}>{value} →</div>
  );
  return (
    <V1Chrome active="recipes">
      <div style={{ padding: '24px 22px 0' }}>
        <div style={{
          display: 'flex', alignItems: 'center', gap: 6,
          fontFamily: V1.mono, fontSize: 10, color: V1.muted, letterSpacing: 0.1,
          textTransform: 'uppercase', marginBottom: 8,
        }}>{Ico.back(13)}<span>Back</span></div>
        <h1 style={{
          margin: 0, fontFamily: V1.serif, fontSize: 40, lineHeight: 1, letterSpacing: -1,
          fontStyle: 'italic', fontWeight: 500,
        }}>Settings.</h1>
        <p style={{
          margin: '10px 0 18px', fontSize: 12.5, lineHeight: 1.55, color: V1.muted, maxWidth: 240,
        }}>Everything is stored on this device.</p>
      </div>

      <div style={{
        fontFamily: V1.mono, fontSize: 10, color: V1.muted, letterSpacing: 0.18,
        textTransform: 'uppercase', padding: '10px 22px 4px',
      }}>Preferences</div>

      <Row
        label="Use metric units"
        hint="Convert measurements as recipes are rendered"
        control={<Toggle on={true} />}
      />
      <Row
        label="Default servings"
        hint="Scaling applied to ingredient quantities"
        control={<Pick value="As written" />}
      />
      <Row
        label="Show cook’s notes"
        hint="Author tips at the end of each recipe"
        control={<Toggle on={true} />}
      />

      <div style={{
        fontFamily: V1.mono, fontSize: 10, color: V1.muted, letterSpacing: 0.18,
        textTransform: 'uppercase', padding: '22px 22px 4px',
      }}>Data</div>
      <Row label="Export library" hint="Save all recipes as a .json file" control={<Pick value="Export" />} />
      <Row label="Clear grocery list" hint="Removes every item" control={<Pick value="Clear" />} />

      <div style={{
        textAlign: 'center', padding: '36px 22px 22px',
        fontFamily: V1.mono, fontSize: 10, color: V1.muted, letterSpacing: 0.18,
      }}>Only the Recipe · v1.1</div>
    </V1Chrome>
  );
}

window.V1 = {
  Library: V1Library, Recipe: V1Recipe, Grocery: V1Grocery,
  Empty: V1Empty, Settings: V1Settings,
};

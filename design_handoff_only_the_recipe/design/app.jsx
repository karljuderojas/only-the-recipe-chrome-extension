// app.jsx — V1 chosen direction. Five screens in a DesignCanvas.
// Each artboard wraps an AndroidDevice with no app-bar title; the variant's
// own header lives inside the content area. Bottom tab bar is shown on
// every screen so users can move between Recipes and Grocery from anywhere.

const PHONE_W = 360;
const PHONE_H = 740;

function Phone({ children, dark = false }) {
  return (
    <AndroidDevice width={PHONE_W} height={PHONE_H} dark={dark}>
      {children}
    </AndroidDevice>
  );
}

function App() {
  return (
    <DesignCanvas>
      <DCSection
        id="v1-core"
        title="Only the Recipe — Cream & Terracotta"
        subtitle="Cormorant Garamond display, Manrope body, JetBrains Mono labels, terracotta accent on cream paper">
        <DCArtboard id="v1-library" label="Library" width={PHONE_W} height={PHONE_H}>
          <Phone><V1.Library /></Phone>
        </DCArtboard>
        <DCArtboard id="v1-recipe" label="Recipe detail" width={PHONE_W} height={PHONE_H}>
          <Phone><V1.Recipe /></Phone>
        </DCArtboard>
        <DCArtboard id="v1-grocery" label="Grocery list" width={PHONE_W} height={PHONE_H}>
          <Phone><V1.Grocery /></Phone>
        </DCArtboard>
        <DCArtboard id="v1-empty" label="Empty state" width={PHONE_W} height={PHONE_H}>
          <Phone><V1.Empty /></Phone>
        </DCArtboard>
        <DCArtboard id="v1-settings" label="Settings" width={PHONE_W} height={PHONE_H}>
          <Phone><V1.Settings /></Phone>
        </DCArtboard>
      </DCSection>

      <DCSection
        id="v2-archive"
        title="Archived directions"
        subtitle="Earlier explorations — kept for reference, click any to expand">
        <DCArtboard id="v2-library" label="V2 Swiss · Library" width={PHONE_W} height={PHONE_H}>
          <Phone><V2.Library /></Phone>
        </DCArtboard>
        <DCArtboard id="v2-recipe" label="V2 Swiss · Recipe" width={PHONE_W} height={PHONE_H}>
          <Phone><V2.Recipe /></Phone>
        </DCArtboard>
        <DCArtboard id="v3-library" label="V3 Recipe-book · Library" width={PHONE_W} height={PHONE_H}>
          <Phone><V3.Library /></Phone>
        </DCArtboard>
        <DCArtboard id="v3-recipe" label="V3 Recipe-book · Recipe" width={PHONE_W} height={PHONE_H}>
          <Phone><V3.Recipe /></Phone>
        </DCArtboard>
      </DCSection>
    </DesignCanvas>
  );
}

ReactDOM.createRoot(document.getElementById('root')).render(<App />);

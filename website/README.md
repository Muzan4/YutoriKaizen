# 🍃 Yutori Kaizen — Web Experience

> *"Your brain wasn't lazy. It was out-engineered."*

This is the official web companion and download portal for **Yutori Kaizen** (ゆとり改善) — an intentional, open-source Android countermeasure against compulsive screen time, phantom phone checks, and impulsive spending.

Instead of lecturing users with guilt meters, hostile screen locks, or sterile blocklists, Yutori Kaizen takes inspiration from two Japanese principles:
- **Yutori (ゆとり)**: Leaving spacious room to breathe rather than packing every waking second.
- **Kaizen (改善)**: Gentle, compounding 1% shifts that stick without burnout.

The website was designed to feel like a quiet sanctuary on the web — dark paper textures, warm sage green accents, responsive typography, and tactile canvas animations.

---

## ✨ What's Inside

- **Interactive Wordmark (`<TechText />`)**: Vector dashed outlines, live letter dragging, click kicks, and assemble intro physics.
- **Section Particle Headings (`<ParticleText />`)**: High-density interactive particle headers with touch & cursor repulsion, click bursts, and responsive multi-line word wrapping on mobile.
- **Urge Surfing Simulator**: A live, working browser recreation of the app's standalone 90-second Surge Bridge. Experience how a craving crests and subsides before reaching for a feed.
- **Ribi Companion Showcase**: Real-time canvas sprite player running authentic Godot companion animations (`idle`, `hop`, `breath`, `scared`) with a 11-outfit wardrobe switcher.
- **7-Part Bento Feature Grid**: Transparent breakdowns of the App Intercept, Floating Quick Dial, Mindful Expense Diary, Attention Clarity Index (NCI), Deep Work Blocks, Screen-Free Breaks, and Daily Habits.
- **Google Drive Direct Download Portal**: Clean, friction-free APK download card with a 3-step sideloading guide and privacy assurances (100% on-device, zero telemetry).

---

## 🛠️ Tech Stack

- **Framework**: React 18 + Vite
- **Styling**: Tailwind CSS + Custom cozy palette (`cozy-950`, `moss`, `parchment`, `surge-orange`)
- **Typography**: *Fraunces* (Editorial Serif), *DM Sans* (Clean Body), *Caveat* (Warm Handwritten Notes)
- **Animation & Physics**: GSAP, Custom HTML5 Canvas Particle Engine, React Bits
- **Icons**: Lucide React

---

## 🚀 Getting Started

### Prerequisites
- Node.js 18+
- npm or pnpm

### Installation

```bash
# Clone the repository
git clone https://github.com/Muzan4/yutori-kaizen.git

# Enter the website folder
cd yutori-kaizen/website

# Install packages
npm install

# Start local dev server
npm run dev
```

Visit `http://localhost:3000` to see it running locally.

### Production Build

```bash
npm run build
```

The production output is placed in `website/dist/` — fully optimized, static, and ready for deployment to Vercel, Netlify, Cloudflare Pages, or GitHub Pages.

---

## ⚙️ Updating the Download Link

The Google Drive download URL is configured in a single place:

Open `src/App.jsx` and edit `GOOGLE_DRIVE_DOWNLOAD_URL`:

```javascript
export const GOOGLE_DRIVE_DOWNLOAD_URL = "https://drive.google.com/drive/folders/YOUR_ACTUAL_FOLDER_ID?usp=sharing";
```

All CTA buttons, cards, and modal links across the site will automatically update.

---

## 📄 License

Created with care for quiet minds. Released under the MIT License.

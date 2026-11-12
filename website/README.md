# 🍃 Yutori Kaizen — Official Web Experience & Portal

> *"Your brain wasn't lazy. It was out-engineered."*

The official web experience and download portal for **Yutori Kaizen** (ゆとり改善) — an intentional, open-source Android countermeasure against compulsive screen time, phantom phone checks, and impulsive spending.

Deployed directly via Git, this site serves as the interactive companion to the native Android app, featuring live simulators, real-time Godot companion previews, and direct APK distribution.

---

## 🍵 Philosophy

Instead of lecturing users with guilt meters, hostile screen locks, or sterile blocklists, Yutori Kaizen takes inspiration from two Japanese principles:
- **Yutori (ゆとり)**: Leaving spacious room to breathe rather than packing every waking second.
- **Kaizen (改善)**: Gentle, compounding 1% shifts that stick without burnout.

The web experience mirrors the app's aesthetic: dark paper textures, warm sage green accents, responsive typography, and tactile canvas animations.

---

## ✨ Web Features & Live Demonstrations

- **Interactive Wordmark (`<TechText />`)**: Vector dashed outlines, live letter dragging, click kicks, and assemble intro physics.
- **Section Particle Headings (`<ParticleText />`)**: High-density interactive particle headers with touch & cursor repulsion, click bursts, and responsive multi-line word wrapping on mobile.
- **Live Urge Surfing Simulator**: A functional in-browser recreation of the app's standalone 90-second Surge Bridge. Lets visitors experience how a craving crests and subsides before reaching for a feed.
- **Ribi Companion Showcase**: Real-time canvas sprite player running authentic companion animations (`idle`, `hop`, `breath`, `scared`) with an 11-outfit wardrobe switcher.
- **7-Part Bento Feature Grid**: Transparent breakdowns of the App Intercept, Floating Quick Dial, Mindful Expense Diary, Attention Clarity Index (NCI), Deep Work Blocks, Screen-Free Breaks, and Daily Habits.
- **Google Drive Direct Download Portal**: Clean, friction-free APK download card with a 3-step sideloading guide and privacy assurances (100% on-device, zero telemetry).

---

## 🛠️ Built With

- **Framework**: React 18 + Vite
- **Styling**: Tailwind CSS + Custom cozy palette (`cozy-950`, `moss`, `parchment`, `surge-orange`)
- **Typography**: *Fraunces* (Editorial Serif), *DM Sans* (Clean Body), *Caveat* (Warm Handwritten Notes)
- **Animation & Physics**: GSAP, Custom HTML5 Canvas Particle Engine, React Bits
- **Icons**: Lucide React

---

## 🌐 Git Hosting & Deployment

This project is configured for continuous static deployment via Git (GitHub Pages, Vercel, Netlify, or Cloudflare Pages):
- Production-ready bundling via Vite with relative asset paths (`base: './'`).
- Fully self-contained static build output in `dist/`.

---

## ⚙️ Google Drive Download Link

The APK download link is centrally managed in `src/App.jsx` under `GOOGLE_DRIVE_DOWNLOAD_URL`:

```javascript
export const GOOGLE_DRIVE_DOWNLOAD_URL = "https://drive.google.com/drive/folders/YOUR_FOLDER_ID?usp=sharing";
```

Updating this single constant propagates instantly across all download buttons, cards, and copy links across the entire portal.

---

## 📄 License

Created with care for quiet minds. Released under the MIT License.

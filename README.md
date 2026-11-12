# 🍃 Yutori Kaizen (ゆとり改善)

> **Mindful Digital Spaciousness for Android**  
> *Engineered friction, urge surfing, and a quiet frog companion to reclaim your attention.*

---

## 🍵 The Philosophy

Modern social feeds and compulsive apps are engineered by behavioral psychologists to extract your attention through variable dopamine rewards. When you try to fight back with standard screen time limits, they usually treat you like an inmate—locking you out, counting your shame hours, and triggering rebellious friction.

**Yutori Kaizen** is built on a different human foundation:
- **Yutori (ゆとり)**: A Japanese concept of deliberate spaciousness—giving your mind room to breathe, pause, and settle before reacting.
- **Kaizen (改善)**: Continuous, unhurried micro-improvements. 1% daily progress instead of punitive cold turkey.

Alongside you is **Ribi**, an unobtrusive companion frog that sits calmly beside your focus sessions, reminds you to do real-world physical resets, and celebrates when you ride out an urge.

---

## 📦 What's Inside

### 1. 🌊 The 90-Second Surge Bridge
A standalone urge-surfing tool based on Dr. Alan Marlatt’s addiction neuroscience. When a sudden craving to doom-scroll strikes, the Surge Bridge guides you through the 90 seconds it takes for a dopamine wave to crest and subside naturally.

### 2. 🛑 Mindful App Intercept
Catches impulsive opens on specified addictive apps before the algorithmic feed can load. Shows your personal wake-up quote with a calm 5–15 second breathing countdown and a one-tap exit to go straight home.

### 3. 🎯 Deep Work Blocks
Peaceful 25-minute focus intervals without stressful alarm sounds or gamified noise. Ribi keeps you company until the block completes.

### 4. 🧭 Neural Clarity Index (NCI)
A thoughtful 0–100 daily score that rewards avoided impulsive opens, completed focus intervals, and urge surfing victories, rather than just shaming you with raw minutes.

### 5. 🪙 Conscious Spending Diary
Because mindless scrolling and impulsive late-night purchasing stem from the exact same restless urge, Yutori Kaizen includes a zero-stress expense tracker with clean monthly clarity.

### 6. 🌐 Official Companion Website (`/website`)
A dedicated, responsive web showcase built with React 18, Vite, Tailwind CSS, and interactive canvas physics components from React Bits (`<TechText />`, `<ParticleText />`, `<BubbleMenu />`).

---

## 🛠️ Project Structure

```text
YutoriKaizen/
├── app/                  # Android Native App (Jetpack Compose, Kotlin, Room, Hilt)
│   ├── src/main/java/    # Clean Architecture (Data, Domain, Presentation)
│   └── src/main/assets/  # Godot companion spritesheets & audio assets
├── website/              # Official Web Landing Page (React + Vite + Tailwind)
│   ├── src/components/   # React Bits animations, bento grid, Ribi showcase
│   └── public/           # Lossless web assets and sprite animations
└── README.md
```

---

## 🚀 Running the Website

```bash
cd website
npm install
npm run dev
```

Server starts at `http://localhost:3000`.

---

## 📱 Sideloading the Android APK

1. Download the latest `.apk` release.
2. Allow installation from your browser or file manager when prompted by Android.
3. Grant **Accessibility Service** permission (required locally on-device to intercept configured apps before they open).
4. **100% Private**: Zero analytics, zero accounts, zero cloud servers. Everything stays on your device.

---

## 📄 License

Open-source under the MIT License.

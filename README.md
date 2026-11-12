# 🍃 Yutori Kaizen (ゆとり改善)

> **Mindful Digital Spaciousness for Android**  
> *Engineered friction, urge surfing, and a quiet frog companion to reclaim your attention.*

---

## 🍵 The Philosophy

Modern social feeds and compulsive apps are engineered by behavioral psychologists to extract your attention through variable dopamine rewards. When you try to fight back with standard screen time limits, they usually treat you like an inmate—locking you out, counting your shame hours, and triggering rebellious friction.

**Yutori Kaizen** is built on a different human foundation:
- **Yutori (ゆとり)**: A Japanese concept of deliberate spaciousness—giving your mind room to breathe, pause, and settle before reacting.
- **Kaizen (改善)**: Continuous, unhurried micro-improvements. 1% daily progress instead of punitive cold turkey.

Alongside you is **Ribi**, an unobtrusive companion frog that sits calmly beside your focus sessions, reminds you to take real-world physical resets, and celebrates when you ride out an urge.

---

## 📱 App Features

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

### 6. 🌿 Screen-Free Frog Breaks
Gentle somatic resets prompting you to rest your eyes, roll your shoulders, or hydrate.

### 7. 📌 Floating Quick Dial
An unobtrusive edge-of-screen shortcut bubble providing immediate access to focus blocks, expense logging, and the Surge Bridge from anywhere.

---

## 🌐 Official Web Portal (`/website`)

The repository includes the official website and download portal located in [`website/`](./website), hosted live via Git.

- **Live Demos**: Functional 90-second Surge Bridge urge surfing simulator and interactive Ribi mascot companion preview with 11 unlockable outfits.
- **Canvas Animations**: Built with React Bits (`<TechText />`, `<ParticleText />`, `<BubbleMenu />`) with mobile-optimized physics and responsive typography.
- **Direct Sideload Distribution**: Direct Google Drive APK download card with clean sideloading guides.

---

## 📦 Architecture & Repository Structure

```text
YutoriKaizen/
├── app/                  # Android Native Application
│   ├── src/main/java/    # Kotlin + Jetpack Compose + Room + Hilt Architecture
│   │   ├── data/         # Repositories, DAOs, Database entities, Preferences
│   │   ├── domain/       # UseCases, Domain Models, NCI Calculator
│   │   ├── presentation/ # Screens (Home, Surge, Intercept, Habits, Expenses)
│   │   └── service/      # App Intercept & Floating Quick Dial Services
│   └── src/main/assets/  # Godot companion sprite frames & sound effects
├── website/              # Official Web Portal (Hosted via Git)
│   ├── src/components/   # React Bits canvas engines, bento grid, Ribi showcase
│   └── public/           # Spritesheets, vector assets, and drawables
└── README.md
```

---

## 📲 Sideloading the Android APK

1. Download the latest `.apk` from the official web portal (Google Drive release).
2. Allow installation from your browser or file manager when prompted by Android.
3. Grant **Accessibility Service** permission (required locally on-device to intercept configured apps before they open).
4. **100% Private**: Zero analytics, zero accounts, zero cloud servers. Everything stays on your device.

---

## 📄 License

Open-source under the MIT License.

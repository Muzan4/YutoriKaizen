import React from 'react';
import ParticleText from './reactbits/ParticleText';
import icBrainNciImg from '../assets/ic_brain_nci.jpg';
import icStreakFireImg from '../assets/ic_streak_fire.jpg';
import ribiJournalImg from '../assets/ribi_journal.jpg';
import { 
  Brain, 
  Clock, 
  Coffee, 
  ShieldBan, 
  Wallet, 
  Layers, 
  CheckSquare, 
  Sparkles,
  Smartphone,
  PieChart,
  ArrowRight
} from 'lucide-react';

export default function FeaturesGrid() {
  return (
    <section id="features" className="py-16 sm:py-24 px-4 sm:px-6 lg:px-8 max-w-7xl mx-auto border-t border-white/5 relative">
      <div className="text-center max-w-3xl mx-auto mb-10 sm:mb-16">
        <span className="font-handwriting text-xl sm:text-2xl text-moss-light mb-1 block">
          Designed for Everyday Peace
        </span>
        
        {/* Header with ParticleText (Active on mobile & desktop with responsive wrapping) */}
        <div className="w-full h-[110px] xs:h-[130px] sm:h-[140px] md:h-[170px] relative flex items-center justify-center">
          <ParticleText
            text="Everything You Need"
            fontSize="clamp(2.2rem, 5.5vw, 4.2rem)"
            fontWeight={700}
            fontFamily='"Fraunces", serif'
            color="#F5F2EB"
            highlightColor="#84A98C"
            highlightMode="motion"
            shape="circle"
            particleSize={2.6}
            density={2}
            idleDrift={0}
            intro="gather"
            clickBurst={true}
            pointerRepel={35}
            repelRadius={90}
          />
        </div>

        <p className="text-parchment-dim text-sm sm:text-base md:text-lg leading-relaxed mt-2">
          Practical tools crafted to gently steer your attention away from compulsive apps, impulsive purchases, and daily rush.
        </p>
      </div>

      {/* Bento Grid */}
      <div className="grid grid-cols-1 md:grid-cols-12 gap-4 sm:gap-6">

        {/* 1. App Intercept Screen (The App Block) - 7 cols */}
        <div className="md:col-span-7 rounded-2xl sm:rounded-3xl bg-cozy-card border border-white/5 p-5 sm:p-8 flex flex-col justify-between relative overflow-hidden group hover:border-moss/20 transition-all duration-300">
          <div className="space-y-3 sm:space-y-4 max-w-lg relative z-10">
            <div className="inline-flex items-center gap-2 px-3 py-1 rounded-full bg-moss/10 text-moss text-xs font-mono uppercase tracking-wider">
              <ShieldBan className="w-3.5 h-3.5" />
              <span>Smart App Intercept</span>
            </div>
            <h3 className="text-xl sm:text-3xl font-serif text-parchment-100 font-semibold">
              The Mindful App Pause
            </h3>
            <p className="text-xs sm:text-sm text-parchment-dim leading-relaxed">
              Whenever you tap on your chosen time-sink apps (like Instagram, TikTok, or Reddit), Yutori Kaizen catches the impulse before the feed loads. 
              It brings up your own personal wake-up message, runs a gentle 5-to-15 second countdown to let your pulse settle, and gives you a one-tap <em>"I don't need this"</em> exit to return straight home.
            </p>
          </div>

          <div className="mt-6 sm:mt-8 pt-4 sm:pt-6 border-t border-white/5 flex flex-wrap items-center justify-between gap-3 relative z-10">
            <div className="flex flex-wrap items-center gap-2 text-xs text-parchment-200">
              <span className="w-2 h-2 rounded-full bg-moss" />
              <span>Your personal wake-up quote</span>
              <span className="text-white/20 hidden xs:inline">•</span>
              <span className="w-2 h-2 rounded-full bg-moss" />
              <span>Custom countdown pause</span>
              <span className="text-white/20 hidden xs:inline">•</span>
              <span className="w-2 h-2 rounded-full bg-moss" />
              <span>One-tap "Go Home" exit</span>
            </div>
          </div>
        </div>

        {/* 2. Floating Quick Dial / Shortcut - 5 cols */}
        <div className="md:col-span-5 rounded-2xl sm:rounded-3xl bg-cozy-card border border-white/5 p-5 sm:p-8 flex flex-col justify-between relative overflow-hidden group hover:border-moss/20 transition-all duration-300">
          <div className="space-y-3 sm:space-y-4 relative z-10">
            <div className="inline-flex items-center gap-2 px-3 py-1 rounded-full bg-moss-light/10 text-moss-light text-xs font-mono uppercase tracking-wider">
              <Layers className="w-3.5 h-3.5" />
              <span>Floating Quick Dial</span>
            </div>
            <h3 className="text-xl sm:text-2xl font-serif text-parchment-100 font-semibold">
              Instant Shortcut From Anywhere
            </h3>
            <p className="text-xs sm:text-sm text-parchment-dim leading-relaxed">
              A quiet, discreet cat rests gently at the edge of your phone screen looking at you all the time. 
              One tap blossoms into a circular radial menu so you can start a focus block, log an expense, or launch the Surge Bridge without digging through app drawers.
            </p>
          </div>

          <div className="mt-6 pt-4 sm:pt-5 border-t border-white/5 flex items-center justify-between">
            <div className="flex items-center gap-2 text-xs font-mono text-parchment-dim">
              <span className="w-2 h-2 rounded-full bg-moss" />
              <span>Works over any app</span>
            </div>
            <span className="text-xs font-mono text-parchment-100">Toggleable anytime</span>
          </div>
        </div>

        {/* 3. Mindful Expense Tracker - 6 cols */}
        <div className="md:col-span-6 rounded-2xl sm:rounded-3xl bg-cozy-card border border-white/5 p-5 sm:p-8 flex flex-col justify-between relative overflow-hidden group hover:border-moss/20 transition-all duration-300">
          <div className="space-y-3 sm:space-y-4 relative z-10">
            <div className="inline-flex items-center gap-2 px-3 py-1 rounded-full bg-moss/10 text-moss text-xs font-mono uppercase tracking-wider">
              <Wallet className="w-3.5 h-3.5" />
              <span>Mindful Money Diary</span>
            </div>
            <h3 className="text-xl sm:text-2xl font-serif text-parchment-100 font-semibold">
              Conscious Spending Tracker
            </h3>
            <p className="text-xs sm:text-sm text-parchment-dim leading-relaxed">
              Impulsive late-night shopping and mindless scrolling stem from the very same restless craving. 
              Keep tabs on daily purchases with simple, zero-stress categories (Coffee, Food, Gear, Bills) and clean monthly totals—helping you build peace in your wallet, too.
            </p>
          </div>

          <div className="mt-6 sm:mt-8 pt-4 sm:pt-6 border-t border-white/5 flex items-center justify-between">
            <div className="flex flex-wrap items-center gap-2 text-xs text-parchment-200">
              <span className="w-2 h-2 rounded-full bg-moss" />
              <span>One-tap quick entry</span>
              <span className="text-white/20 hidden xs:inline">•</span>
              <span className="w-2 h-2 rounded-full bg-moss" />
              <span>Simple category filters</span>
            </div>
            <span className="text-xs font-mono text-moss-light">100% Private</span>
          </div>
        </div>

        {/* 4. Neural Clarity Index (NCI) - 6 cols */}
        <div className="md:col-span-6 rounded-2xl sm:rounded-3xl bg-cozy-card border border-white/5 p-5 sm:p-8 flex flex-col justify-between relative overflow-hidden group hover:border-moss/20 transition-all duration-300">
          <div className="space-y-3 sm:space-y-4 relative z-10">
            <div className="inline-flex items-center gap-2 px-3 py-1 rounded-full bg-moss-vibrant/10 text-moss-vibrant text-xs font-mono uppercase tracking-wider">
              <Brain className="w-3.5 h-3.5" />
              <span>Clarity Score</span>
            </div>
            <h3 className="text-xl sm:text-2xl font-serif text-parchment-100 font-semibold">
              Your Daily Attention Score
            </h3>
            <p className="text-xs sm:text-sm text-parchment-dim leading-relaxed">
              Standard screen time meters only count raw hours and make you feel guilty. 
              Our daily clarity index scores how well you handled cravings, how many impulsive opens you avoided, and how much undistracted deep work you enjoyed.
            </p>
          </div>

          <div className="mt-6 sm:mt-8 pt-4 sm:pt-6 border-t border-white/5 flex items-center justify-between">
            <div className="flex items-center gap-3">
              <div className="w-10 h-10 rounded-xl bg-cozy-850 p-1 border border-white/10 overflow-hidden shadow shrink-0">
                <img src={icBrainNciImg} alt="Clarity Index" className="w-full h-full object-cover rounded-lg" />
              </div>
              <div>
                <div className="text-xs font-semibold text-parchment-100">0 to 100 Index</div>
                <div className="text-[11px] text-parchment-dim">Reset • Rewire • Reclaim</div>
              </div>
            </div>
            <span className="text-xs font-mono text-moss">Meaningful Progress</span>
          </div>
        </div>

        {/* 5. Deep Focus Blocks - 4 cols */}
        <div className="md:col-span-4 rounded-2xl sm:rounded-3xl bg-cozy-card border border-white/5 p-5 sm:p-8 flex flex-col justify-between relative overflow-hidden group hover:border-moss/20 transition-all duration-300">
          <div className="space-y-2 sm:space-y-3 relative z-10">
            <div className="inline-flex items-center gap-2 px-3 py-1 rounded-full bg-moss/10 text-moss text-xs font-mono uppercase tracking-wider">
              <Clock className="w-3.5 h-3.5" />
              <span>Focus Timer</span>
            </div>
            <h3 className="text-lg sm:text-xl font-serif text-parchment-100 font-semibold">
              Deep Work Blocks
            </h3>
            <p className="text-xs text-parchment-dim leading-relaxed">
              Calm attention intervals without noisy notifications or alarms. Ribi sits quietly beside your task until the block is done.
            </p>
          </div>

          <div className="mt-6 pt-4 border-t border-white/5 flex items-center justify-between text-xs text-parchment-dim">
            <span>Peaceful pacing</span>
            <span className="text-parchment-100 font-mono">25 Mins</span>
          </div>
        </div>

        {/* 6. Screen-Free Frog Breaks - 4 cols */}
        <div className="md:col-span-4 rounded-2xl sm:rounded-3xl bg-cozy-card border border-white/5 p-5 sm:p-8 flex flex-col justify-between relative overflow-hidden group hover:border-moss/20 transition-all duration-300">
          <div className="space-y-2 sm:space-y-3 relative z-10">
            <div className="inline-flex items-center gap-2 px-3 py-1 rounded-full bg-surge-orange/10 text-surge-orange text-xs font-mono uppercase tracking-wider">
              <Coffee className="w-3.5 h-3.5" />
              <span>Offline Rest</span>
            </div>
            <h3 className="text-lg sm:text-xl font-serif text-parchment-100 font-semibold">
              Screen-Free Breaks
            </h3>
            <p className="text-xs text-parchment-dim leading-relaxed">
              Rest your eyes and ease neck tension. Prompts simple real-world rituals: sip cool water, stretch your shoulders, or look 20 feet away out a window.
            </p>
          </div>

          <div className="mt-6 pt-4 border-t border-white/5 flex items-center justify-between text-xs text-parchment-dim">
            <span>Somatic reset</span>
            <span className="text-surge-orange font-mono">Real-World Rituals</span>
          </div>
        </div>

        {/* 7. Kaizen Daily Habits - 4 cols */}
        <div className="md:col-span-4 rounded-2xl sm:rounded-3xl bg-cozy-card border border-white/5 p-5 sm:p-8 flex flex-col justify-between relative overflow-hidden group hover:border-moss/20 transition-all duration-300">
          <div className="space-y-2 sm:space-y-3 relative z-10">
            <div className="inline-flex items-center gap-2 px-3 py-1 rounded-full bg-moss-vibrant/10 text-moss-vibrant text-xs font-mono uppercase tracking-wider">
              <CheckSquare className="w-3.5 h-3.5" />
              <span>Daily Habits</span>
            </div>
            <h3 className="text-lg sm:text-xl font-serif text-parchment-100 font-semibold">
              Gentle Daily Check-Ins
            </h3>
            <p className="text-xs text-parchment-dim leading-relaxed">
              Check off simple positive actions that replace compulsive screen time: reading five pages, getting fresh air, or an unhurried walk.
            </p>
          </div>

          <div className="mt-6 pt-4 border-t border-white/5 flex items-center justify-between">
            <div className="flex items-center gap-2">
              <img src={icStreakFireImg} alt="Streak" className="w-8 h-8 rounded-lg object-cover" />
              <div className="text-xs font-semibold text-parchment-100">Resilience Streak</div>
            </div>
            <span className="text-xs font-mono text-moss">1% Every Day</span>
          </div>
        </div>

      </div>
    </section>
  );
}

import React from 'react';
import TechText from './reactbits/TechText';
import icRibiHeadImg from '../assets/ic_ribi_head.png';
import { Download, Sparkles, ShieldCheck, Heart, ArrowDown } from 'lucide-react';

export default function Hero({ driveUrl }) {
  return (
    <section id="hero" className="relative pt-4 sm:pt-6 md:pt-8 pb-10 sm:pb-14 px-4 sm:px-6 lg:px-8 max-w-6xl mx-auto flex flex-col items-center text-center overflow-hidden">
      {/* Ambient background glows */}
      <div className="absolute top-1/4 left-1/2 -translate-x-1/2 -translate-y-1/2 w-[280px] sm:w-[480px] h-[220px] sm:h-[300px] bg-moss/10 rounded-full blur-[90px] sm:blur-[130px] pointer-events-none -z-10" />
      <div className="absolute top-1/3 left-1/3 -translate-x-1/2 w-[200px] sm:w-[320px] h-[160px] sm:h-[220px] bg-surge-orange/5 rounded-full blur-[80px] sm:blur-[110px] pointer-events-none -z-10" />

      {/* Pill Badge */}
      <div className="inline-flex flex-wrap justify-center items-center gap-2 px-3.5 py-1.5 rounded-full bg-cozy-850/90 border border-white/10 text-xs text-parchment-200 mb-2 sm:mb-3 backdrop-blur-md shadow-inner max-w-full">
        <span className="w-2 h-2 rounded-full bg-moss-vibrant animate-pulse shrink-0" />
        <span className="font-mono text-moss-light">v1.0 Ready</span>
        <span className="text-white/20 hidden xs:inline">•</span>
        <span className="font-serif italic text-parchment-100 truncate">ゆとり改善 • Room to Breathe in a Busy World</span>
      </div>

      {/* Main Title powered by React Bits TechText (Unclipped overflow bleed) */}
      <div className="w-full max-w-4xl h-[85px] xs:h-[105px] sm:h-[135px] md:h-[160px] lg:h-[185px] relative my-0.5 sm:my-1 flex items-center justify-center overflow-visible">
        <TechText
          text="Yutori Kaizen"
          fontFamily='"Fraunces", serif'
          fontWeight={600}
          fontSize={100}
          letterSpacing={-0.03}
          color="#F5F2EB"
          accentColor="#84A98C"
          reach={110}
          specks={12}
          dashLength={4}
          dashGap={2}
          clickKick={true}
          draggable={true}
          bleedX={100}
          bleedY={150}
          intro="assemble"
          introDuration={1.8}
        />
      </div>

      {/* Subheading & Editorial Copy */}
      <div className="max-w-2xl mx-auto mt-2 sm:mt-3 space-y-2">
        <h2 className="text-xl sm:text-2xl md:text-3xl font-serif text-parchment-100 font-light leading-snug">
          Your brain wasn't lazy. <br className="hidden sm:inline" />
          <span className="italic text-moss-light font-normal">It was out-engineered.</span>
        </h2>
        <p className="text-xs sm:text-sm md:text-base text-parchment-dim font-sans leading-relaxed max-w-xl mx-auto">
          The apps stealing your hours have thousands of behavioral psychologists on their payroll. 
          Yutori Kaizen gives you the scientific countermeasures: intentional friction, 90-second urge resets, 
          and Ribi — a companion frog keeping you anchored in real life.
        </p>
      </div>

      {/* Action Buttons */}
      <div className="mt-5 sm:mt-6 flex flex-col sm:flex-row items-center justify-center gap-3 sm:gap-4 w-full sm:w-auto relative z-20">
        <a
          href={driveUrl}
          target="_blank"
          rel="noopener noreferrer"
          className="w-full sm:w-auto inline-flex items-center justify-center gap-3 px-7 py-3 rounded-2xl bg-moss hover:bg-moss-light text-cozy-950 font-medium text-base transition-all duration-200 transform hover:-translate-y-0.5 shadow-lg shadow-moss/20 group"
        >
          <Download className="w-5 h-5 transition-transform group-hover:scale-110" />
          <span>Download Android APK</span>
          <span className="text-xs px-2 py-0.5 rounded-full bg-cozy-950/20 font-mono">Google Drive</span>
        </a>

        <a
          href="#surge-bridge"
          className="w-full sm:w-auto inline-flex items-center justify-center gap-2.5 px-6 py-3 rounded-2xl bg-cozy-850 hover:bg-cozy-800 text-parchment-200 border border-white/10 font-medium text-base transition-all duration-200 hover:border-moss/30"
        >
          <span>See How It Works</span>
          <ArrowDown className="w-4 h-4 text-moss" />
        </a>
      </div>

      {/* Trust & Spec Highlights (Compact Card Grid) */}
      <div className="mt-7 sm:mt-8 pt-5 sm:pt-6 border-t border-white/10 grid grid-cols-2 lg:grid-cols-4 gap-3 sm:gap-3.5 text-left max-w-4xl w-full">
        <div className="flex items-center gap-3 p-3 sm:p-3.5 rounded-2xl bg-cozy-850/70 border border-white/5 hover:border-moss/20 transition-colors">
          <div className="p-2 rounded-xl bg-cozy-800 border border-white/5 text-moss shrink-0">
            <ShieldCheck className="w-4 h-4" />
          </div>
          <div className="min-w-0">
            <div className="text-[10px] sm:text-xs font-mono text-parchment-dim uppercase tracking-wider truncate">Privacy First</div>
            <div className="text-xs sm:text-sm font-medium text-parchment-100 truncate">100% On-Device</div>
          </div>
        </div>

        <div className="flex items-center gap-3 p-3 sm:p-3.5 rounded-2xl bg-cozy-850/70 border border-white/5 hover:border-surge-orange/20 transition-colors">
          <div className="p-2 rounded-xl bg-cozy-800 border border-white/5 text-surge-orange shrink-0">
            <Sparkles className="w-4 h-4" />
          </div>
          <div className="min-w-0">
            <div className="text-[10px] sm:text-xs font-mono text-parchment-dim uppercase tracking-wider truncate">Urge Surfing</div>
            <div className="text-xs sm:text-sm font-medium text-parchment-100 truncate">90s Surge Bridge</div>
          </div>
        </div>

        <div className="flex items-center gap-3 p-3 sm:p-3.5 rounded-2xl bg-cozy-850/70 border border-white/5 hover:border-moss-light/20 transition-colors">
          <div className="p-2 rounded-xl bg-cozy-800 border border-white/5 text-moss-light shrink-0">
            <Heart className="w-4 h-4" />
          </div>
          <div className="min-w-0">
            <div className="text-[10px] sm:text-xs font-mono text-parchment-dim uppercase tracking-wider truncate">Mindful Mascot</div>
            <div className="text-xs sm:text-sm font-medium text-parchment-100 truncate">Ribi Companion</div>
          </div>
        </div>

        <div className="flex items-center gap-3 p-3 sm:p-3.5 rounded-2xl bg-cozy-850/70 border border-white/5 hover:border-parchment-100/20 transition-colors">
          <div className="p-2 rounded-xl bg-cozy-800 border border-white/5 text-parchment-200 shrink-0">
            <div className="w-4 h-4 flex items-center justify-center font-handwriting text-base font-bold leading-none">ゆ</div>
          </div>
          <div className="min-w-0">
            <div className="text-[10px] sm:text-xs font-mono text-parchment-dim uppercase tracking-wider truncate">Philosophy</div>
            <div className="text-xs sm:text-sm font-medium text-parchment-100 truncate">Calm Yutori Space</div>
          </div>
        </div>
      </div>
    </section>
  );
}

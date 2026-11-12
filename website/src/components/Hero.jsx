import React from 'react';
import TechText from './reactbits/TechText';
import icRibiHeadImg from '../assets/ic_ribi_head.png';
import { Download, Sparkles, ShieldCheck, Heart, ArrowDown } from 'lucide-react';

export default function Hero({ driveUrl }) {
  return (
    <section id="hero" className="relative pt-24 pb-16 md:pt-40 md:pb-32 px-4 sm:px-6 lg:px-8 max-w-7xl mx-auto flex flex-col items-center text-center overflow-hidden">
      {/* Ambient background glows */}
      <div className="absolute top-1/4 left-1/2 -translate-x-1/2 -translate-y-1/2 w-[300px] sm:w-[550px] h-[250px] sm:h-[350px] bg-moss/10 rounded-full blur-[100px] sm:blur-[140px] pointer-events-none -z-10" />
      <div className="absolute top-1/3 left-1/3 -translate-x-1/2 w-[220px] sm:w-[350px] h-[180px] sm:h-[250px] bg-surge-orange/5 rounded-full blur-[90px] sm:blur-[120px] pointer-events-none -z-10" />

      {/* Pill Badge */}
      <div className="inline-flex flex-wrap justify-center items-center gap-2 px-3.5 py-1.5 rounded-full bg-cozy-850/90 border border-white/10 text-xs text-parchment-200 mb-6 sm:mb-8 backdrop-blur-md shadow-inner max-w-full">
        <span className="w-2 h-2 rounded-full bg-moss-vibrant animate-pulse shrink-0" />
        <span className="font-mono text-moss-light">v1.0 Ready</span>
        <span className="text-white/20 hidden xs:inline">•</span>
        <span className="font-serif italic text-parchment-100 truncate">ゆとり改善 • Engineered Spaciousness</span>
      </div>

      {/* Main Title powered by React Bits TechText (Active on mobile & desktop) */}
      <div className="w-full max-w-5xl h-[120px] xs:h-[150px] sm:h-[190px] md:h-[240px] lg:h-[300px] relative my-1 sm:my-2 flex items-center justify-center">
        <TechText
          text="Yutori Kaizen"
          fontFamily='"Fraunces", serif'
          fontWeight={600}
          fontSize={110}
          letterSpacing={-0.03}
          color="#F5F2EB"
          accentColor="#84A98C"
          reach={120}
          specks={12}
          dashLength={4}
          dashGap={2}
          clickKick={true}
          draggable={true}
          intro="assemble"
          introDuration={1.8}
        />
      </div>

      {/* Subheading & Editorial Copy */}
      <div className="max-w-2xl mx-auto mt-4 sm:mt-6 space-y-4">
        <h2 className="text-xl sm:text-2xl md:text-3xl font-serif text-parchment-100 font-light leading-snug">
          Your brain wasn't lazy. <br className="hidden sm:inline" />
          <span className="italic text-moss-light font-normal">It was out-engineered.</span>
        </h2>
        <p className="text-sm sm:text-base md:text-lg text-parchment-dim font-sans leading-relaxed">
          The apps stealing your hours have thousands of behavioral psychologists on their payroll. 
          Yutori Kaizen gives you the scientific countermeasures: intentional friction, 90-second urge resets, 
          and Ribi — a companion frog keeping you anchored in real life.
        </p>
      </div>

      {/* Action Buttons */}
      <div className="mt-9 flex flex-col sm:flex-row items-center gap-4 w-full sm:w-auto">
        <a
          href={driveUrl}
          target="_blank"
          rel="noopener noreferrer"
          className="w-full sm:w-auto inline-flex items-center justify-center gap-3 px-8 py-4 rounded-2xl bg-moss hover:bg-moss-light text-cozy-950 font-medium text-base transition-all duration-200 transform hover:-translate-y-0.5 shadow-lg shadow-moss/20 group"
        >
          <Download className="w-5 h-5 transition-transform group-hover:scale-110" />
          <span>Download Android APK</span>
          <span className="text-xs px-2 py-0.5 rounded-full bg-cozy-950/20 font-mono">Google Drive</span>
        </a>

        <a
          href="#surge-bridge"
          className="w-full sm:w-auto inline-flex items-center justify-center gap-2.5 px-7 py-4 rounded-2xl bg-cozy-850 hover:bg-cozy-800 text-parchment-200 border border-white/10 font-medium text-base transition-all duration-200 hover:border-moss/30"
        >
          <span>See How It Works</span>
          <ArrowDown className="w-4 h-4 text-moss" />
        </a>
      </div>

      {/* Trust & Spec Highlights */}
      <div className="mt-14 pt-8 border-t border-white/5 grid grid-cols-2 md:grid-cols-4 gap-6 text-left max-w-4xl w-full">
        <div className="flex items-start gap-3">
          <div className="p-2 rounded-xl bg-cozy-800/60 border border-white/5 text-moss">
            <ShieldCheck className="w-4 h-4" />
          </div>
          <div>
            <div className="text-xs font-mono text-parchment-dim uppercase tracking-wider">Privacy First</div>
            <div className="text-sm font-medium text-parchment-100">100% On-Device</div>
          </div>
        </div>

        <div className="flex items-start gap-3">
          <div className="p-2 rounded-xl bg-cozy-800/60 border border-white/5 text-surge-orange">
            <Sparkles className="w-4 h-4" />
          </div>
          <div>
            <div className="text-xs font-mono text-parchment-dim uppercase tracking-wider">Urge Surfing</div>
            <div className="text-sm font-medium text-parchment-100">90s Surge Bridge</div>
          </div>
        </div>

        <div className="flex items-start gap-3">
          <div className="p-2 rounded-xl bg-cozy-800/60 border border-white/5 text-moss-light">
            <Heart className="w-4 h-4" />
          </div>
          <div>
            <div className="text-xs font-mono text-parchment-dim uppercase tracking-wider">Mindful Mascot</div>
            <div className="text-sm font-medium text-parchment-100">Ribi the Companion</div>
          </div>
        </div>

        <div className="flex items-start gap-3">
          <div className="p-2 rounded-xl bg-cozy-800/60 border border-white/5 text-parchment-200">
            <div className="w-4 h-4 flex items-center justify-center font-handwriting text-base font-bold">ゆ</div>
          </div>
          <div>
            <div className="text-xs font-mono text-parchment-dim uppercase tracking-wider">Philosophy</div>
            <div className="text-sm font-medium text-parchment-100">Calm Yutori Space</div>
          </div>
        </div>
      </div>
    </section>
  );
}

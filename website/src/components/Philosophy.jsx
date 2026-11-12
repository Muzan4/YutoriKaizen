import React from 'react';
import ParticleText from './reactbits/ParticleText';
import { Compass, Flame, ShieldAlert, Cpu } from 'lucide-react';

export default function Philosophy() {
  return (
    <section id="philosophy" className="py-16 sm:py-24 px-4 sm:px-6 lg:px-8 max-w-7xl mx-auto border-t border-white/5 relative">
      <div className="text-center max-w-3xl mx-auto mb-10 sm:mb-16">
        <span className="font-handwriting text-xl sm:text-2xl text-moss-light mb-1 block">
          Ancient Wisdom meets Modern Neuroscience
        </span>

        {/* React Bits ParticleText Header (Active on mobile & desktop with responsive wrapping) */}
        <div className="w-full h-[110px] xs:h-[130px] sm:h-[140px] md:h-[170px] relative flex items-center justify-center">
          <ParticleText
            text="The Yutori Mindset"
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

        <p className="text-parchment-dim text-sm sm:text-base md:text-lg leading-relaxed mt-2 sm:mt-4">
          Most blockers shame you with timers and locks. But shame reinforces dopamine hunger. 
          Yutori Kaizen replaces hostile locks with spacious awareness and continuous micro-shifts.
        </p>
      </div>

      {/* The Two Concepts */}
      <div className="grid md:grid-cols-2 gap-5 sm:gap-8 mb-10 sm:mb-16">
        <div className="p-6 sm:p-10 rounded-2xl sm:rounded-3xl bg-cozy-card border border-white/5 relative overflow-hidden group hover:border-moss/20 transition-all duration-300">
          <div className="absolute top-0 right-0 p-8 text-6xl font-serif text-white/[0.03] select-none pointer-events-none">
            ゆとり
          </div>
          <div className="inline-flex items-center gap-2 px-3 py-1 rounded-full bg-moss/10 text-moss text-xs font-mono uppercase tracking-widest mb-6">
            01 • The Japanese Way
          </div>
          <h3 className="text-2xl sm:text-3xl font-serif text-parchment-100 font-semibold mb-4">
            Yutori <span className="font-normal text-moss-light">(ゆとり)</span>
          </h3>
          <p className="text-parchment-dim leading-relaxed mb-6">
            Yutori translates to <em>"room to breathe"</em> or <em>"intentional spaciousness"</em>. It is the conscious refusal to let every second of your day be packed, optimized, or consumed by algorithms. When you give your mind room to settle, cravings lose their grip naturally.
          </p>
          <ul className="space-y-3 text-sm text-parchment-200">
            <li className="flex items-center gap-3">
              <span className="w-1.5 h-1.5 rounded-full bg-moss" />
              <span>Breaks the compulsive phantom-buzz reflex</span>
            </li>
            <li className="flex items-center gap-3">
              <span className="w-1.5 h-1.5 rounded-full bg-moss" />
              <span>Leaves empty space for deep thoughts to surface</span>
            </li>
          </ul>
        </div>

        <div className="p-6 sm:p-10 rounded-2xl sm:rounded-3xl bg-cozy-card border border-white/5 relative overflow-hidden group hover:border-moss/20 transition-all duration-300">
          <div className="absolute top-0 right-0 p-8 text-6xl font-serif text-white/[0.03] select-none pointer-events-none">
            改善
          </div>
          <div className="inline-flex items-center gap-2 px-3 py-1 rounded-full bg-moss/10 text-moss text-xs font-mono uppercase tracking-widest mb-6">
            02 • The Neuroplastic Method
          </div>
          <h3 className="text-2xl sm:text-3xl font-serif text-parchment-100 font-semibold mb-4">
            Kaizen <span className="font-normal text-moss-light">(改善)</span>
          </h3>
          <p className="text-parchment-dim leading-relaxed mb-6">
            Kaizen means <em>"continuous 1% gradual refinement"</em>. Quitting cold-turkey almost always leads to sudden relapse. By introducing tiny 90-second pauses and micro-habits, Kaizen rewires your neural pathways gently until clarity becomes your default state.
          </p>
          <ul className="space-y-3 text-sm text-parchment-200">
            <li className="flex items-center gap-3">
              <span className="w-1.5 h-1.5 rounded-full bg-moss" />
              <span>Micro-friction instead of painful cold-turkey restrictions</span>
            </li>
            <li className="flex items-center gap-3">
              <span className="w-1.5 h-1.5 rounded-full bg-moss" />
              <span>Dopamine Baseline Reset across structured 3-phase journey</span>
            </li>
          </ul>
        </div>
      </div>

      {/* Quote Banner */}
      <div className="rounded-2xl sm:rounded-3xl bg-gradient-to-r from-cozy-850 via-cozy-800 to-cozy-850 p-6 sm:p-12 border border-white/10 text-center relative overflow-hidden">
        <div className="max-w-2xl mx-auto space-y-4">
          <p className="font-serif italic text-lg sm:text-2xl text-parchment-100 leading-relaxed">
            "Willpower is an exhaustible battery. Architecture is permanent. Don't fight the algorithm with grit — defeat it with thoughtful friction."
          </p>
          <div className="font-mono text-xs uppercase tracking-widest text-moss">
            — The Yutori Kaizen Engineering Principle
          </div>
        </div>
      </div>
    </section>
  );
}

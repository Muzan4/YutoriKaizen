import React, { useState } from 'react';
import ParticleText from './reactbits/ParticleText';
import RibiFrogSprite, { SKIN_MAP } from './RibiFrogSprite';
import ribiCelebrationImg from '../assets/ribi_celebration.jpg';
import { Sparkles, Shirt, Award, Check } from 'lucide-react';

const SKINS_LIST = Object.values(SKIN_MAP);

export default function RibiShowcase() {
  const [selectedSkinId, setSelectedSkinId] = useState('cowboy');
  const [hopCount, setHopCount] = useState(0);

  const activeSkin = SKIN_MAP[selectedSkinId] || SKIN_MAP.green;

  return (
    <section id="ribi" className="py-16 sm:py-24 px-4 sm:px-6 lg:px-8 max-w-7xl mx-auto border-t border-white/5 relative">
      <div className="grid lg:grid-cols-12 gap-8 lg:gap-16 items-center">
        
        {/* Left: Companion Interactive Preview */}
        <div className="lg:col-span-5 flex flex-col items-center w-full">
          <div className="w-full max-w-md rounded-2xl sm:rounded-3xl bg-cozy-card border border-white/10 p-5 sm:p-8 text-center relative overflow-hidden shadow-2xl">
            <div className="flex items-center justify-between mb-2">
              <span className="px-3 py-1 rounded-full bg-moss/10 text-moss text-xs font-mono">
                Companion Preview
              </span>
              <span className="text-xs font-mono text-parchment-dim">
                Hops: <strong className="text-moss font-bold">{hopCount}</strong>
              </span>
            </div>

            {/* Live Interactive Sprite */}
            <div className="my-2 sm:my-4">
              <RibiFrogSprite 
                skinId={selectedSkinId} 
                onHop={() => setHopCount(h => h + 1)}
              />
            </div>

            <div className="space-y-1 mt-2">
              <h3 className="font-serif text-xl sm:text-2xl text-parchment-100 font-semibold">
                {activeSkin.name}
              </h3>
              <p className="text-xs sm:text-sm text-parchment-dim max-w-xs mx-auto">
                {activeSkin.desc}
              </p>
            </div>

            {/* Streak unlocks */}
            <div className="mt-5 sm:mt-6 pt-4 sm:pt-5 border-t border-white/5 flex items-center justify-between text-left">
              <div className="flex items-center gap-3">
                <img 
                  src={ribiCelebrationImg} 
                  alt="Ribi Celebration" 
                  className="w-10 h-10 sm:w-11 sm:h-11 rounded-xl object-cover border border-white/10 shrink-0" 
                />
                <div>
                  <div className="text-xs font-semibold text-parchment-100">Unlockable Wardrobe</div>
                  <div className="text-[10px] sm:text-[11px] text-parchment-dim">Earn new looks as your streaks grow</div>
                </div>
              </div>
              <Award className="w-5 h-5 text-moss-light shrink-0" />
            </div>
          </div>
        </div>

        {/* Right: Narrative & Wardrobe Picker */}
        <div className="lg:col-span-7 space-y-5 sm:space-y-6">
          <div className="inline-flex items-center gap-2 px-3.5 py-1 rounded-full bg-moss/10 border border-moss/20 text-moss-light text-xs font-mono uppercase tracking-wider">
            <Sparkles className="w-3.5 h-3.5" />
            <span>Interactive Companion</span>
          </div>

          {/* Header with ParticleText (Active on mobile & desktop) */}
          <div className="w-full h-[100px] xs:h-[120px] sm:h-[140px] md:h-[160px] relative flex items-center justify-center">
            <ParticleText
              text="Meet Ribi"
              fontSize="clamp(2.4rem, 5.5vw, 4.4rem)"
              fontWeight={700}
              fontFamily='"Fraunces", serif'
              color="#F5F2EB"
              highlightColor="#84A98C"
              highlightMode="motion"
              shape="circle"
              particleSize={2.2}
              density={3.5}
              intro="gather"
              clickBurst={true}
              pointerRepel={30}
              repelRadius={80}
            />
          </div>

          <p className="text-parchment-dim text-sm sm:text-base md:text-lg leading-relaxed">
            Most blockers treat you like a prisoner with harsh lock screens and shame counters. 
            <strong> Ribi</strong> turns habit recovery into a quiet, warm daily partnership. 
            Your companion sits calmly with you during focus blocks, reminds you to stretch your neck when tension builds, and celebrates quietly whenever you ride out an urge.
          </p>

          {/* Wardrobe Selector */}
          <div className="space-y-3 pt-2">
            <div className="text-xs font-mono text-parchment-dim uppercase tracking-wider flex items-center justify-between">
              <span className="flex items-center gap-2">
                <Shirt className="w-4 h-4 text-moss" />
                <span>Choose an Outfit ({SKINS_LIST.length} Styles)</span>
              </span>
              <span className="text-moss-light">Tap to switch look</span>
            </div>

            <div className="grid grid-cols-2 sm:grid-cols-3 gap-2 sm:gap-2.5 max-h-[280px] sm:max-h-[310px] overflow-y-auto pr-1">
              {SKINS_LIST.map(skin => (
                <button
                  key={skin.id}
                  onClick={() => setSelectedSkinId(skin.id)}
                  className={`p-2.5 sm:p-3 rounded-xl sm:rounded-2xl text-left border transition-all relative flex flex-col justify-between ${
                    selectedSkinId === skin.id
                      ? 'bg-cozy-800 border-moss shadow-md shadow-moss/10'
                      : 'bg-cozy-card border-white/5 hover:border-white/20'
                  }`}
                >
                  <div className="flex items-center justify-between mb-1.5 sm:mb-2">
                    <span className="w-2.5 h-2.5 rounded-full" style={{ backgroundColor: skin.color }} />
                    {selectedSkinId === skin.id && (
                      <span className="w-3.5 h-3.5 rounded-full bg-moss text-cozy-950 flex items-center justify-center">
                        <Check className="w-2.5 h-2.5 stroke-[3]" />
                      </span>
                    )}
                  </div>
                  <div>
                    <div className="text-xs font-semibold text-parchment-100 leading-tight">{skin.name}</div>
                    <div className="text-[10px] sm:text-[11px] text-parchment-dim leading-tight mt-0.5">{skin.tagline}</div>
                  </div>
                </button>
              ))}
            </div>
          </div>
        </div>

      </div>
    </section>
  );
}

import React, { useState, useEffect } from 'react';
import { Sparkles, RotateCcw, Award, CheckCircle2, Waves } from 'lucide-react';
import icRibiHeadImg from '../assets/ic_ribi_head.png';
import ribiCelebrationImg from '../assets/ribi_celebration.jpg';

export default function SurgeShowcase() {
  const [secondsLeft, setSecondsLeft] = useState(90);
  const [isActive, setIsActive] = useState(false);
  const [selectedTrigger, setSelectedTrigger] = useState('Boredom');
  const [survived, setSurvived] = useState(false);

  const triggers = [
    { label: 'Boredom', desc: 'Nothing to do' },
    { label: 'Anxiety', desc: 'Stress relief' },
    { label: 'Loneliness', desc: 'Seeking connection' },
    { label: 'Exhaustion', desc: 'Too tired to think' },
    { label: 'Avoidance', desc: 'Procrastinating a task' }
  ];

  useEffect(() => {
    let interval = null;
    if (isActive && secondsLeft > 0) {
      interval = setInterval(() => {
        setSecondsLeft(sec => sec - 1);
      }, 1000);
    } else if (secondsLeft === 0) {
      setIsActive(false);
      setSurvived(true);
    }
    return () => clearInterval(interval);
  }, [isActive, secondsLeft]);

  const handleStart = () => {
    setSecondsLeft(90);
    setSurvived(false);
    setIsActive(true);
  };

  const handleReset = () => {
    setIsActive(false);
    setSecondsLeft(90);
    setSurvived(false);
  };

  const progress = ((90 - secondsLeft) / 90) * 100;

  return (
    <section id="surge-bridge" className="py-16 sm:py-24 px-4 sm:px-6 lg:px-8 max-w-7xl mx-auto border-t border-white/5 relative">
      <div className="grid lg:grid-cols-12 gap-10 lg:gap-16 items-center">
        
        {/* Left Explanation: The Science of the Surge Bridge */}
        <div className="lg:col-span-7 space-y-6 sm:space-y-8">
          <div className="inline-flex items-center gap-2 px-3.5 py-1 rounded-full bg-surge-orange/15 border border-surge-orange/30 text-surge-orange text-xs font-mono uppercase tracking-wider">
            <Waves className="w-3.5 h-3.5" />
            <span>Dedicated Dopamine Reset Tool</span>
          </div>

          <h2 className="text-3xl sm:text-4xl md:text-5xl font-serif text-parchment-100 font-normal leading-tight">
            The 90-Second <br />
            <span className="italic text-surge-orange font-medium">Surge Bridge.</span>
          </h2>

          <p className="text-parchment-dim text-sm sm:text-base md:text-lg leading-relaxed">
            When a sudden urge to binge-scroll strikes, trying to forcefully crush it only creates inner tension. 
            The <strong>Surge Bridge</strong> is a deliberate, standalone reset you can open anytime from your home screen or quick dial. 
            It guides you through 90 seconds of <em>urge surfing</em>—the exact neurobiological window it takes for a dopamine wave to crest and naturally subside.
          </p>

          {/* 3 Step Breakdown */}
          <div className="space-y-4 pt-2">
            <div className="p-4 rounded-2xl bg-cozy-850/80 border border-white/5 flex items-start gap-4">
              <div className="w-8 h-8 rounded-xl bg-cozy-800 flex items-center justify-center font-mono text-xs font-bold text-surge-orange shrink-0">
                01
              </div>
              <div>
                <h4 className="text-sm font-semibold text-parchment-100">Name What You're Feeling</h4>
                <p className="text-xs text-parchment-dim mt-0.5">
                  Pick your underlying trigger: Boredom, Anxiety, Loneliness, Exhaustion, or Avoidance. Simply naming the emotion disarms the compulsive autopilot.
                </p>
              </div>
            </div>

            <div className="p-4 rounded-2xl bg-cozy-850/80 border border-white/5 flex items-start gap-4">
              <div className="w-8 h-8 rounded-xl bg-cozy-800 flex items-center justify-center font-mono text-xs font-bold text-moss-light shrink-0">
                02
              </div>
              <div>
                <h4 className="text-sm font-semibold text-parchment-100">Observe Without Reacting</h4>
                <p className="text-xs text-parchment-dim mt-0.5">
                  Watch the 90-second wave breathe and contract. You don't have to fight the craving—just stay still for a minute and a half.
                </p>
              </div>
            </div>

            <div className="p-4 rounded-2xl bg-cozy-850/80 border border-white/5 flex items-start gap-4">
              <div className="w-8 h-8 rounded-xl bg-cozy-800 flex items-center justify-center font-mono text-xs font-bold text-moss-vibrant shrink-0">
                03
              </div>
              <div>
                <h4 className="text-sm font-semibold text-parchment-100">Survive & Level Up</h4>
                <p className="text-xs text-parchment-dim mt-0.5">
                  Once the wave passes, Ribi celebrates with you. Your resilience score updates, strengthening your baseline focus.
                </p>
              </div>
            </div>
          </div>
        </div>

        {/* Right: Interactive Simulated Phone Screen */}
        <div className="lg:col-span-5 flex justify-center w-full">
          <div className="w-full max-w-[305px] sm:max-w-[340px] rounded-[38px] sm:rounded-[44px] p-3 sm:p-4 bg-cozy-950 border-[5px] sm:border-[6px] border-cozy-800 shadow-2xl shadow-black/80 relative">
            {/* Phone speaker notch */}
            <div className="w-20 sm:w-24 h-3.5 sm:h-4 bg-cozy-800 rounded-full mx-auto mb-3 sm:mb-4" />

            {/* Inner Phone Screen Content */}
            <div className="rounded-[28px] sm:rounded-[32px] bg-gradient-to-b from-[#1A0A06] via-cozy-900 to-[#0A0505] border border-white/5 p-4 sm:p-6 flex flex-col items-center text-center relative overflow-hidden min-h-[460px] sm:min-h-[510px]">
              
              {/* Top indicator */}
              <div className="w-full flex items-center justify-between text-[10px] sm:text-[11px] font-mono text-parchment-dim/60 mb-3 sm:mb-4">
                <span>SURGE BRIDGE</span>
                <span className="text-surge-orange">90s RESET</span>
              </div>

              {/* Ribi Icon */}
              <div className="w-12 h-12 sm:w-14 sm:h-14 rounded-2xl bg-cozy-800/80 p-1.5 sm:p-2 border border-white/10 mb-2.5 sm:mb-3 shadow-lg shadow-black/40">
                <img src={icRibiHeadImg} alt="Ribi Mascot" className="w-full h-full object-contain" />
              </div>

              <h3 className="font-serif text-lg sm:text-xl text-parchment-100 font-semibold mb-1">
                Ride the Wave
              </h3>
              <p className="text-[11px] sm:text-xs text-parchment-dim mb-3 sm:mb-4 max-w-[220px]">
                Cravings peak and dissolve in 90 seconds. What is driving this urge?
              </p>

              {/* Triggers Selector */}
              <div className="flex flex-wrap justify-center gap-1 sm:gap-1.5 mb-4">
                {triggers.map(t => (
                  <button
                    key={t.label}
                    onClick={() => setSelectedTrigger(t.label)}
                    className={`text-[10px] sm:text-[11px] px-2.5 py-1 rounded-full transition-all ${
                      selectedTrigger === t.label
                        ? 'bg-surge-orange text-white font-semibold shadow-sm shadow-surge-orange/40'
                        : 'bg-cozy-850 text-parchment-dim border border-white/5 hover:border-white/20'
                    }`}
                  >
                    {t.label}
                  </button>
                ))}
              </div>

              {/* 90-Second Wave Circular Display */}
              <div className="relative w-32 h-32 sm:w-36 sm:h-36 flex items-center justify-center my-auto">
                <svg className="w-full h-full -rotate-90">
                  <circle
                    cx="64"
                    cy="64"
                    r="52"
                    stroke="rgba(255, 255, 255, 0.08)"
                    strokeWidth="5"
                    fill="none"
                  />
                  <circle
                    cx="64"
                    cy="64"
                    r="52"
                    stroke="#E07A5F"
                    strokeWidth="5"
                    strokeDasharray={327}
                    strokeDashoffset={327 - (327 * progress) / 100}
                    strokeLinecap="round"
                    fill="none"
                    className="transition-all duration-1000 ease-linear"
                  />
                </svg>

                <div className="absolute flex flex-col items-center">
                  <span className="font-mono text-2xl sm:text-3xl font-bold text-parchment-100">
                    {secondsLeft}s
                  </span>
                  <span className="text-[9px] sm:text-[10px] uppercase font-mono text-surge-orange tracking-wider mt-0.5">
                    {survived ? 'Survived!' : isActive ? 'Surfing' : 'Ready'}
                  </span>
                </div>
              </div>

              {/* Interactive Controls */}
              <div className="w-full mt-4 space-y-2">
                {!isActive ? (
                  <button
                    onClick={handleStart}
                    className="w-full py-2.5 rounded-xl bg-surge-orange hover:bg-surge-orange/90 text-white font-medium text-xs tracking-wide transition-all shadow-md shadow-surge-orange/20 min-h-[42px]"
                  >
                    {survived ? 'Start Another Surge' : 'Begin 90-Second Reset'}
                  </button>
                ) : (
                  <button
                    onClick={handleReset}
                    className="w-full py-2.5 rounded-xl bg-cozy-800 text-parchment-dim hover:text-white font-mono text-xs flex items-center justify-center gap-1.5 transition-all min-h-[42px]"
                  >
                    <RotateCcw className="w-3.5 h-3.5" />
                    Reset Wave
                  </button>
                )}
                <div className="text-[10px] text-parchment-dim/60 font-mono">
                  Trigger: <span className="text-surge-orange">{selectedTrigger}</span>
                </div>
              </div>

            </div>
          </div>
        </div>

      </div>
    </section>
  );
}

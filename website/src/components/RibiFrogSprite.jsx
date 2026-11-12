import React, { useEffect, useRef, useState } from 'react';

import greenSheet from '../assets/frog_skins/frog_green_spritesheet.webp';
import blueSheet from '../assets/frog_skins/frog_blue_spritesheet.webp';
import purpleSheet from '../assets/frog_skins/frog_purple_spritesheet.webp';
import bawSheet from '../assets/frog_skins/frog_GameBoy_B&W_spritesheet.webp';
import brownSheet from '../assets/frog_skins/frog_brown_spritesheet.webp';
import clownSheet from '../assets/frog_skins/frog_clown_spritesheet.webp';
import cowboySheet from '../assets/frog_skins/frog_cowboy_spritesheet.webp';
import funnySheet from '../assets/frog_skins/frog_funnyglasses_spritesheet.webp';
import hatSheet from '../assets/frog_skins/frog_tophat_spritesheet.webp';
import tanpSheet from '../assets/frog_skins/frog_tan_pirate_spritesheet.webp';
import vikingSheet from '../assets/frog_skins/frog_viking_spritesheet.webp';

export const SKIN_MAP = {
  cowboy: { id: 'cowboy', name: 'Cowboy', sheet: cowboySheet, tagline: 'Western stetson & bandana', desc: 'Ready to herd your focus out of the digital noise.', color: '#D4956A' },
  green: { id: 'green', name: 'Classic Green', sheet: greenSheet, tagline: 'Quiet & grounded', desc: 'The original calm companion keeping you anchored.', color: '#7CB47A' },
  viking: { id: 'viking', name: 'Viking', sheet: vikingSheet, tagline: 'Horned helm & armor', desc: 'A steadfast guardian shielding your dopamine baseline.', color: '#3498DB' },
  hat: { id: 'hat', name: 'Dapper', sheet: hatSheet, tagline: 'Classic silk top hat', desc: 'Composed, stylish focus for deep work sessions.', color: '#2ECC71' },
  tanp: { id: 'tanp', name: 'Tropical', sheet: tanpSheet, tagline: 'Sun-warmed traveler', desc: 'Warm coastal tones for easy, unhurried pacing.', color: '#E67E22' },
  clown: { id: 'clown', name: 'Jester', sheet: clownSheet, tagline: 'Playful spirit', desc: 'A gentle reminder not to take notifications so seriously.', color: '#E74C3C' },
  blue: { id: 'blue', name: 'Ocean Blue', sheet: blueSheet, tagline: 'Cool deep sea', desc: 'Tranquil, steady focus for long attention blocks.', color: '#5B9BD5' },
  purple: { id: 'purple', name: 'Twilight', sheet: purpleSheet, tagline: 'Quiet evening purple', desc: 'Muted serenity designed for late day check-ins.', color: '#9B59B6' },
  baw: { id: 'baw', name: 'Monochrome', sheet: bawSheet, tagline: 'Clean ink & paper', desc: 'A minimalist black and white look with zero distractions.', color: '#BDBDBD' },
  brown: { id: 'brown', name: 'Earthy', sheet: brownSheet, tagline: 'Warm chestnut', desc: 'Grounded tones inspired by quiet autumn forests.', color: '#A0522D' },
  funny: { id: 'funny', name: 'Spectacles', sheet: funnySheet, tagline: 'Lighthearted glasses', desc: 'A quirky touch to diffuse heavy phone cravings.', color: '#F1C40F' },
};

// 32x32 pixel frames
const ANIMATIONS = {
  idle: {
    frames: [
      { x: 0, y: 0 },
      { x: 0, y: 32 },
      { x: 0, y: 64 },
    ],
    fps: 3,
    loop: true
  },
  hop: {
    frames: [
      { x: 64, y: 0 },
      { x: 64, y: 32 },
      { x: 64, y: 64 },
      { x: 64, y: 96 },
    ],
    fps: 6,
    loop: false
  },
  breath: {
    frames: [
      { x: 32, y: 0 },
      { x: 32, y: 32 },
      { x: 32, y: 64 },
      { x: 32, y: 96 },
      { x: 32, y: 64 },
      { x: 32, y: 32 },
    ],
    fps: 4.5,
    loop: true
  },
  scared: {
    frames: [
      { x: 96, y: 0 },
      { x: 96, y: 32 },
      { x: 96, y: 64 },
      { x: 96, y: 96 },
    ],
    fps: 5,
    loop: false
  }
};

export default function RibiFrogSprite({ skinId = 'cowboy', onHop }) {
  const canvasRef = useRef(null);
  const [currentAnim, setCurrentAnim] = useState('idle');
  const [ribbitBubble, setRibbitBubble] = useState(false);
  const animRef = useRef({ anim: 'idle', frameIdx: 0, lastTime: 0 });
  const imgRef = useRef(null);

  const skin = SKIN_MAP[skinId] || SKIN_MAP.green;

  useEffect(() => {
    const img = new Image();
    img.src = skin.sheet;
    img.onload = () => {
      imgRef.current = img;
    };
  }, [skin.sheet]);

  useEffect(() => {
    let reqId = null;

    const render = (time) => {
      const canvas = canvasRef.current;
      const img = imgRef.current;

      if (canvas && img) {
        const ctx = canvas.getContext('2d');
        const animDef = ANIMATIONS[animRef.current.anim] || ANIMATIONS.idle;
        const interval = 1000 / animDef.fps;

        if (time - animRef.current.lastTime >= interval) {
          animRef.current.lastTime = time;
          animRef.current.frameIdx++;

          if (animRef.current.frameIdx >= animDef.frames.length) {
            if (animDef.loop) {
              animRef.current.frameIdx = 0;
            } else {
              animRef.current.anim = 'idle';
              animRef.current.frameIdx = 0;
              setCurrentAnim('idle');
            }
          }
        }

        const activeDef = ANIMATIONS[animRef.current.anim] || ANIMATIONS.idle;
        const frame = activeDef.frames[animRef.current.frameIdx] || activeDef.frames[0];

        ctx.clearRect(0, 0, canvas.width, canvas.height);
        ctx.imageSmoothingEnabled = false;
        ctx.drawImage(
          img,
          frame.x, frame.y, 32, 32,
          0, 0, canvas.width, canvas.height
        );
      }

      reqId = requestAnimationFrame(render);
    };

    reqId = requestAnimationFrame(render);
    return () => cancelAnimationFrame(reqId);
  }, []);

  const triggerAnimation = (animName) => {
    animRef.current.anim = animName;
    animRef.current.frameIdx = 0;
    animRef.current.lastTime = performance.now();
    setCurrentAnim(animName);

    if (animName === 'hop') {
      setRibbitBubble(true);
      setTimeout(() => setRibbitBubble(false), 900);
      onHop?.();
    }
  };

  return (
    <div className="flex flex-col items-center select-none">
      <div 
        className="relative group cursor-pointer"
        onClick={() => triggerAnimation('hop')}
        title="Tap Ribi"
      >
        {/* Clean speech bubble with NO random emojis */}
        {ribbitBubble && (
          <div className="absolute -top-9 left-1/2 -translate-x-1/2 px-3.5 py-1 rounded-full bg-moss text-cozy-950 font-serif font-medium text-xs shadow-lg animate-bounce pointer-events-none whitespace-nowrap z-30">
            Ribbit!
          </div>
        )}

        {/* Ambient Backlight */}
        <div 
          className="absolute inset-0 rounded-full blur-2xl opacity-35 group-hover:opacity-60 transition-opacity duration-300"
          style={{ backgroundColor: skin.color }}
        />

      {/* Pixel Sprite Canvas */}
      <canvas
        ref={canvasRef}
        width={180}
        height={180}
        className="w-36 h-36 sm:w-48 sm:h-48 md:w-52 md:h-52 relative z-10 transform group-hover:scale-105 active:scale-95 transition-transform duration-150"
        style={{ imageRendering: 'pixelated' }}
      />
    </div>

    {/* Clean Animation Pills */}
    <div className="flex flex-wrap justify-center items-center gap-1 sm:gap-1.5 mt-3 sm:mt-4 p-1 rounded-xl bg-cozy-850 border border-white/5">
      <button
        onClick={() => triggerAnimation('idle')}
        className={`px-2.5 sm:px-3 py-1 rounded-lg text-[11px] sm:text-xs font-mono transition-all ${
          currentAnim === 'idle' ? 'bg-moss text-cozy-950 font-semibold' : 'text-parchment-dim hover:text-white'
        }`}
      >
        Idle
      </button>
      <button
        onClick={() => triggerAnimation('hop')}
        className={`px-2.5 sm:px-3 py-1 rounded-lg text-[11px] sm:text-xs font-mono transition-all ${
          currentAnim === 'hop' ? 'bg-moss text-cozy-950 font-semibold' : 'text-parchment-dim hover:text-white'
        }`}
      >
        Hop
      </button>
      <button
        onClick={() => triggerAnimation('breath')}
        className={`px-2.5 sm:px-3 py-1 rounded-lg text-[11px] sm:text-xs font-mono transition-all ${
          currentAnim === 'breath' ? 'bg-moss text-cozy-950 font-semibold' : 'text-parchment-dim hover:text-white'
        }`}
      >
        Breathe
      </button>
      <button
        onClick={() => triggerAnimation('scared')}
        className={`px-2.5 sm:px-3 py-1 rounded-lg text-[11px] sm:text-xs font-mono transition-all ${
          currentAnim === 'scared' ? 'bg-moss text-cozy-950 font-semibold' : 'text-parchment-dim hover:text-white'
        }`}
      >
        Scared
      </button>
    </div>

      <span className="text-[11px] font-mono text-parchment-dim/60 mt-2">
        Tap Ribi anytime to interact
      </span>
    </div>
  );
}

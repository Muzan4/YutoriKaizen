import React from 'react';
import icRibiHeadImg from '../assets/ic_ribi_head.png';
import { Heart, Sparkles } from 'lucide-react';

export default function Footer({ driveUrl }) {
  return (
    <footer className="border-t border-white/5 bg-cozy-950 py-16 px-4 sm:px-6 lg:px-8 text-center text-parchment-dim relative overflow-hidden">
      <div className="max-w-7xl mx-auto flex flex-col items-center space-y-8">
        
        {/* Brand mark */}
        <div className="flex items-center gap-3">
          <img src={icRibiHeadImg} alt="Ribi" className="w-9 h-9 object-contain rounded-xl bg-cozy-850 p-1 border border-white/10" />
          <span className="font-serif font-bold text-xl text-parchment-100 tracking-tight">
            Yutori Kaizen
          </span>
          <span className="font-handwriting text-xl text-moss-light">ゆとり改善</span>
        </div>

        {/* Links */}
        <div className="flex flex-wrap justify-center gap-6 sm:gap-8 text-sm">
          <a href="#hero" className="hover:text-parchment-100 transition-colors">Home</a>
          <a href="#philosophy" className="hover:text-parchment-100 transition-colors">Philosophy</a>
          <a href="#surge-bridge" className="hover:text-parchment-100 transition-colors">Surge Bridge</a>
          <a href="#features" className="hover:text-parchment-100 transition-colors">Features</a>
          <a href="#ribi" className="hover:text-parchment-100 transition-colors">Ribi Companion</a>
          <a href={driveUrl} target="_blank" rel="noopener noreferrer" className="hover:text-moss text-moss-light transition-colors font-medium">Download APK</a>
        </div>

        {/* Japanese sentiment quote */}
        <p className="max-w-md text-xs sm:text-sm font-serif italic text-parchment-dim/80">
          "Give your soul room to breathe, and let continuous gentle improvements rebuild your peaceful attention."
        </p>

        {/* Bottom copyright */}
        <div className="pt-8 border-t border-white/5 w-full flex flex-col sm:flex-row items-center justify-between text-xs text-parchment-dim/60 font-mono gap-4">
          <div>
            © {new Date().getFullYear()} Yutori Kaizen. All rights reserved.
          </div>
          <div className="flex items-center gap-1.5">
            <span>Crafted with intentional friction &</span>
            <Heart className="w-3.5 h-3.5 text-surge-orange fill-surge-orange inline" />
            <span>for peaceful minds.</span>
          </div>
        </div>

      </div>
    </footer>
  );
}

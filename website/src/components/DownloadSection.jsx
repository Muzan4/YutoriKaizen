import React, { useState } from 'react';
import ParticleText from './reactbits/ParticleText';
import { Download, ShieldCheck, Check, Copy, ExternalLink, HelpCircle, Smartphone, AlertTriangle } from 'lucide-react';

export default function DownloadSection({ driveUrl }) {
  const [copied, setCopied] = useState(false);

  const handleCopyLink = () => {
    navigator.clipboard.writeText(driveUrl);
    setCopied(true);
    setTimeout(() => setCopied(false), 2000);
  };

  return (
    <section id="download" className="py-16 sm:py-24 px-4 sm:px-6 lg:px-8 max-w-7xl mx-auto border-t border-white/5 relative">
      <div className="text-center max-w-3xl mx-auto mb-10 sm:mb-16">
        <span className="font-handwriting text-xl sm:text-2xl text-moss-light mb-1 block">
          Get Started Today
        </span>

        {/* ParticleText header (Active on mobile & desktop with responsive wrapping) */}
        <div className="w-full h-[110px] xs:h-[130px] sm:h-[140px] md:h-[170px] relative flex items-center justify-center overflow-visible">
          <ParticleText
            text="Install Yutori Kaizen"
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
          Directly distributed via Google Drive for maximum autonomy. No accounts required, no telemetry, no subscription lock-ins.
        </p>
      </div>

      {/* Main Download Card */}
      <div className="max-w-4xl mx-auto rounded-2xl sm:rounded-3xl bg-cozy-card border border-white/10 p-6 sm:p-10 md:p-12 relative overflow-hidden shadow-2xl mb-12 sm:mb-16">
        {/* Glow */}
        <div className="absolute top-0 right-1/4 w-96 h-96 bg-moss/10 rounded-full blur-[120px] pointer-events-none" />

        <div className="grid md:grid-cols-12 gap-8 items-center relative z-10">
          <div className="md:col-span-7 space-y-5">
            <div className="inline-flex items-center gap-2 px-3 py-1 rounded-full bg-moss/15 text-moss text-xs font-mono">
              <ShieldCheck className="w-3.5 h-3.5" />
              <span>Official Release Build</span>
            </div>

            <h3 className="font-serif text-3xl sm:text-4xl text-parchment-100 font-semibold leading-tight">
              Yutori Kaizen for Android
            </h3>

            <p className="text-sm text-parchment-dim leading-relaxed">
              Download the latest release APK directly from our verified Google Drive folder. Fast, secure, and ready to install on any Android 8.0+ smartphone.
            </p>

            <div className="flex flex-wrap gap-4 pt-2 text-xs font-mono text-parchment-dim">
              <div className="flex items-center gap-1.5">
                <span className="w-2 h-2 rounded-full bg-moss" />
                <span>Version 1.0.0</span>
              </div>
              <div className="flex items-center gap-1.5">
                <span className="w-2 h-2 rounded-full bg-moss" />
                <span>Android 8.0+ (Oreo to 15)</span>
              </div>
              <div className="flex items-center gap-1.5">
                <span className="w-2 h-2 rounded-full bg-moss" />
                <span>~32 MB • Lightweight & Fast</span>
              </div>
            </div>
          </div>

          <div className="md:col-span-5 flex flex-col gap-3">
            <a
              href={driveUrl}
              target="_blank"
              rel="noopener noreferrer"
              className="w-full py-4 px-6 rounded-2xl bg-moss hover:bg-moss-light text-cozy-950 font-semibold text-center flex items-center justify-center gap-3 transition-all duration-200 transform hover:-translate-y-0.5 shadow-xl shadow-moss/20 group"
            >
              <Download className="w-5 h-5 transition-transform group-hover:scale-110" />
              <span>Download from Google Drive</span>
              <ExternalLink className="w-4 h-4 opacity-60" />
            </a>

            <button
              onClick={handleCopyLink}
              className="w-full py-3 px-6 rounded-2xl bg-cozy-850 hover:bg-cozy-800 text-parchment-dim hover:text-parchment-100 border border-white/5 font-mono text-xs flex items-center justify-center gap-2 transition-all"
            >
              {copied ? (
                <>
                  <Check className="w-4 h-4 text-moss" />
                  <span>Google Drive Link Copied!</span>
                </>
              ) : (
                <>
                  <Copy className="w-4 h-4" />
                  <span>Copy Drive Download Link</span>
                </>
              )}
            </button>
          </div>
        </div>
      </div>

      {/* 3-Step Installation Guide */}
      <div className="max-w-4xl mx-auto mb-20">
        <h4 className="text-xl font-serif text-parchment-100 font-semibold text-center mb-8">
          Simple 3-Step Sideloading Guide
        </h4>

        <div className="grid sm:grid-cols-3 gap-6">
          <div className="p-6 rounded-2xl bg-cozy-card border border-white/5 relative">
            <div className="w-8 h-8 rounded-xl bg-cozy-800 text-moss font-mono text-sm font-bold flex items-center justify-center mb-4">
              1
            </div>
            <h5 className="font-semibold text-parchment-100 text-sm mb-1">Download the APK</h5>
            <p className="text-xs text-parchment-dim leading-relaxed">
              Click the Google Drive button above on your phone or PC and save the APK file.
            </p>
          </div>

          <div className="p-6 rounded-2xl bg-cozy-card border border-white/5 relative">
            <div className="w-8 h-8 rounded-xl bg-cozy-800 text-moss font-mono text-sm font-bold flex items-center justify-center mb-4">
              2
            </div>
            <h5 className="font-semibold text-parchment-100 text-sm mb-1">Allow Unknown Sources</h5>
            <p className="text-xs text-parchment-dim leading-relaxed">
              Tap the downloaded file. When Android prompts <em>"Install unknown apps"</em>, toggle enable for your browser.
            </p>
          </div>

          <div className="p-6 rounded-2xl bg-cozy-card border border-white/5 relative">
            <div className="w-8 h-8 rounded-xl bg-cozy-800 text-moss font-mono text-sm font-bold flex items-center justify-center mb-4">
              3
            </div>
            <h5 className="font-semibold text-parchment-100 text-sm mb-1">Enable Accessibility</h5>
            <p className="text-xs text-parchment-dim leading-relaxed">
              Open the app and grant Accessibility Service. This only detects when selected apps open — no screen content is read.
            </p>
          </div>
        </div>
      </div>

      {/* Trust & FAQ Accordion */}
      <div className="max-w-3xl mx-auto space-y-4">
        <h4 className="text-xl font-serif text-parchment-100 font-semibold text-center mb-6">
          Frequently Answered Questions
        </h4>

        <div className="p-5 rounded-2xl bg-cozy-card border border-white/5">
          <h5 className="text-sm font-semibold text-parchment-100 mb-1 flex items-center gap-2">
            <HelpCircle className="w-4 h-4 text-moss" />
            <span>Why does the app ask for Accessibility Permission?</span>
          </h5>
          <p className="text-xs text-parchment-dim leading-relaxed pl-6">
            Android does not let third-party apps detect which app package was opened without Accessibility Service. Yutori Kaizen uses this strictly to recognize when you launch distracting apps (like Instagram or TikTok) so it can display the gentle Surge pause. No keystrokes, personal messages, or screen pixels are ever observed.
          </p>
        </div>

        <div className="p-5 rounded-2xl bg-cozy-card border border-white/5">
          <h5 className="text-sm font-semibold text-parchment-100 mb-1 flex items-center gap-2">
            <HelpCircle className="w-4 h-4 text-moss" />
            <span>Is my data sent to any cloud server?</span>
          </h5>
          <p className="text-xs text-parchment-dim leading-relaxed pl-6">
            Zero cloud servers. All habit logs, NCI scores, trigger records, and Ribi customization data are saved locally in an on-device SQLite database. You can use the app completely in Airplane Mode.
          </p>
        </div>

        <div className="p-5 rounded-2xl bg-cozy-card border border-white/5">
          <h5 className="text-sm font-semibold text-parchment-100 mb-1 flex items-center gap-2">
            <HelpCircle className="w-4 h-4 text-moss" />
            <span>Is Yutori Kaizen coming to the Google Play Store?</span>
          </h5>
          <p className="text-xs text-parchment-dim leading-relaxed pl-6">
            Yes! A Google Play Store submission is actively prepared. Direct APK distribution lets early supporters access the latest builds immediately without waiting for store review delays.
          </p>
        </div>
      </div>
    </section>
  );
}

import React from 'react';
import BubbleMenu from './components/reactbits/BubbleMenu';
import Hero from './components/Hero';
import Philosophy from './components/Philosophy';
import SurgeShowcase from './components/SurgeShowcase';
import FeaturesGrid from './components/FeaturesGrid';
import RibiShowcase from './components/RibiShowcase';
import DownloadSection from './components/DownloadSection';
import Footer from './components/Footer';
import icRibiHeadImg from './assets/ic_ribi_head.png';

// Google Drive public download link for beta APK
export const GOOGLE_DRIVE_DOWNLOAD_URL = "https://drive.google.com/file/d/1rZWZramDUcQnE_3uV-bss5N0-tQ0KaMQ/view?usp=sharing";

export default function App() {
  const menuItems = [
    {
      label: 'Home',
      href: '#hero',
      ariaLabel: 'Home',
      rotation: -6,
      hoverStyles: { bgColor: '#84A98C', textColor: '#111110' }
    },
    {
      label: 'Philosophy',
      href: '#philosophy',
      ariaLabel: 'Philosophy',
      rotation: 6,
      hoverStyles: { bgColor: '#E8E2D5', textColor: '#111110' }
    },
    {
      label: 'Surge Bridge',
      href: '#surge-bridge',
      ariaLabel: 'Surge Bridge',
      rotation: -4,
      hoverStyles: { bgColor: '#E07A5F', textColor: '#FFFFFF' }
    },
    {
      label: 'Features',
      href: '#features',
      ariaLabel: 'Features',
      rotation: 5,
      hoverStyles: { bgColor: '#A8C69F', textColor: '#111110' }
    },
    {
      label: 'Ribi Mascot',
      href: '#ribi',
      ariaLabel: 'Ribi Mascot',
      rotation: -5,
      hoverStyles: { bgColor: '#98D8AA', textColor: '#111110' }
    },
    {
      label: 'Download APK',
      href: '#download',
      ariaLabel: 'Download',
      rotation: 4,
      hoverStyles: { bgColor: '#F5F2EB', textColor: '#111110' }
    }
  ];

  const logoNode = (
    <div className="flex items-center gap-2.5">
      <img src={icRibiHeadImg} alt="Ribi" className="w-7 h-7 object-contain rounded-lg" />
      <span className="font-serif font-bold text-sm tracking-tight text-parchment-100 hidden sm:inline">
        Yutori Kaizen
      </span>
      <span className="font-handwriting text-xs text-moss-light hidden md:inline">ゆとり</span>
    </div>
  );

  return (
    <div className="min-h-screen bg-cozy-900 text-parchment-100 relative grain-overlay selection:bg-moss/20 selection:text-moss-vibrant">
      {/* React Bits BubbleMenu floating navigation */}
      <BubbleMenu
        logo={logoNode}
        items={menuItems}
        menuAriaLabel="Toggle navigation"
        menuBg="#181816"
        menuContentColor="#F5F2EB"
        useFixedPosition={true}
        animationEase="back.out(1.5)"
        animationDuration={0.45}
        staggerDelay={0.08}
      />

      {/* Main Page Content */}
      <main>
        <Hero driveUrl={GOOGLE_DRIVE_DOWNLOAD_URL} />
        <Philosophy />
        <SurgeShowcase />
        <FeaturesGrid />
        <RibiShowcase />
        <DownloadSection driveUrl={GOOGLE_DRIVE_DOWNLOAD_URL} />
      </main>

      <Footer driveUrl={GOOGLE_DRIVE_DOWNLOAD_URL} />
    </div>
  );
}

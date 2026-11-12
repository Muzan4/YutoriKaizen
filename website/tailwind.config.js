/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        cozy: {
          950: '#0B0B0A',
          900: '#111110',
          850: '#161614',
          800: '#1C1C19',
          700: '#282824',
          border: 'rgba(255, 255, 255, 0.08)',
          card: '#181816',
        },
        moss: {
          DEFAULT: '#84A98C',
          light: '#A8C69F',
          dark: '#52796F',
          vibrant: '#98D8AA',
        },
        parchment: {
          50: '#FAF8F5',
          100: '#F5F2EB',
          200: '#E8E2D5',
          300: '#D5CCBB',
          dim: '#9E9789',
        },
        surge: {
          orange: '#E07A5F',
          glow: 'rgba(224, 122, 95, 0.25)',
        }
      },
      fontFamily: {
        serif: ['"Fraunces"', 'serif'],
        sans: ['"DM Sans"', 'sans-serif'],
        handwriting: ['"Caveat"', 'cursive'],
        mono: ['"JetBrains Mono"', 'monospace'],
      },
      animation: {
        'pulse-subtle': 'pulseSubtle 4s ease-in-out infinite',
        'float-slow': 'floatSlow 6s ease-in-out infinite',
      },
      keyframes: {
        pulseSubtle: {
          '0%, 100%': { opacity: '0.2' },
          '50%': { opacity: '0.45' },
        },
        floatSlow: {
          '0%, 100%': { transform: 'translateY(0px)' },
          '50%': { transform: 'translateY(-8px)' },
        }
      }
    },
  },
  plugins: [],
};

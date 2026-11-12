import urllib.request
import os

fonts = {
    'dm_sans.ttf': 'https://raw.githubusercontent.com/google/fonts/main/ofl/dmsans/DMSans%5Bopsz%2Cwght%5D.ttf',
    'fraunces.ttf': 'https://raw.githubusercontent.com/google/fonts/main/ofl/fraunces/Fraunces%5BSOFT%2CWONK%2Copsz%2Cwght%5D.ttf',
    'caveat.ttf': 'https://raw.githubusercontent.com/google/fonts/main/ofl/caveat/Caveat%5Bwght%5D.ttf'
}

font_dir = 'app/src/main/res/font'
os.makedirs(font_dir, exist_ok=True)

for name, url in fonts.items():
    print(f"Downloading {name}...")
    try:
        urllib.request.urlretrieve(url, os.path.join(font_dir, name))
        print(f"Success: {name}")
    except Exception as e:
        print(f"Failed to download {name}: {e}")

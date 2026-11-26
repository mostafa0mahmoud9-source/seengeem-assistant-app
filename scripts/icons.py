from PIL import Image,ImageDraw
from pathlib import Path
p=Path('desktop');p.mkdir(exist_ok=True)
im=Image.new('RGBA',(512,512),'#332267');d=ImageDraw.Draw(im);d.rounded_rectangle((95,95,417,417),70,fill='#b8f1d7');d.line((165,260,235,330,355,190),fill='#332267',width=32);im.save(p/'icon.png');im.save(p/'icon.ico',sizes=[(16,16),(32,32),(48,48),(64,64),(128,128),(256,256)])

import hashlib,json,os,pathlib
v=json.loads(pathlib.Path('package.json').read_text())['version'];a,b,c=map(int,v.split('.'));apk=pathlib.Path('release-assets')/f'SeenGeem-Assistant-{v}.apk'
if apk.exists():
 data={'version':v,'versionCode':a*1000000+b*1000+c,'sha256':hashlib.sha256(apk.read_bytes()).hexdigest(),'url':f'https://github.com/{os.environ.get("GITHUB_REPOSITORY","mostafa0mahmoud9-source/seengeem-assistant-app")}/releases/download/v{v}/{apk.name}'}
 (apk.parent/'android-update.json').write_text(json.dumps(data,indent=2))

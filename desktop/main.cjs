'use strict';
const {app,BrowserWindow,Menu,shell,session,dialog}=require('electron');
const path=require('node:path');const fs=require('node:fs');
const {autoUpdater}=require('electron-updater');
const {APP_URL,internal,external}=require('./policy.cjs');
let win,checking=false,updateReady=false;
const single=app.requestSingleInstanceLock();if(!single)app.quit();
app.on('second-instance',()=>{if(win){if(win.isMinimized())win.restore();win.show();win.focus();}});
function log(message){try{fs.appendFileSync(path.join(app.getPath('userData'),'updates.log'),new Date().toISOString()+' '+message+'\n');}catch{}}
function check(){if(!app.isPackaged||checking)return;checking=true;autoUpdater.checkForUpdates().catch(()=>log('Update check failed')).finally(()=>checking=false);}
function create(){
 const store=session.fromPartition('persist:seengeem-assistant');
 store.setPermissionRequestHandler((_contents,_permission,callback)=>callback(false));
 store.setPermissionCheckHandler(()=>false);
 win=new BrowserWindow({width:1360,height:900,minWidth:850,minHeight:600,title:'مساعد سين جيم',backgroundColor:'#f5f4f9',icon:path.join(__dirname,'icon.png'),webPreferences:{partition:'persist:seengeem-assistant',nodeIntegration:false,contextIsolation:true,sandbox:true,webSecurity:true,allowRunningInsecureContent:false}});
 Menu.setApplicationMenu(Menu.buildFromTemplate([{label:'مساعد سين جيم',submenu:[{label:'المنصة',click:()=>win.loadURL(APP_URL)},{label:'تحديث البيانات',accelerator:'CmdOrCtrl+R',click:()=>win.webContents.reload()},{label:'فحص تحديث البرنامج',click:check},{label:'الإصدار '+app.getVersion(),enabled:false},{type:'separator'},{label:'إغلاق',role:'quit'}]},{label:'العرض',submenu:[{role:'zoomIn',label:'تكبير'},{role:'zoomOut',label:'تصغير'},{role:'resetZoom',label:'الحجم الطبيعي'},{role:'togglefullscreen',label:'ملء الشاشة'}]}]));
 win.webContents.on('will-navigate',(event,url)=>{if(!internal(url)){event.preventDefault();if(external(url))shell.openExternal(url);}});
 win.webContents.setWindowOpenHandler(({url})=>{if(internal(url))win.loadURL(url);else if(external(url))shell.openExternal(url);return {action:'deny'};});
 win.webContents.on('will-attach-webview',event=>event.preventDefault());
 win.webContents.on('did-fail-load',(_ev,code,_desc,url,isMain)=>{if(isMain&&code!==-3&&internal(url))win.loadFile(path.join(__dirname,'offline.html'));});
 win.loadURL(APP_URL);win.on('closed',()=>win=null);
}
app.whenReady().then(()=>{if(!single)return;create();autoUpdater.autoDownload=true;autoUpdater.autoInstallOnAppQuit=true;autoUpdater.allowPrerelease=false;autoUpdater.allowDowngrade=false;
 autoUpdater.on('error',()=>log('Update request failed; installed version retained'));
 autoUpdater.on('update-downloaded',()=>{updateReady=true;log('Update downloaded, will install on normal exit');if(win)win.setTitle('مساعد سين جيم — التحديث جاهز للإغلاق القادم');});
 setTimeout(check,15000);setInterval(check,6*60*60*1000).unref();
});
app.on('window-all-closed',()=>app.quit());

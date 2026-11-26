'use strict';
const APP_URL='https://seengeem-eg.com/seengeem-assistant/';
function internal(value){try{const u=new URL(value);return u.protocol==='https:'&&u.hostname==='seengeem-eg.com'&&!u.username&&!u.password&&(!u.port||u.port==='443');}catch{return false;}}
function external(value){try{const u=new URL(value);return ['https:','mailto:'].includes(u.protocol)&&!u.username&&!u.password;}catch{return false;}}
module.exports={APP_URL,internal,external};

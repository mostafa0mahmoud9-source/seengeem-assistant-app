package com.seengeem.assistant;
import android.app.*;import android.content.*;import android.graphics.Color;import android.net.*;import android.net.http.SslError;import android.os.*;import android.provider.Settings;import android.view.*;import android.webkit.*;import android.widget.*;
public final class MainActivity extends Activity {
 static final String HOME="https://seengeem-eg.com/seengeem-assistant/"; WebView web;Button update;TextView status;UpdateManager updater;
 @Override public void onCreate(Bundle saved){super.onCreate(saved);getWindow().setStatusBarColor(Color.rgb(51,34,103));
 LinearLayout root=new LinearLayout(this);root.setOrientation(1);root.setBackgroundColor(Color.rgb(245,244,249));
 LinearLayout bar=new LinearLayout(this);bar.setPadding(8,3,8,3);bar.setGravity(Gravity.CENTER_VERTICAL);Button home=new Button(this);home.setText("الرئيسية");home.setOnClickListener(v->web.loadUrl(HOME));Button reload=new Button(this);reload.setText("تحديث");reload.setOnClickListener(v->web.reload());update=new Button(this);update.setText("الإصدار "+BuildConfig.VERSION_NAME);update.setTextSize(11);update.setOnClickListener(v->{if(updater.ready())install();else updater.check(true);});bar.addView(home);bar.addView(reload);bar.addView(update,new LinearLayout.LayoutParams(0,-2,1));root.addView(bar);
 status=new TextView(this);status.setPadding(16,2,16,2);status.setTextSize(12);status.setText("بيانات مباشرة من المنصة عند الاتصال بالإنترنت");root.addView(status);
 web=new WebView(this);root.addView(web,new LinearLayout.LayoutParams(-1,0,1));setContentView(root);
 WebSettings s=web.getSettings();s.setJavaScriptEnabled(true);s.setDomStorageEnabled(true);s.setAllowFileAccess(false);s.setAllowContentAccess(false);s.setMixedContentMode(WebSettings.MIXED_CONTENT_NEVER_ALLOW);s.setCacheMode(WebSettings.LOAD_NO_CACHE);s.setSupportMultipleWindows(false);CookieManager.getInstance().setAcceptCookie(true);CookieManager.getInstance().setAcceptThirdPartyCookies(web,false);
 web.setWebViewClient(new WebViewClient(){
  public boolean shouldOverrideUrlLoading(WebView w,WebResourceRequest request){Uri u=request.getUrl();if("https".equals(u.getScheme())&&"seengeem-eg.com".equals(u.getHost())&&u.getUserInfo()==null&&(u.getPort()==-1||u.getPort()==443))return false;if("https".equals(u.getScheme())||"mailto".equals(u.getScheme()))try{startActivity(new Intent(Intent.ACTION_VIEW,u));}catch(Exception ignored){}return true;}
  public void onReceivedSslError(WebView w,SslErrorHandler handler,SslError error){handler.cancel();status.setText("تعذر التحقق من أمان اتصال المنصة");}
  public void onReceivedError(WebView w,WebResourceRequest r,WebResourceError error){if(r.isForMainFrame())status.setText("الاتصال غير متاح — أعد المحاولة من زر الرئيسية");}
  public void onPageFinished(WebView w,String url){CookieManager.getInstance().flush();if(url.startsWith(HOME))status.setText("متابعة المنصة — الإصدار "+BuildConfig.VERSION_NAME);}
 });web.setWebChromeClient(new WebChromeClient(){public void onPermissionRequest(PermissionRequest r){r.deny();}});
 updater=new UpdateManager(this);if(saved!=null)web.restoreState(saved);else web.loadUrl(HOME);updater.check(false);
 }
 void notice(String value){runOnUiThread(()->{status.setText(value);if(updater.ready())update.setText("تثبيت التحديث");});}
 void install(){if(Build.VERSION.SDK_INT>=26&&!getPackageManager().canRequestPackageInstalls()){startActivity(new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES,Uri.parse("package:"+getPackageName())));return;}try{Intent i=new Intent(Intent.ACTION_VIEW);i.setDataAndType(Uri.parse("content://com.seengeem.assistant.updates/update.apk"),"application/vnd.android.package-archive");i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);startActivity(i);}catch(Exception e){notice("تعذر فتح مثبت التحديث");}}
 @Override protected void onResume(){super.onResume();if(web!=null)web.onResume();if(updater!=null)updater.check(false);}
 @Override protected void onPause(){if(web!=null)web.onPause();super.onPause();}
 @Override protected void onSaveInstanceState(Bundle b){web.saveState(b);super.onSaveInstanceState(b);}
 @Override public void onBackPressed(){if(web.canGoBack())web.goBack();else super.onBackPressed();}
 @Override protected void onDestroy(){if(updater!=null)updater.close();if(web!=null)web.destroy();super.onDestroy();}
}

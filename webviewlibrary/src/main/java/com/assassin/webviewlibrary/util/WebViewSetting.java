package com.assassin.webviewlibrary.util;

import android.content.Context;
import android.os.Build;
import android.webkit.WebSettings;
import android.webkit.WebView;

import java.io.File;

/**
 * @Author: Shay-Patrick-Cormac
 * @Date: 2017/5/18 14:03
 * @Version: 1.0
 * @Description: webView的基本设置
 */

public class WebViewSetting 
{

    //webView的缓存文件夹名称
    private static final String WEBVIEW_CACHE_PATH = "web_view_h5_cache";

    public static void webViewDefaultSetting(Context context, WebView webview)
    {
        if (context == null || webview == null)
            return;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(false);
        }

        WebSettings webSettings = webview.getSettings();
        
        //支持扩展插件
        webSettings.setPluginState(WebSettings.PluginState.ON);
        
        // 缩放操作
        //支持缩放，默认为true。是下面那个的前提。
        webSettings.setSupportZoom(true);
        //设置内置的缩放控件。若为false，则该WebView不可缩放
        webSettings.setBuiltInZoomControls(true);
        //隐藏原生的缩放控件
        webSettings.setDisplayZoomControls(false);
        //设置自适应屏幕，两者合用
        //将图片调整到适合webview的大小
         webSettings.setUseWideViewPort(true);
        // 缩放至屏幕的大小
         webSettings.setLoadWithOverviewMode(true);
      
        // 设置是否允许 WebView 使用 File 协议
        //存在安全性问题，暂时关闭 ,需要的时候打开，即使用时禁止 file 协议加载 JavaScript
        webSettings.setAllowFileAccess(false);
      
// 在 onStop 和 onResume 里分别把 setJavaScriptEnabled() 给设置成 false 和 true 即可
       // webSettings.setJavaScriptEnabled(true);
        //设置允许js弹出alert对话框
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
       
        webSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);
        //缓存模式(采用默认的，根据cache-control决定是否从网络上取数据)
        if (NetWorkUtils.isNetworkConnected(context))
        {
            webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        }else 
        {

            webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        }

        
        //设置 Dom Storage 缓存机制
        webSettings.setDomStorageEnabled(true);
        //设置web sql data缓存机制
        webSettings.setDatabaseEnabled(true);
       //设置Application Cache 缓存机
       String sCacheDirPath = context.getApplicationContext().getDir(WEBVIEW_CACHE_PATH, Context.MODE_PRIVATE).getPath();
      //  每个 Application 只调用一次 WebSettings.setAppCachePath() 和setAppCacheMaxSize()
        //设置数据库缓存路径  
        webSettings.setAppCachePath(sCacheDirPath);
        webSettings.setAppCacheMaxSize(20*1024*1024);
        webSettings.setAppCacheEnabled(true);
        
        //websetting 设置密码保存提醒
        webSettings.setSavePassword(false);
//这个是加载的地址是https的，一些资源文件使用的是http方法的，从安卓4.4之后对webview安全机制有了加强，webview里面加载https url的时候，如果里面需要加载http的资源或者重定向的时候，webview会block页面加载。需要设置MixedContentMode
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);}


            //todo 设置cookie 
        
      /*  if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            CookieSyncManager.createInstance(context);
        }
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);

        CookieStore cookieStore = OkGo.getInstance().getCookieJar().getCookieStore();
        List<Cookie> cookies = cookieStore.getAllCookie();
        
        if (cookies != null && !cookies.isEmpty())
            for (Cookie sessionCookie : cookies) 
            {
                String cookieString = sessionCookie.name() + "="
                        + sessionCookie.value() + "; domain="
                        + sessionCookie.domain();
                cookieManager.setCookie(sessionCookie.domain(), cookieString);
            }
        CookieSyncManager.getInstance().sync();*/
        
    }

    /**
     * 清除WebView缓存 
     */
    public static void clearWebViewCache(Context context){

        //清理Webview缓存数据库  
        try {
            context.deleteDatabase("webview.db");
            context.deleteDatabase("webviewCache.db");
        } catch (Exception e) {
            e.printStackTrace();
        }

        //WebView 缓存文件  
        String sCacheDirPath = context.getApplicationContext().getDir(WEBVIEW_CACHE_PATH, Context.MODE_PRIVATE).getPath();
        File appCacheDir = new File(sCacheDirPath);

        File webviewCacheDir = new File(context.getCacheDir().getAbsolutePath()+"/webviewCache");

        //删除webview 缓存目录  
        if(webviewCacheDir.exists()){
            deleteFile(webviewCacheDir);
        }
        //删除webview 缓存 缓存目录  
        if(appCacheDir.exists()){
            deleteFile(appCacheDir);
        }
    }

    /**
     * 递归删除 文件/文件夹 
     *
     * @param file
     */
    private static void deleteFile(File file) {
        if (file.exists()) 
        {
            if (file.isFile()) {
                file.delete();
            } else if (file.isDirectory()) {
                File files[] = file.listFiles();
                for (int i = 0; i < files.length; i++) {
                    deleteFile(files[i]);
                }
            }
            file.delete();
        }
    }
    
}

package com.assassin.webviewlibrary.util;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.webkit.JavascriptInterface;

import com.assassin.webviewlibrary.provider.SPHelper;

/**
 * WebView中用来跟页面交互js的类,其中的方法,
 * 可以在页面上通过{@link WebViewJSInterface#NAME}.方法名(参数)来调用
 */
public class WebViewJSInterface 
{

    /**
     * 回调接口，注意是否需要在主线程中调用
     */
    public interface WebViewListener 
    {
        /**
         * 设置toolbar上的title
         *
         * @param title
         */
        void setTitle(String title);

        /**
         * 关闭当前页面
         */
         void close();

        /**
         * 设置toolbar显示与否
         */
         void setToolBarVisible(boolean isVisible);
        
    }

    /**
     * NAME = "GMQuality" 为了兼容之前的质量app，只能用这个名字
     */
    public static final String NAME = "GMQuality";
    private Context mContext ;
    private WebViewListener listener ;
    private Fragment fragment;
   

    /**
     * Instantiate the interface and set the context
     */
    public WebViewJSInterface(Context mContext) {
        this.mContext = mContext;
       
    }

    public WebViewJSInterface(Context mContext, WebViewListener listener) {
        super();
        this.mContext = mContext;
        this.listener = listener;
       
    }
    /**
     * fragment传过来
     */
    public WebViewJSInterface(Context mContext, Fragment fragment, WebViewListener listener) {
        super();
        this.mContext = mContext;
        this.fragment = fragment;
       
        this.listener = listener;
    }
    
    /**
     * 设置网页的Title
     *
     * @return
     */
    @JavascriptInterface
    public void setTitle(final String title) {
        if (listener != null)
            new Handler(Looper.getMainLooper()).post(new Runnable() {

                @Override
                public void run() {
                    listener.setTitle(title);

                }
            });

    }
    
    /**
     * JS调用打印Log
     *
     * @param message log message
     */
    @JavascriptInterface
    public void log(final String message) {
        Log.i("Js调用LogCat", message);
    }
    
    /**
     * 关闭WebView所在的Activity
     */
    @JavascriptInterface
    public void closeWeb() {
        if (listener != null)
            new Handler(Looper.getMainLooper()).post(new Runnable() {

                @Override
                public void run() {
                    listener.close();

                }
            });
    }

    /**
     * 设置ToolBar显示与否
     */
    @JavascriptInterface
    public void setToolBarVisible(final boolean isVisible) {
        if (listener != null)
            new Handler(Looper.getMainLooper()).post(new Runnable() {

                @Override
                public void run() {
                    listener.setToolBarVisible(isVisible);
                }
            });
    }

 
    
  
    //获取当前系统的时间
    @JavascriptInterface
    public long getCurrentTime()
    {
        long currentTime = SPHelper.getLong(Param.LOCAL.CLICK_H5_SHORT_CUT, 1000L);
        return currentTime;
    }


    /**
     * 获取当前用户的token
     */
    @JavascriptInterface
    public String getToken()
    {
        String token = SPHelper.getString(Param.LOCAL.TOKEN, "");
        return token;
        
    }
    @JavascriptInterface
    public void refreshToken()
    {
        SPHelper.save(Param.LOCAL.TOKEN, "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJhY2NvdW50IjoibHNtIiwiam9iQ29kZSI6IkoyMTA0ODgiLCJ1c2VySUQiOjYxNjgsIm5iZiI6MTU0NTc4ODUzMCwiaXNzdWUiOiJnb2xkTWFudGlzIiwiZXhwIjoxNTMwMDY0ODAwfQ.JsDnlfthLLKQnta_LsowfMnvrhCsMQtdeL4z16Vvud4");
    }

    
}

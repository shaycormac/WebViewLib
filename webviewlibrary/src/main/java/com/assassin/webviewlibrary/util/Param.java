package com.assassin.webviewlibrary.util;

/**
 * Author: Shay-Patrick-Cormac
 * Date: 2018/6/19 0019 10:43
 * Version: 1.0
 * Description: 静态常量
 */

public class Param 
{
   
    
    
    public static class INTENT_PARAM
    {
        /**
         * 地址链接
         */
        public static final String URL = "url";
        /**
         * 标题内容
         */
        public static final String TITLE = "title";
        /**
         * 是否显示actionBar
         */
        public static final String SHOW_TITLE = "showTitle";
        
    }
    
    
    public static  class REQUEST_CODE
    {
        public static final int FILE_CHOOSER = 101;
        public static final int TAKE_PHOTO_RESULTCODE = 102;
    }


    public static class LOCAL {
        public static final String TOKEN = "TOKEN";  //后台的token令牌。
        //点击进入H5页面的当前时间
        public static final String CLICK_H5_SHORT_CUT = "CLICK_H5_SHORT_CUT";
    }
}

package com.assassin.webviewlibrary.util;

import android.app.DownloadManager;
import android.app.DownloadManager.Query;
import android.app.DownloadManager.Request;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.widget.Toast;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * 自带系统下载
 */
public class DownloadUtil {
    private String mUrl;
    public Context mContext;
    private String mFileDescription;
    private String mFileName;
    private int mAllowedNetworkTypes = ~0; // default to all network types
    // allowed

    public DownloadUtil(Context context, String url, String fileDescription,
                        String fileName) {
        mContext = context;
        mUrl = url;
        mFileDescription = fileDescription;
        mFileName = fileName;
    }

    public void startDownload() {
        //先校验当前内存空间是否足够
        if (!storageAvailableForDownload()) {
            Toast.makeText(mContext, "sd卡当前不可用或者空间不足", Toast.LENGTH_SHORT).show();
            return;
        }

        Request request = new Request(Uri.parse(mUrl));
        //通知栏出现的消息提示
        request.setDescription(String.format("正在下载%s,请稍候。。。", mFileName));
        request.setTitle(mFileDescription);

        String downloaddir = Environment.DIRECTORY_DOWNLOADS;
        if (downloaddir.contains("://")) {
            downloaddir = "download";
        }
        //2018-6-20 使用内置的目录，不走外置的目录。避免使用储存卡申请的权限。

        // Environment.getExternalStoragePublicDirectory(downloaddir).mkdir();

        request.setDestinationInExternalFilesDir(mContext, downloaddir, mFileName);
        // request.setDestinationInExternalPublicDir(downloaddir, mFileName);
        DownloadManager manager = (DownloadManager) mContext.getSystemService(Context
                .DOWNLOAD_SERVICE);
        //设置下载需要的网络环境
        request.setAllowedNetworkTypes(mAllowedNetworkTypes);
        try {
            //加入下载队列
            manager.enqueue(request);
            Toast.makeText(mContext, "开始下载" + mFileName, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {

            //出现错误就采用默认下载
            downloadAsDefault();
        }

    }

    public void setOnlyWifiAvalible(boolean avalible) {
        if (avalible) {
            mAllowedNetworkTypes = Request.NETWORK_WIFI;
        }
    }

    /**
     * 判断当前uri是否正在下载或者已经下载完成 (2.3以下版本直接返回false)
     *
     * @return
     */
    public static boolean isDownloading(String url, Context pContext) {
        if (TextUtils.isEmpty(url))
            return false;
        //获取下载服务器
        DownloadManager downloadManager = (DownloadManager) pContext.getSystemService(Context
                .DOWNLOAD_SERVICE);
        //获取游标
        Cursor cursor = downloadManager.query(new Query());
        if (cursor == null) {
            return false;
        }
        while (cursor.moveToNext()) {
            String szDownloadUri = cursor.getString(cursor.getColumnIndex(DownloadManager
                    .COLUMN_URI));
            if (!url.equals(szDownloadUri))
                continue;
            int status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
            switch (status) {
                case DownloadManager.STATUS_FAILED:
                    cursor.close();
                    return false;
                case DownloadManager.STATUS_PAUSED:
                case DownloadManager.STATUS_PENDING:
                case DownloadManager.STATUS_RUNNING:
                    cursor.close();
                    return true;
                case DownloadManager.STATUS_SUCCESSFUL:
                    cursor.close();
                    return false;
                default:
                    cursor.close();
                    return false;
            }
        }

        cursor.close();
        return false;
    }

    /**
     * 获取下载过的apk所在的本地位置
     *
     * @param url 根据所给的url
     * @return
     */
    public static File getDownloadFile(String url, Context pContext) {
        if (TextUtils.isEmpty(url))
            return null;

        DownloadManager downloadManager = (DownloadManager) pContext.getSystemService(Context
                .DOWNLOAD_SERVICE);
        Cursor cursor = downloadManager.query(new Query());

        if (cursor == null) {
            return null;
        }
        while (cursor.moveToNext()) {
            String szDownloadUri = cursor.getString(cursor.getColumnIndex(DownloadManager
                    .COLUMN_URI));
            if (!url.equals(szDownloadUri))
                continue;
            int status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
            switch (status) {
                case DownloadManager.STATUS_SUCCESSFUL:
                    String szLocalUri = cursor.getString(cursor.getColumnIndex(DownloadManager
                            .COLUMN_LOCAL_URI));
                    cursor.close();
                    return new File(Uri.parse(szLocalUri).getPath());
            }
        }
        cursor.close();
        return null;

    }

    /**
     * 低于9的采用默认下载器下载
     */
    private void downloadAsDefault() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(mUrl));
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);
    }

    /**
     * Checks whether the space is enough for download.
     * 检测内存空间是否可用，便于下载
     * the download needs to check whether the space is enough
     *
     * @return
     */
    private boolean storageAvailableForDownload() {
        // Make sure the space can satisfy the current download, so
        // we will add the size of running downloads.
        long neededspace = Storage.APK_SIZE + Storage.RESERVED_SPACE;

        // Calculate the needed space for current download.
        long cacheSpace = Storage.cachePartitionAvailableSpace();
        long dataSpace = Storage.dataPartitionAvailableSpace();
        long externalStorageSpace = Storage.externalStorageAvailableSpace();

        if (Storage.externalStorageAvailable()) {
            if (externalStorageSpace < neededspace) {
                return false;
            }
        } else if (dataSpace < neededspace) {
            return false;
        }

        return true;
    }


    /**
     * 根据contentDisposition ，url获取文件名
     */
    public static String getNetFileName(String contentDisposition, String url) {
        String fileName = getHeaderFileName(contentDisposition);
        if (TextUtils.isEmpty(fileName)) fileName = getUrlFileName(url);
        if (TextUtils.isEmpty(fileName)) fileName = "未知文件名";
        return fileName;
    }

    /**
     * 通过 ‘？’ 和 ‘/’ 判断文件名
     */
    public static String getUrlFileName(String url) {
        //先用URL转义一下
        String targetUrl = null;
        try {
            targetUrl = URLDecoder.decode(url, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if (TextUtils.isEmpty(targetUrl))
            targetUrl = url;
        int index = targetUrl.lastIndexOf('?');
        String filename;
        try {
            if (index > 1) {
                filename = targetUrl.substring(targetUrl.lastIndexOf('/') + 1, index);
            } else {
                filename = targetUrl.substring(targetUrl.lastIndexOf('/') + 1);
            }

        } catch (Exception e) {
            filename = "未知文件名";
        }

        return filename;
    }

    /**
     * 解析文件头ContentDisposition的字符串  Content-Disposition:attachment;filename=FileName.txt
     */
    public static String getHeaderFileName(String contentDisposition) {

        if (!TextUtils.isEmpty(contentDisposition)) {
            String split = "filename=";
            int indexOf = contentDisposition.indexOf(split);
            if (indexOf != -1) {
                String fileName = contentDisposition.substring(indexOf + split.length(),
                        contentDisposition.length());
                fileName = fileName.replaceAll("\"", "");   //文件名可能包含双引号,需要去除
                return fileName;
            }
        }
        return null;
    }


    /**
     * 根据相应的类型打开相应的页面
     */
    public static void openPageAccordType(File file, Context pContext) {
        String type = getMIMEType(file);
        try {

            Intent intent = new Intent("android.intent.action.VIEW");
            intent.addCategory("android.intent.category.DEFAULT");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //注意type为apk，7.0的适配
            Uri uri;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) 
            {
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                uri = FileProvider.getUriForFile(pContext,  "com.assassin.webviewdemo.FileProvider", file);
            }else 
            {
                uri = Uri.fromFile(file);
            }
            intent.setDataAndType(uri, type);
            //校验一下intent
            boolean hasIntentExist = pContext.getPackageManager().queryIntentActivities
                    (intent, 0).size() > 0;
            if (!hasIntentExist) {
                Toast.makeText(pContext, "不存在这样的软件界面", Toast.LENGTH_SHORT).show();
                return;
            }
            pContext.startActivity(intent);
        } catch (Throwable e) {
            e.printStackTrace();
            String errorMessage = null;
            switch (type) {
                case "application/pdf":
                    errorMessage = "请安装可以阅读pdf的相关软件再尝试打开~";
                    break;
                case "audio/*":
                    errorMessage = "请安装可以收听音频的相关软件再尝试打开~";
                    break;
                case "video/*":
                    errorMessage = "请安装可以观看视频的相关软件再尝试打开~";
                    break;
                case "image/*":
                    errorMessage = "请安装可以浏览图片的相关软件再尝试打开~";
                    break;
                case "application/vnd.android.package-archive":
                    errorMessage = "apk包错误";
                    break;
                case "application/vnd.ms-powerpoint":
                case "application/vnd.ms-word":
                case "application/vnd.ms-excel":
                    errorMessage = "请安装office的相关软件再尝试打开~";
                    break;
                default:
                    errorMessage = "文件已损坏或者不支持该文件预览";
                    break;
            }
            String filePath = file.getPath();
            Toast.makeText(pContext, "文件已经下载在" + filePath + "\n" + errorMessage, Toast
                    .LENGTH_SHORT).show();
        }
    }

    private static String getMIMEType(File f) {
        String type = "";
        String fName = f.getName();    
      /* 取得扩展名 */
        String end = fName.substring(fName.lastIndexOf(".") + 1, fName.length()).toLowerCase();  
        
      /* 依扩展名的类型决定MimeType */
        if (end.equals("pdf")) {
            type = "application/pdf";//  
        } else if (end.equals("m4a") || end.equals("mp3") || end.equals("mid") ||
                end.equals("xmf") || end.equals("ogg") || end.equals("wav")) {
            type = "audio/*";
        } else if (end.equals("3gp") || end.equals("mp4")) {
            type = "video/*";
        } else if (end.equals("jpg") || end.equals("gif") || end.equals("png") ||
                end.equals("jpeg") || end.equals("bmp")) {
            type = "image/*";
        } else if (end.equals("apk")) {     
        /* android.permission.INSTALL_PACKAGES */
            type = "application/vnd.android.package-archive";
        } else if (end.equals("pptx") || end.equals("ppt")) {
            type = "application/vnd.ms-powerpoint";
        } else if (end.equals("docx") || end.equals("doc")) {
            type = "application/vnd.ms-word";
        } else if (end.equals("xlsx") || end.equals("xls")) {
            type = "application/vnd.ms-excel";
        } else {
//        /*如果无法直接打开，就跳出软件列表给用户选择 */    
            type = "*/*";
        }
        return type;
    }


}

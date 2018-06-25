package com.assassin.webviewlibrary;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.DownloadListener;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.assassin.webviewlibrary.util.DialogUtils;
import com.assassin.webviewlibrary.util.DownloadUtil;
import com.assassin.webviewlibrary.util.Param;
import com.assassin.webviewlibrary.util.WebViewJSInterface;
import com.assassin.webviewlibrary.util.WebViewSetting;
import com.assassin.webviewlibrary.widget.NestedWebViewCompat;
import com.assassin.webviewlibrary.widget.SwipeRefreshWebViewLayout;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;

import java.io.File;
import java.util.List;
import java.util.UUID;

public class ChromeWebViewActivity extends AppCompatActivity {
    
    ImageView imgBack;
    TextView imgClose;
    TextView tvTitle;
   // ImageView imgRefresh;
    ProgressBar progressbar;
    SwipeRefreshWebViewLayout flWebViewContainer;
    LinearLayout llActionBar;

    private NestedWebViewCompat webView;
    private String url ;
    private String title ;
    private boolean isShowTitle;

    //用于js调用系统的相机或者照片页面
    private ValueCallback<Uri> mUploadMessage = null;
    private ValueCallback<Uri[]> filePathCallbackLollipop = null;
    //用于选择照片还是相加
    private PopupWindow mPopupWindow;
    private View mView;
    private String mFileAbsolutePath;

    private Activity mActivity;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mActivity = this;
        getBundle(getIntent().getExtras());
        setContentView(R.layout.activity_chrome_web_view);
        initView();
        bindListener();
        initPop();
        load();
    }

    /**
     * 获取传过来的参数
     */
    private void getBundle(Bundle pBundle)
    {
        if (pBundle==null)
        {
            throw new RuntimeException("参数不合法");
        }

        url = pBundle.getString(Param.INTENT_PARAM.URL);
        title = pBundle.getString(Param.INTENT_PARAM.TITLE);
        isShowTitle = pBundle.getBoolean(Param.INTENT_PARAM.SHOW_TITLE, true);

    }

    private void initView()
    {
        imgBack = findViewById(R.id.imgBack);
        imgClose = findViewById(R.id.imgClose);
        tvTitle = findViewById(R.id.tvTitle);
       // imgRefresh = findViewById(R.id.imgRefresh);
        progressbar = findViewById(R.id.progressbar);
        flWebViewContainer = findViewById(R.id.webViewContainer);
        llActionBar = findViewById(R.id.llActionBar);
        //是否显示标题栏
        llActionBar.setVisibility(isShowTitle?View.VISIBLE:View.GONE);
        //代码生成webView
        //避免webview内存泄露， 不在xml中定义 Webview ，而是在需要的时候在Activity中创建，
        // 并且Context使用 getApplicationgContext()
        SwipeRefreshWebViewLayout.LayoutParams params = new SwipeRefreshWebViewLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        //  webView = new WebView(_mActivity.getApplicationContext());
        webView = new NestedWebViewCompat(getApplicationContext());
        webView.setLayoutParams(params);
        flWebViewContainer.addView(webView);

      //  imgRefresh.setEnabled(false);
        WebViewSetting.webViewDefaultSetting(this, webView);
        webView.addJavascriptInterface(new WebViewJSInterface(this),WebViewJSInterface.NAME);
    }

    private void bindListener() {


        flWebViewContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
        {
            @Override
            public void onRefresh()
            {
                //刷新
                webView.clearCache(true);
                webView.reload();
            }
        });
        
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check if there's history
                if (webView.canGoBack())
                    webView.goBack();
                else {
                    finish();
                }

            }
        });

        imgClose.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });
        webView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                if (webView != null)
                    webView.removeAllViews();
                if (flWebViewContainer!=null)
                {
                    flWebViewContainer.setRefreshing(false);
                }
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request,
                                        WebResourceError error) {
                super.onReceivedError(view, request, error);
                // 加载失败
                //todo 加载本地的一个错误的html(需要webSettings设置为setFile true)
            }

            @Override
            public void onPageFinished(WebView view, String url)
            {

                super.onPageFinished(view, url);
                if (flWebViewContainer!=null)
                {
                    flWebViewContainer.setRefreshing(false);
                }
            }

            //处理https请求
            //webView默认是不处理https请求的，页面显示空白
            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error)
            {
                //   handler.proceed();
            }

            //是否拦截，根据需求进行拦截
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url)
            {
                return false;
            }

        });


        webView.setWebChromeClient(new MyWebChromeClient());

        webView.setDownloadListener(new MyWebViewDownListener());
        
    }

    /**
     * 初始化拍照弹出框
     */
    private void initPop() {

        mView = LayoutInflater.from(this).inflate(R.layout.popupwindow_take_photo_picture, null);

        mPopupWindow = new PopupWindow(this);
        mPopupWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        mPopupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
        mPopupWindow.setFocusable(true);
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setContentView(mView);
        mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                if (mUploadMessage != null) {
                    mUploadMessage.onReceiveValue(null);
                    mUploadMessage = null;
                }
                if (filePathCallbackLollipop != null) {
                    filePathCallbackLollipop.onReceiveValue(null);
                    filePathCallbackLollipop = null;
                }
            }
        });

        mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopupWindow.dismiss();
            }
        });

        Button mBtn_take_camera = (Button) mView.findViewById(R.id.itemCamera);
        Button mBtn_get_photos = (Button) mView.findViewById(R.id.itemPhoto);
        Button mBtn_cancel = (Button) mView.findViewById(R.id.itemCancel);
        mBtn_take_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePhoto();
            }
        });
        mBtn_get_photos.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                selectFile();
            }
        });
        mBtn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    private void load() {

        if (!TextUtils.isEmpty(title))
        {
            tvTitle.setText(title);
        }
        webView.loadUrl(url);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if (webView != null) {
            webView.getSettings().setJavaScriptEnabled(true);
            webView.onResume();
        }
    }

    @Override
    public void onPause()
    {
        if (webView != null) {
            webView.getSettings().setJavaScriptEnabled(false);
            webView.onPause();
        }
        super.onPause();
    }

    //防止内存泄露
    @Override
    public void onDestroy()
    {

        if (webView != null) {
            webView.setWebViewClient(null);
            webView.setWebChromeClient(null);
            webView.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
            webView.clearHistory();
            ((ViewGroup) webView.getParent()).removeView(webView);
            webView.removeAllViews();
            webView.destroy();
            webView = null;
        }
        super.onDestroy();
    }


    public boolean onKeyDown(int keyCode, KeyEvent event)
    {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (webView.canGoBack()) {
                webView.goBack();
                return true;
            } else {
                finish();
            }

        }

        return false;
    }




    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent)
    {
        super.onActivityResult(requestCode, resultCode, intent);

        if (requestCode == Param.REQUEST_CODE.TAKE_PHOTO_RESULTCODE || requestCode == Param.REQUEST_CODE.FILE_CHOOSER)
        {
            if (null == mUploadMessage && null == filePathCallbackLollipop) return;
            Uri result = intent == null || resultCode != RESULT_OK ? null : intent.getData();
            if (filePathCallbackLollipop != null) {
                onActivityResultAboveL(requestCode, intent);
            } else if (mUploadMessage != null) {
                mUploadMessage.onReceiveValue(result);
                mUploadMessage = null;
            }
        }

        dismiss();

        //BiliBili先放着
       /* if (RESULT_OK == resultCode) 
        {
            if (requestCode == BILIBILI_MUIT_PHOTO) {
                ArrayList<BaseMedia> getPhotos = Boxing.getResult(intent);
                if (getPhotos == null || getPhotos.isEmpty()) {
                    ToastUtil.INSTANCE.toastBottom(_mActivity, "选择图片失败，请重新选择~");
                    return;
                }
                //压缩图片
                getCompressPhotos(getPhotos);

            }
        }*/
    }

    //5.0以上版本，由于api不一样，要单独处理
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void onActivityResultAboveL(int requestCode, Intent data)
    {

        if (filePathCallbackLollipop == null) {
            return;
        }
        Uri result = null;
        if (requestCode == Param.REQUEST_CODE.TAKE_PHOTO_RESULTCODE)
        {
            File file = new File(mFileAbsolutePath);
            Uri localUri = Uri.fromFile(file);
            Intent localIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, localUri);
            sendBroadcast(localIntent);
            result = Uri.fromFile(file);

        } else if (requestCode == Param.REQUEST_CODE.FILE_CHOOSER && data != null)
        {
            result = data.getData();
        }


        if (result != null)
        {
            filePathCallbackLollipop.onReceiveValue(new Uri[]{result});
        } else {
            filePathCallbackLollipop.onReceiveValue(null);
        }
        filePathCallbackLollipop = null;

    }


    //打开相机
    private void takePhoto()
    {
        AndPermission.with(this)
                .runtime()
                .permission(Permission.CAMERA)
                .onGranted(new Action<List<String>>()
                {
                    @Override
                    public void onAction(List<String> data)
                    {
                        Toast.makeText(mActivity, data.toString(), Toast.LENGTH_SHORT)
                                .show();
                        //创建一个空文件
                        File file = new File(getExternalFilesDir(null), UUID.randomUUID().toString() + ".png");
                        String authority = getPackageName() + ".FileProvider";
                        Uri imageUri = null;
                        //通过FileProvider创建一个content类型的Uri
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            imageUri = FileProvider.getUriForFile(getApplicationContext(), authority, file);
                        } else {
                            imageUri = Uri.fromFile(file);
                        }
                        mFileAbsolutePath = file.getAbsolutePath();
                        Intent intent = new Intent();
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            //添加这一句表示对目标应用临时授权该Uri所代表的文件
                        }
                        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);//设置Action为拍照

                        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);//将拍取的照片保存到指定URI

                        startActivityForResult(intent, Param.REQUEST_CODE.TAKE_PHOTO_RESULTCODE);
                    }
                })
                .onDenied(new Action<List<String>>() {
                    @Override
                    public void onAction(List<String> data) {
                        Toast.makeText(mActivity, data.toString(), Toast.LENGTH_SHORT).show();
                        DialogUtils.alertRefuseDialog(data, mActivity, false, false);
                    }
                })
                .start();
    }


    //打开文件
    private void selectFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "完成操作需要使用"), Param.REQUEST_CODE.FILE_CHOOSER);
    }



    class MyWebChromeClient extends WebChromeClient {

        //设置加载进度条
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            if (progressbar != null) {
                if (newProgress == 100) {
                    progressbar.setVisibility(View.GONE);
                } else {
                    if (progressbar.getVisibility() == View.GONE)
                        progressbar.setVisibility(View.VISIBLE);
                    progressbar.setProgress(newProgress);
                }

            }

            super.onProgressChanged(view, newProgress);
        }

        //2017/05/19获取网页的标题
        @Override
        public void onReceivedTitle(WebView view, String title) {
            //super.onReceivedTitle(view, title);
            //todo 有这样的需求，可以将标题取出来，赋值到标题栏上
            tvTitle.setText(title);
        }

        //支持javascript的警告框
        @Override
        public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
            //自己加入的弹窗
            DialogUtils.showAlertDialog(mActivity, "", message, true, new DialogUtils
                    .AlertDialogListener() {
                @Override
                public void onPositiveSelect()
                {
                    Toast.makeText(mActivity, "点击了确定按钮", Toast.LENGTH_SHORT).show();
                }
            });
            result.cancel();
            return true;
        }

        /**
         * 覆盖默认的window.confirm展示界面，避免title里显示为“：来自file:////”
         */
        @Override
        public boolean onJsConfirm(WebView view, String url, String message, final JsResult
                result) {

            DialogUtils.showAlertDialog(mActivity, "", message, true, new DialogUtils
                    .AlertDialogListener() {
                @Override
                public void onPositiveSelect() {
                    result.confirm();
                }
            });

            return true;
        }

        //5/19支持javascript输入框
        //点击确认返回输入框中的值，点击取消返回 null。
        @Override
        public boolean onJsPrompt(WebView view, String url, String message, String defaultValue,
                                  final JsPromptResult result) {
            return true;
        }

        /**
         * android中使用WebView来打开本机的文件选择器
         * js上传文件的<input type="file" name="fileField" userId="fileField" />事件捕获
         **/
        public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback,
                                         FileChooserParams fileChooserParams) {
            if (filePathCallbackLollipop != null)
                filePathCallbackLollipop.onReceiveValue(null);
            filePathCallbackLollipop = filePathCallback;
            show();
            return true;
        }


        // Android > 4.1.1 调用这个方法
        public void openFileChooser(ValueCallback<Uri> uploadMsg,
                                    String acceptType, String capture) {
            if (mUploadMessage != null)
                mUploadMessage.onReceiveValue(null);
            mUploadMessage = uploadMsg;
            show();

        }

        // 3.0 + 调用这个方法
        public void openFileChooser(ValueCallback<Uri> uploadMsg,
                                    String acceptType) {
            if (mUploadMessage != null)
                mUploadMessage.onReceiveValue(null);
            mUploadMessage = uploadMsg;
            show();
        }
    }
    //实现监听下载
    class MyWebViewDownListener implements DownloadListener {

        @Override
        public void onDownloadStart(final String url, String userAgent, final String
                contentDisposition, String mimetype, long contentLength)
        {
            //首先校验一下读写权限
            final String fileName = DownloadUtil.getNetFileName(contentDisposition, url);
            DialogUtils.showAlertDialog(mActivity, "注意", "您要下载"+fileName+"这个文件么", true, new DialogUtils.AlertDialogListener()
            {

                @Override
                public void onPositiveSelect()
                {
                    //先判断是否有这个文件
                    File tmpFile= DownloadUtil.getDownloadFile(url, mActivity);
                    if (tmpFile != null && tmpFile.exists() )
                    {
                        //todo 打开文件
                        DownloadUtil.openPageAccordType(tmpFile,mActivity);
                        return;
                    }else
                    {
                        if (DownloadUtil.isDownloading(url, mActivity))
                        {
                            Toast.makeText(mActivity, "正在下载~", Toast.LENGTH_SHORT).show();
                            return;
                        }else
                        {
                            DownloadUtil download = new DownloadUtil(mActivity,
                                    url, "文件的名称", fileName);
                            download.startDownload();
                        }

                    }

                }
            });


        }
    }

    //popupWindow的弹出和关闭
    public void dismiss() {
        if (mPopupWindow != null && mPopupWindow.isShowing()) {
            mPopupWindow.dismiss();
        }
    }

    public void show() {
        if (mPopupWindow != null && mView != null) {
            mPopupWindow.showAtLocation(mView, Gravity.BOTTOM, 0, 0);
        }
    }

}

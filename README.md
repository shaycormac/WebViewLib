# WebViewLib
封装一个简单的WebView
    webviewlibrary库里面有两个封装好的WebviewActivity,只需要传递url连接，标题即可使用，简单粗暴.
WebViewActivity是一个封装好的带有标题栏的webViewActivity,里面已经封装好了与前端JS交互的WebViewJSInterface
按照自己的业务需求，自己模仿写方法和业务即可，上传照片和图片选择的方法也已经封装好，另外，也对下载html中的各种文件进行了封装
，并适配到最新的8.1版本，根据下载文件的不同类型打开默认的应用。
    ChromeWebView是模仿谷歌的Chrome,下拉刷新当前的webview,并添加了手势上划隐藏标题栏，手势下拉，出现标题栏，重写WebView,继承
NestedScrollingChild和NestedScrollingParent这两个接口，解决webview和SwipeRefresh的冲突。同时解决了前端页面把 height 写成了 100% ，安卓端webview的getScrollY一直为0的问题，下拉刷新冲突的问题。
   封装的过程引用了一些原作，在此一一感谢：
   
   WebView.getScrollY 一直是 0  
   https://www.jianshu.com/p/70632591878c
   
   Android WebView与下拉刷新控件滑动冲突的解决方法
   https://blog.csdn.net/allenli0413/article/details/79763123
   
   Android WebView下拉刷新与SwipeRefreshLayout事件冲突解决
   https://blog.csdn.net/ahuyangdong/article/details/77773323
   
   
   后续：添加支持跨进程的webview,将wenview放在一个子进程中，从而减轻主进程的内存使用，降低OOM的可能性。
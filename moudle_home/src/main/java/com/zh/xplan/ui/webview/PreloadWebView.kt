package com.zh.xplan.ui.webview

import android.annotation.SuppressLint
import android.content.Context
import android.content.MutableContextWrapper
import android.net.http.SslError
import android.os.Build
import android.os.Looper
import android.webkit.SslErrorHandler
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import com.module.common.log.LogUtil
import com.zh.xplan.XPlanApplication
import java.util.*

class PreloadWebView private constructor() {
    private val CACHED_WEBVIEW_MAX_NUM = 2
    private val mCachedWebViewStack = Stack<WebView>()


    private val html: String
        get() {
            val builder = StringBuilder()
            builder.append("<!DOCTYPE html>\n")
            builder.append("<html>\n")
            builder.append("<head>\n")
            builder.append("<meta charset=\"utf-8\">\n")
            builder.append("<meta name=\"viewport\" content=\"initial-scale=1.0, minimum-scale=1.0, maximum-scale=1.0, user-scalable=no\">\n")
            builder.append("<link rel=\"stylesheet\" type=\"text/css\" href=\"")
            builder.append("file:///android_asset/article/css/android.css")
            builder.append("\">\n</head>\n")
            builder.append("<body class=\"font_m\"><header></header><article></article><footer></footer>")
            builder.append("<script type=\"text/javascript\" src=\"")
            builder.append("file:///android_asset/article/js/lib.js")
            builder.append("\"></script>")
            builder.append("<script type=\"text/javascript\" src=\"")
            builder.append("file:///android_asset/article/js/android.js")
            builder.append("\" ></script>\n")
            builder.append("</body>\n")
            builder.append("</html>\n")
            return builder.toString()
        }

    private object Holder {
         val INSTANCE = PreloadWebView()
    }

    /**
     * 创建WebView实例
     * 用了applicationContext
     */
    fun preload() {
        LogUtil.e("webview preload")
        Looper.myQueue().addIdleHandler {
            if (mCachedWebViewStack.size < CACHED_WEBVIEW_MAX_NUM) {

                mCachedWebViewStack.push(createWebView())
            }
            false
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun createWebView(): WebView {
        val context = XPlanApplication.instance
        val webView = WebView(MutableContextWrapper(context))
        //远程代码执行漏洞
        webView.removeJavascriptInterface("accessibility")
        webView.removeJavascriptInterface("searchBoxJavaBridge_")
        webView.removeJavascriptInterface("accessibilityTraversal")
        val webSettings = webView.settings
        //域控制不严格漏洞 禁用 file 协议；
        webSettings.allowFileAccess = false
        webSettings.allowFileAccessFromFileURLs = false
        webSettings.allowUniversalAccessFromFileURLs = false
        //JS交互
        webSettings.javaScriptEnabled = true
        //dom
        webSettings.domStorageEnabled = true
        webSettings.databaseEnabled = true
        //自适应
        webSettings.useWideViewPort = true
        webSettings.loadWithOverviewMode = true
        //允许加载图片
        webSettings.blockNetworkImage = false
        webSettings.javaScriptCanOpenWindowsAutomatically = true
        webSettings.loadsImagesAutomatically = true
        //缓存
        webSettings.setAppCacheEnabled(true)
        val appCachePath = XPlanApplication.instance.cacheDir
                .absolutePath
        webSettings.setAppCachePath(appCachePath)
        webSettings.allowFileAccess = true
        webSettings.cacheMode = WebSettings.LOAD_DEFAULT
        //允许http和https混合
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webSettings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        }
        // WebView默认使用系统默认浏览器打开网页的行为，覆盖这个方法使网页用自己的WebView打开
        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                // 返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
                view.loadUrl(url)
                return true
            }

            override fun onReceivedSslError(view: WebView, handler: SslErrorHandler, error: SslError) {
                //忽略证书校验
                handler.proceed()
            }
        }
        return webView
    }

    /**
     * 从缓存池中获取合适的WebView
     *
     * @param context activity context
     * @return WebView
     */
    fun getWebView(context: Context): WebView {
        var webView: WebView = if (mCachedWebViewStack.isEmpty()) {
            // 为空，直接返回新实例
            LogUtil.e("webview createWebView ")
            createWebView()
        } else {
            // webView不为空，则开始使用预创建的WebView,
            LogUtil.e("webview mCachedWebViewStack.pop")
            mCachedWebViewStack.pop()
        }
        //并且替换Context
        val contextWrapper = webView.context as MutableContextWrapper
        contextWrapper.baseContext = context
        return webView
    }

    companion object {
        public val instance: PreloadWebView = Holder.INSTANCE
    }

}

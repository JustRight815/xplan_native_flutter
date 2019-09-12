package com.zh.xplan.ui.webview

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.webkit.WebSettings
import android.widget.LinearLayout
import android.widget.RelativeLayout
import com.module.common.utils.PixelUtil
import com.tencent.smtt.sdk.WebView
import com.zh.xplan.R
import com.zh.xplan.XPlanApplication
import com.zh.xplan.ui.base.BaseActivity
import kotlinx.android.synthetic.main.activity_weather_details.*

/**
 * 腾讯 X5 演示实例
 * X5 webview 控件默认有个细长的滚动条，但是当快速滑动的时候，滚动条就变成一个小方块。
 * ScrollView 嵌套X5 webview  小方块不显示，滚动条显示为ScrollView样式
 * @author zh
 */
class X5WebViewActivity : BaseActivity() {
    private var mURL: String? = null
    private var mTitle: String? = "" //页面标题
    private var mWebView: WebView? = null
    private var currentProgress = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weather_details)
        setStatusBarColor(resources.getColor(R.color.colorPrimaryDark), 0)
        initView()
        initData()
        loadURL()
    }

    private fun initView() {
        title_bar_back.setOnClickListener{
            mWebView?:finish()
            mWebView?.let {
                if (!it.canGoBack()) {
                    finish()
                } else {
                    it.goBack()
                }
            }
        }
        title_bar_share.setOnClickListener{
            mWebView?.let {
                val curUrl= it.url
                val sendIntent = Intent()
                sendIntent.action = Intent.ACTION_SEND
                sendIntent.putExtra(Intent.EXTRA_TEXT, "$mTitle:$curUrl")
                sendIntent.type = "text/plain"
                startActivity(sendIntent)
            }
        }
        title_bar_colse.setOnClickListener{
            finish()
        }

        ProgressBar.layoutParams = RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT, PixelUtil.dp2px(2f, this))

        //防止内存泄露
        val webViewLayout = findViewById<View>(R.id.ll_webview_layout) as LinearLayout
        val params = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        mWebView = WebView(applicationContext)
        mWebView?.let {
            it.layoutParams = params
            webViewLayout.addView(it)
            initWebView(it)
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initWebView(webView: WebView) {
        //远程代码执行漏洞
        webView.removeJavascriptInterface("accessibility")
        webView.removeJavascriptInterface("searchBoxJavaBridge_")
        webView.removeJavascriptInterface("accessibilityTraversal")

        val webSettings = webView.settings
        //下面方法去掉滚动条
//        webView.setHorizontalScrollBarEnabled(false)
//        webView.setVerticalScrollBarEnabled(false)
//        val ix5 = webView.getX5WebViewExtension()
//        if (null != ix5) {
//            ix5!!.setScrollBarFadingEnabled(false)
//        }
        //密码明文存储漏洞 关闭密码保存提醒
        webSettings.savePassword = false
        //域控制不严格漏洞 禁用 file 协议；
        webSettings.allowFileAccess = false
        webSettings.setAllowFileAccessFromFileURLs(false)
        webSettings.setAllowUniversalAccessFromFileURLs(false)
        //JS交互
        webSettings.javaScriptEnabled = true
        //dom
        webSettings.domStorageEnabled = true
        //自适应
        webSettings.useWideViewPort = true
        webSettings.loadWithOverviewMode = true
        //缓存
        val appCachePath = XPlanApplication.getInstance().cacheDir
                .absolutePath
        webSettings.setAppCachePath(appCachePath)
        webSettings.allowFileAccess = true
        webSettings.setAppCacheEnabled(true)
        webSettings.cacheMode = com.tencent.smtt.sdk.WebSettings.LOAD_DEFAULT
        //允许加载图片
        webSettings.blockNetworkImage = false
        webSettings.javaScriptCanOpenWindowsAutomatically = true
        webSettings.loadsImagesAutomatically = true
        //允许http和https混合
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webSettings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        }

        webView.webChromeClient = object : com.tencent.smtt.sdk.WebChromeClient() {
            // 监听网页进度
            override fun onProgressChanged(view: com.tencent.smtt.sdk.WebView?, newProgress: Int) {
                if (newProgress > currentProgress) {
                    currentProgress = newProgress
                    ProgressBar.progress = newProgress
                }
                if (newProgress == 100) {
                    val h = Handler()
                    h.postDelayed({
                        ProgressBar.visibility = View.GONE
                        currentProgress = 0
                    }, 500)
                } else {
                    if (ProgressBar.visibility == View.GONE) {
                        ProgressBar.visibility = View.VISIBLE
                    }
                }
                super.onProgressChanged(view, newProgress)
            }

            override fun onReceivedTitle(view: WebView?, title: String?) {
                super.onReceivedTitle(view, title)
                //获取到网页标题
                mTitle = title
                title_name!!.text = mTitle
            }
        }

        // WebView默认使用系统默认浏览器打开网页的行为，覆盖这个方法使网页用自己的WebView打开
        webView.webViewClient = object : com.tencent.smtt.sdk.WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                // 返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
                view?.loadUrl(url)
                return true
            }

            override fun onPageStarted(webView: WebView, s: String, bitmap: Bitmap) {
                if (webView.canGoBack()) {
                    pageNavigator(View.VISIBLE)
                } else {
                    pageNavigator(View.GONE)
                }
                super.onPageStarted(webView, s, bitmap)
            }
        }
    }

    private fun pageNavigator(tag: Int) {
        title_bar_back_layout!!.visibility = tag
    }

    private fun initData() {
        val intent = intent
        mURL = intent.getStringExtra("URL")
    }

    private fun loadURL() {
        mWebView?.let {
            if (mURL != null) {
                it.loadUrl(mURL)
            }
        }
    }


    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        mWebView?.let {
            if (keyCode == KeyEvent.KEYCODE_BACK && it.canGoBack()) {
                it.goBack()
                return true
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onDestroy() {
        mWebView?.let {
            it.loadDataWithBaseURL(null, "", "text/html", "utf-8", null)
            it.clearHistory()
            // 如果先调用destroy()方法，则会命中if (isDestroyed()) return;这一行代码，需要先onDetachedFromWindow()，再destory()
            val parent = it.parent
            if (parent != null) {
                (parent as ViewGroup).removeView(it)
            }
            it.stopLoading()
            // 退出时调用此方法，移除绑定的服务，否则某些特定系统会报错
            it.settings.javaScriptEnabled = false
            it.removeAllViews()
            it.destroy()
            mWebView = null
        }
        super.onDestroy()
    }
}

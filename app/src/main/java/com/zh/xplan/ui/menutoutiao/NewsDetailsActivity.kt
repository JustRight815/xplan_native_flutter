package com.zh.xplan.ui.menutoutiao

import android.annotation.SuppressLint
import android.content.Intent
import android.net.http.SslError
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import android.webkit.SslErrorHandler
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.module.common.log.LogUtil
import com.module.common.net.rx.NetManager
import com.module.common.view.snackbar.SnackbarUtils
import com.zh.xplan.R
import com.zh.xplan.ui.base.BaseActivity
import com.zh.xplan.ui.menutoutiao.model.NewsDetail
import com.zh.xplan.ui.menutoutiao.model.response.ResultResponse
import com.zh.xplan.ui.webview.PreloadWebView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_news_details.*

/**
 * X5 webview 演示实例 仿今日头条新闻详情  *ScollView 嵌套 WebView 底部空白  https://blog.csdn.net/self_study/article/details/54378978
 * X5 webview 控件默认有个细长的滚动条，但是当快速滑动的时候，滚动条就变成一个小方块。
 * ScrollView 嵌套X5 webview  小方块不显示，滚动条显示为ScrollView样式
 * @author zh
 */
class NewsDetailsActivity : BaseActivity(), View.OnClickListener {
    private var mContentView: View? = null
    private var mWebView: WebView? = null
    private val mTitle = "" //页面标题
    private var mRecyclerView: RecyclerView? = null

    private var mDetalUrl: String? = null
    private var mChannelCode: String? = null
    private var mPosition: Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mContentView = View.inflate(this, R.layout.activity_news_details, null)
        setContentView(mContentView)
        setStatusBarColor(resources.getColor(R.color.colorPrimaryDark), 0)
        initView()
        initData()
    }

    private fun initView() {
        findViewById<View>(R.id.title_bar_back).setOnClickListener(this)
        findViewById<View>(R.id.title_bar_share).setOnClickListener(this)
        findViewById<View>(R.id.title_bar_colse).setOnClickListener(this)
        //防止内存泄露
        val params = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        mWebView = PreloadWebView.instance.getWebView(this)
        mWebView?.let {
            it.layoutParams = params
            ll_webview_layout?.addView(it)
            mRecyclerView = findViewById<View>(R.id.recyclerView) as RecyclerView
            val layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
            layoutManager.isSmoothScrollbarEnabled = true
            mRecyclerView?.layoutManager = layoutManager
            initComment()
        }
    }

    private fun pageNavigator(tag: Int) {
        title_bar_back_layout!!.visibility = tag
    }

    private fun initData() {
        val intent = intent
        mChannelCode = intent.getStringExtra(CHANNEL_CODE)
        mPosition = intent.getIntExtra(POSITION, 0)
        mDetalUrl = intent.getStringExtra(DETAIL_URL)
        getNewsDetail(mDetalUrl)
    }

    @SuppressLint("CheckResult")
    fun getNewsDetail(url: String?) {
        LogUtil.e("zh", "getNewsDetail :url: " + url!!)
        NetManager.get()
                .url(url)
                .build()!!
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableObserver<String>() {
                    override fun onNext(response: String) {
                        LogUtil.e("zh", "getNewsDetail :onSuccess response: $response")
                        if (response.isEmpty()) {
                            return
                        }
                        val response1 = Gson().fromJson<ResultResponse<NewsDetail>>(response, object : TypeToken<ResultResponse<NewsDetail>>() {

                        }.type)
                        val newsDetail = response1.data
                        LogUtil.e("zh", "getNewsDetail :onSuccess newsDetail: $newsDetail")
                        tvTitle?.text = newsDetail.title
                        LogUtil.e("zh", "getNewsDetail :onSuccess newsDetail.title: " + newsDetail.title)
                        if (newsDetail.media_user == null) {
                            //如果没有用户信息
                            LogUtil.e("zh", "getNewsDetail :如果没有用户信息 ")
                        } else {
                            if (!TextUtils.isEmpty(newsDetail.media_user.avatar_url)) {
                                //								GlideUtils.loadRound(mContext, detail.media_user.avatar_url, mIvAvatar);
                            }
                        }

                        if (TextUtils.isEmpty(newsDetail.content)) {
                            mWebView?.visibility = View.GONE
                        }
                        LogUtil.e("zh", "getNewsDetail :newsDetail.content " + newsDetail.content)

                        mWebView?.addJavascriptInterface(ShowPicRelation(this@NewsDetailsActivity), "chaychan")//绑定JS和Java的联系类，以及使用到的昵称

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
                        builder.append("<style>\n")
                        builder.append("img{width:100%!important;height:auto!important}\n")
                        builder.append("</style>\n")
                        builder.append(newsDetail.content)
                        builder.append("</body>\n")
                        builder.append("</html>\n")


                        //						String htmlPart1 = "<!DOCTYPE HTML html>\n" +
                        //								"<head><meta charset=\"utf-8\"/>\n" +
                        //								"<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0, minimum-scale=1.0, user-scalable=no\"/>\n" +
                        //								"</head>\n" +
                        //								"<body>\n" +
                        //								"<style> \n" +
                        //								"img{width:100%!important;height:auto!important}\n" +
                        //								" </style>";
                        //						String htmlPart2 = "</body></html>";
                        //						String html = htmlPart1 + newsDetail.content + htmlPart2;
                        val html = builder.toString()
                        mWebView?.webViewClient = object : WebViewClient() {
                            override fun onPageFinished(view: WebView, url: String) {
                                addJs(view)//添加多JS代码，为图片绑定点击函数
                                loading_view?.visibility = View.GONE
                            }

                            override fun onReceivedSslError(view: WebView, handler: SslErrorHandler, error: SslError) {
                                //忽略证书校验
                                handler.proceed()
                            }
                        }
                        mWebView?.loadDataWithBaseURL(null, html, "text/html", "UTF-8", null)
                    }

                    override fun onError(e: Throwable) {
                        LogUtil.e("zh", ":::onFailure response: $e")
                        SnackbarUtils.ShortToast(mContentView, "数据请求失败")
                    }

                    override fun onComplete() {

                    }
                })
    }

    /**
     * 模拟评论数据
     */
    private fun initComment() {
        val adapter = ScrollviewAdapter()
        mRecyclerView?.adapter = adapter
    }


    /**添加JS代码，获取所有图片的链接以及为图片设置点击事件 */
    private fun addJs(wv: WebView) {
        wv.loadUrl("javascript:(function  pic(){" +
                "var imgList = \"\";" +
                "var imgs = document.getElementsByTagName(\"img\");" +
                "for(var i=0;i<imgs.length;i++){" +
                "var img = imgs[i];" +
                "imgList = imgList + img.src +\";\";" +
                "img.onclick = function(){" +
                "window.chaychan.openImg(this.src);" +
                "}" +
                "}" +
                "window.chaychan.getImgArray(imgList);" +
                "})()")
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.title_bar_back -> finish()
            R.id.title_bar_colse -> finish()
            R.id.title_bar_share -> {
                val curUrl = mDetalUrl
                val sendIntent = Intent()
                sendIntent.action = Intent.ACTION_SEND
                sendIntent.putExtra(Intent.EXTRA_TEXT, "$mTitle:$curUrl")
                sendIntent.type = "text/plain"
                startActivity(sendIntent)
            }
            else -> {
            }
        }
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
        PreloadWebView.instance.preload()
        super.onDestroy()
    }

    companion object {
        val CHANNEL_CODE = "channelCode"
        val POSITION = "position"
        val DETAIL_URL = "detailUrl"
        val GROUP_ID = "groupId"
        val ITEM_ID = "itemId"
    }
}

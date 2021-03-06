package com.zh.xplan.ui.menusetting

import android.Manifest
import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.module.common.dialog.BaseDialog
import com.module.common.log.LogUtil
import com.module.common.net.callback.IDownLoadCallback
import com.module.common.net.rx.NetManager
import com.module.common.pay.alipay.Alipay
import com.module.common.pay.weixin.WXPay
import com.module.common.sharesdk.ShareSDKManager
import com.module.common.view.snackbar.SnackbarUtils
import com.zh.xplan.R
import com.zh.xplan.XPlanApplication
import com.zh.xplan.common.utils.InstallApkUtil
import com.zh.xplan.common.utils.TitleUtil
import com.zh.xplan.ui.aboutapp.AboutAppActivity
import com.zh.xplan.ui.base.BaseFragment
import com.zh.xplan.ui.camera.RecordVideoSet
import com.zh.xplan.ui.camera.record.CustomCameraActivity
import com.zh.xplan.ui.flutter.MyFlutterActivity
import com.zh.xplan.ui.iptoolsactivity.IpToolsActivity
import com.zh.xplan.ui.logisticsdetail.LogisticsDetailActivity
import com.zh.xplan.ui.robot.RobotKotlinActivity
import com.zh.xplan.ui.view.addialog.AdDialog
import com.zh.xplan.ui.weather.model.WeatherBeseModel
import com.zh.xplan.ui.webview.NativeWebViewActivity
import com.zh.xplan.ui.webview.X5WebViewActivity
import kotlinx.android.synthetic.main.fragment_setting.*
import org.qcode.qskinloader.SkinManager
import permissions.dispatcher.NeedsPermission
import permissions.dispatcher.OnNeverAskAgain
import permissions.dispatcher.OnPermissionDenied
import permissions.dispatcher.RuntimePermissions
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException

/**
 * 第四个菜单 设置菜单
 * Created by zh
 */
@RuntimePermissions
class SettingFragment : BaseFragment(), OnClickListener, SettingFragmentView {
    private var mContentView: View? = null
    private var mProgressDialog: ProgressDialog? = null//清理缓存时的对话框
    private var bitmap: Bitmap? = null
    private var tempFile: File? = null
    private var resultBean: WeatherBeseModel.WeatherBean? = null
    private var presenter: SettingFragmentPresenter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        mContentView = View.inflate(activity, R.layout.fragment_setting, null)
        initTitle(activity, mContentView)
        return mContentView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        SkinManager.getInstance().applySkin(view, true)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initView()
        initData()
    }

    /**
     * 每次进入设置页面都更新下缓存的大小
     */
    override fun onResume() {
        super.onResume()
        presenter?.apply {
            getCityWeather("", "")
            getCacheSize()
        }
    }

    /**
     * 每次进入设置页面都更新下缓存的大小
     */
    override fun onHiddenChanged(hidden: Boolean) {
        if (!hidden) {
            presenter?.getCacheSize()
        }
        super.onHiddenChanged(hidden)
    }

    /**
     * 初始化标题栏
     * @param activity
     * @param view
     */
    private fun initTitle(activity: Activity?, view: View?) {
        // 1.设置左边的图片按钮显示，以及事件 2.设置中间TextView显示的文字 3.设置右边的图片按钮显示，并设置事件
        TitleUtil(activity, view!!).setMiddleTitleText("设置")
    }

    /**
     * 初始化界面
     */
    private fun initView() {
        ll_pay.setOnClickListener(this)
        ll_camera.setOnClickListener(this)
        ll_flutter.setOnClickListener(this)
        ll_logistics.setOnClickListener(this)
        ll_x5_webview.setOnClickListener(this)
        ll_native_webview.setOnClickListener(this)
        iv_head_picture.setOnClickListener(this)
        ll_share.setOnClickListener(this)
        ll_chack_version.setOnClickListener(this)
        ll_clear.setOnClickListener(this)
        ll_about.setOnClickListener(this)
        ll_go_market.setOnClickListener(this)
        ll_robot.setOnClickListener(this)
        ll_ip_tools.setOnClickListener(this)
        ll_ad_dialog.setOnClickListener(this)
        activity?.let {
            val bt = getHead(HEAD_PATH)
            Glide.with(this).load(bt)
                    .apply(RequestOptions.bitmapTransform(CircleCrop())
                            .placeholder(R.drawable.head_default)
                            .error(R.drawable.head_default))
                    .into(iv_head_picture)
            try {
                val pm = it.packageManager
                val pi: PackageInfo
                pi = pm.getPackageInfo(it.packageName, 0)
                tv_current_version.text = "当前版本:" + pi.versionName
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun initData() {
        presenter = SettingFragmentPresenter()
        presenter?.apply {
            attachView(this@SettingFragment)
            getCityWeather("", "北京")
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.iv_head_picture ->
                //编辑头像
                changeHeadPicture()
            R.id.ll_share ->
                //分享软件
                share()
            R.id.ll_chack_version ->
                //检查新版本
                chackVersion()
            R.id.ll_clear ->
                //清除缓存
                clearHistory()
            R.id.ll_about ->
                //关于软件
                startActivity(Intent(activity, AboutAppActivity::class.java))
            R.id.ll_go_market ->
                //市场评分
                goToMarket()
            R.id.ll_pay ->
                //支付工具
                goToPay()
            R.id.ll_robot ->
                //小机器人
                startActivity(Intent(activity, RobotKotlinActivity::class.java))
            R.id.ll_ip_tools ->
                //IP工具
                startActivity(Intent(activity, IpToolsActivity::class.java))
            R.id.ll_ad_dialog ->
                //弹窗广告
                showAdDialog()
            R.id.ll_camera ->
                //拍照、录视频
                customRecord(true)
            R.id.ll_flutter ->
                //Flutter版本
                startActivity(Intent(activity, MyFlutterActivity::class.java))
            R.id.ll_logistics ->
                //物流详情
                startActivity(Intent(activity, LogisticsDetailActivity::class.java))
            R.id.ll_x5_webview -> {
                //x5_webview
                val intent = Intent(activity,
                        X5WebViewActivity::class.java)
                intent.putExtra("URL", "https://www.baidu.com")
                startActivity(intent)
            }
            R.id.ll_native_webview -> {
                //原生webview
                val nativeWebviewIntent = Intent(activity,
                        NativeWebViewActivity::class.java)
                nativeWebviewIntent.putExtra("URL", "https://www.baidu.com")
                startActivity(nativeWebviewIntent)
            }
            else -> {
            }
        }
    }

    /**
     * 启用自定义相机录制视频
     * @param isSmallVideo
     */
    private fun customRecord(isSmallVideo: Boolean) {
        val recordVideoSet = RecordVideoSet()
        recordVideoSet.limitRecordTime = 30
        recordVideoSet.isSmallVideo = isSmallVideo
        val intent = Intent(activity, CustomCameraActivity::class.java)
        intent.putExtra("RECORD_VIDEO_CONFIG", recordVideoSet)
        startActivity(intent)
    }

    /**
     * 去应用市场评分
     */
    private fun goToMarket() {
        activity?.let { activity ->
            if (!isMarketInstalled(activity)) {
                SnackbarUtils.ShortToast(mContentView, "您的手机没有安装应用市场")
                return
            }
            try {
                //Uri uri = Uri.parse("market://details?id="+getPackageName());
                val uri = Uri.parse("market://details?id=" + "com.tencent.mobileqq")
                val intent = Intent(Intent.ACTION_VIEW, uri)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                if (intent.resolveActivity(activity.packageManager) != null) {
                    startActivity(intent)
                }
            } catch (e: Exception) {
                // 该功能部分手机可能有问题，待验证。详情参见http://blog.csdn.net/wangfayinn/article/details/10351655
                // 也可以调到某个网页应用市场
                SnackbarUtils.ShortToast(mContentView, "您的手机没有安装应用市场")
            }
        }
    }

    /**
     * 本手机是否安装了应用市场
     * @param context
     * @return
     */
    private fun isMarketInstalled(context: Context): Boolean {
        val intent = Intent()
        intent.data = Uri.parse("market://details?id=android.browser")
        val list = context.packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
        return 0 != list.size
    }

    /**
     * 替换头像对话框
     */
    private fun changeHeadPicture() {
        activity?.let { activity ->
            val view = View.inflate(activity, R.layout.dialog_change_head_picture, null)
            //要用 android.support.v7.app.AlertDialog 并且设置主题
            val dialog = AlertDialog.Builder(activity)
                    .setTitle("更换头像")
                    .setView(view)
                    .create()
            dialog.show()
            dialog.window?.let { window ->
                val params = window.attributes
                params.width = activity.windowManager.defaultDisplay.width * 5 / 6
                //	params.height = 200 ;
                window.attributes = params
                view.findViewById<View>(R.id.ll_from_camera).setOnClickListener {
                    // 从相机截取头像
                    cameraWithPermissionCheck()
                    dialog.dismiss()
                }
                view.findViewById<View>(R.id.ll_from_gallery).setOnClickListener {
                    // 从图库截取头像
                    gallery()
                    dialog.dismiss()
                }
            }
        }
    }

    /**
     * 手动检测更新版本
     */
    private fun chackVersion() {
        activity?.let { activity ->
            BaseDialog
                    .DefaultBuilder(activity)
                    .setDefaultTitle("发现新版本")
                    .setContextText("1.测试下载 \n2.测试下载带进度 \n3.测试下载带进度")
                    .setNegativeButton("取消", null)
                    .setPositiveButton("确定") {
                        it?.dismiss()
                        getApkWithPermissionCheck()
                    }
                    .builder()
                    .show()
        }
    }

    @NeedsPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
    internal fun getApk() {
        //		View view = LayoutInflater.from(getActivity()).inflate(R.layout.update_app_progress,null);
        val view = View.inflate(activity,
                R.layout.update_app_progress, null)
        val tv_Progress = view.findViewById<View>(R.id.tv_progress) as TextView
        val title = view.findViewById<View>(R.id.title) as TextView
        val progressBar = view.findViewById<View>(R.id.progressbar) as ProgressBar
        progressBar.max = 100
        progressBar.progress = 0
        tv_Progress.text = "0%"
        title.text = "正在下载"
        val dialog = AlertDialog.Builder(activity!!)
                .setView(view)
                .setNegativeButton("取消", null)
                .setPositiveButton("重试") { arg0, arg1 -> }
                .create()
        dialog.show()
        dialog.setCanceledOnTouchOutside(false)
        val params = dialog.window!!.attributes
        params.width = activity!!.windowManager.defaultDisplay.width * 5 / 6
        //	params.height = 200 ;
        dialog.window!!.attributes = params
        val negativeButton = dialog.getButton(DialogInterface.BUTTON_NEGATIVE)
        val positiveButton = dialog.getButton(DialogInterface.BUTTON_POSITIVE)
        negativeButton.visibility = View.GONE
        positiveButton.visibility = View.GONE
        LogUtil.e("zh", "getApk , ")
        //		String url = "https://raw.githubusercontent.com/JustRight815/XPlan/master/apk/app-debug.apk";
        //		String url = "http://cn.bing.com/az/hprichbg/rb/TartumaaEstonia_ZH-CN13968964399_720x1280.jpg";
        val url = "http://app.2345.cn/appsoft/80202.apk"
        val callback = object : IDownLoadCallback() {

            override fun onStart(totalBytes: Long) {
                super.onStart(totalBytes)
                LogUtil.e("zh", "onStart , $totalBytes")
            }

            override fun onCancel() {
                super.onCancel()
                LogUtil.e("zh", "onCancel , ")
                title.text = "已取消下载"
            }

            override fun onFinish(downloadFile: File) {
                LogUtil.e("zh", "onFinish , " + downloadFile.path)
                title.text = "下载成功"
                progressBar.progress = 100
                tv_Progress.text = "" + 100 + "%"
                dialog.dismiss()
                activity?.let {
                    InstallApkUtil.autoInstallApk(it,downloadFile)
                }
            }

            override fun onProgress(currentBytes: Long, totalBytes: Long) {
//                LogUtil.e("zh", "doDownload onProgress:$currentBytes/$totalBytes")
                var currentProgress = (currentBytes.toFloat() / totalBytes * 100).toInt()
//                LogUtil.e("zh", "doDownload currentProgress:$currentProgress")
                if (currentProgress > 100) {
                    currentProgress = 100
                }
                title.text = "正在下载"
                progressBar.progress = currentProgress
                tv_Progress.text = "$currentProgress%"
            }

            override fun onFailure(error_msg: String) {
                LogUtil.e("zh", "onFailure , $error_msg")
                title.text = "下载失败！"
                negativeButton.visibility = View.VISIBLE
                //						positiveButton.setVisibility(View.VISIBLE);
            }
        }
        val disposableObserver = NetManager.download(url, null,
                Environment.getExternalStorageDirectory().path + "/xplan/", null,
                getDefaultDownLoadFileName(url),
                callback
        )
        negativeButton.setOnClickListener {
            LogUtil.e("zh", "setOnClickListener setOnClickListener , ")
            if (dialog != null && dialog.isShowing) {
                dialog.dismiss()
            }
            if (disposableObserver != null) {
                LogUtil.e("zh", "disposableObserver dispose , ")
                disposableObserver.dispose()
            }
        }

        //		//下载新的图片
        //		HttpManager.download()
        //				.url(url)
        //				.dir(Environment.getExternalStorageDirectory().getPath() + "/xplan/")
        //				.name(getDefaultDownLoadFileName(url))
        //				.enqueue(callback);
    }

    @OnNeverAskAgain(Manifest.permission.READ_EXTERNAL_STORAGE)
    internal fun onNeverAskAgain() {
        AlertDialog.Builder(activity!!)
                .setPositiveButton("好的") { dialog, which ->
                    //打开系统设置权限
                    val intent = getAppDetailSettingIntent(activity)
                    startActivity(intent)
                    dialog.cancel()
                }
                .setNegativeButton("取消") { dialog, which -> dialog.cancel() }
                .setCancelable(false)
                .setMessage("您已经禁止了存储权限,是否去开启权限")
                .show()
    }

    @OnPermissionDenied(Manifest.permission.READ_EXTERNAL_STORAGE)
    internal fun onPermissionDenied() {
        SnackbarUtils.ShortToast(mContentView, "拒绝存储权限将无法升级")
    }


    /**
     * share SDk 分享
     */
    private fun share() {
        ShareSDKManager.show(activity)
    }

    /**
     * 清除历史记录
     */
    private fun clearHistory() {
        presenter?.let {
            mProgressDialog = ProgressDialog.show(activity, null, "清除记录中...",
                    true, false)
            it.clearCache()
        }
    }

    /*
	 * 从相册获取
	 */
    private fun gallery() {
        // 激活系统图库，选择一张图片
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, PHOTO_REQUEST_GALLERY)
    }

    /*
	 * 从相机获取
	 */
    @NeedsPermission(Manifest.permission.CAMERA)
    fun camera() {
        val intent = Intent("android.media.action.IMAGE_CAPTURE")
        intent.addCategory("android.intent.category.DEFAULT")
        val file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), PHOTO_FILE_NAME)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            activity?.let {
                LogUtil.e("zh","url " + FileProvider.getUriForFile(it, "${XPlanApplication.instance.packageName}.fileProvider", file))
                intent.putExtra(MediaStore.EXTRA_OUTPUT,FileProvider.getUriForFile(it, "${XPlanApplication.instance.packageName}.fileProvider", file))
            }
        }else{
            intent.putExtra(MediaStore.EXTRA_OUTPUT,Uri.fromFile(file))
        }
        startActivityForResult(intent, PHOTO_REQUEST_CAMERA)
    }

    @OnPermissionDenied(Manifest.permission.CAMERA)
    internal fun showRecordDenied() {
        SnackbarUtils.ShortToast(mContentView, "拒绝相机权限将无法从相机获取头像")
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        onRequestPermissionsResult(requestCode, grantResults)
    }

    @OnNeverAskAgain(Manifest.permission.CAMERA)
    internal fun onRecordNeverAskAgain() {
        AlertDialog.Builder(activity!!)
                .setPositiveButton("好的") { dialog, which ->
                    //打开系统设置权限
                    val intent = getAppDetailSettingIntent(activity)
                    startActivity(intent)
                    dialog.cancel()
                }
                .setNegativeButton("取消") { dialog, which -> dialog.cancel() }
                .setCancelable(false)
                .setMessage("您已经禁止了相机权限,是否去开启权限")
                .show()
    }

    /**
     * 获取应用详情页面intent
     *
     * @return
     */
    private fun getAppDetailSettingIntent(context: Context?): Intent {
        val localIntent = Intent()
        localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        if (Build.VERSION.SDK_INT >= 9) {
            localIntent.action = "android.settings.APPLICATION_DETAILS_SETTINGS"
            localIntent.data = Uri.fromParts("package", context!!.packageName, null)
        } else if (Build.VERSION.SDK_INT <= 8) {
            localIntent.action = Intent.ACTION_VIEW
            localIntent.setClassName("com.android.settings", "com.android.settings.InstalledAppDetails")
            localIntent.putExtra("com.android.settings.ApplicationPkgName", context!!.packageName)
        }
        return localIntent
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), PHOTO_FILE_NAME)
        if (requestCode == PHOTO_REQUEST_GALLERY) {
            if (data != null) {
                // 得到图片的全路径
                val uri = data.data
                crop(uri,file)
            }
        } else if (requestCode == PHOTO_REQUEST_CAMERA) {
            if (resultCode == Activity.RESULT_OK) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    activity?.let {
                        crop(FileProvider.getUriForFile(it, "${XPlanApplication.instance.packageName}.fileProvider", file),file)
                    }
                }else{
                    crop(Uri.fromFile(file),file)
                }
            }
        } else if (requestCode == PHOTO_REQUEST_CUT) {
            try {
                activity?.let {
                    bitmap = BitmapFactory.decodeStream(it.contentResolver.openInputStream(Uri.fromFile(file)))
                }
                bitmap?.let {
                    /**
                     * 上传服务器代码
                     */
                    setPicToView(it)// 保存在SD卡中
                    iv_head_picture.setImageBitmap(it)// 用ImageView显示出来
                    Glide.with(this).load(it)
                            .apply(RequestOptions.bitmapTransform(CircleCrop())
                                    .placeholder(R.drawable.head_default)
                                    .error(R.drawable.head_default))
                            .into(iv_head_picture)
                }
                val delete = tempFile?.delete()
                println("delete = $delete")
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }

        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun setPicToView(mBitmap: Bitmap) {
        val sdStatus = Environment.getExternalStorageState()
        if (sdStatus != Environment.MEDIA_MOUNTED) { // 检测sd是否可用
            return
        }
        var b: FileOutputStream? = null
        val fileName = XPlanApplication.instance.getExternalFilesDir(null)!!.absolutePath + "/head.jpg"// 图片名字
        try {
            b = FileOutputStream(fileName)
            mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, b)// 把数据写入文件
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } finally {
            try {
                // 关闭流
                b!!.flush()
                b.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }
    }

    // 从本地的文件中以保存的图片中 获取图片的方法
    private fun getHead(pathString: String): Bitmap? {
        var bitmap: Bitmap? = null
        try {
            val file = File(pathString)
            if (file.exists()) {
                bitmap = BitmapFactory.decodeFile(pathString)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return bitmap
    }


    /**
     * 剪切图片
     *
     * @function:
     * @author:Jerry
     * @date:2013-12-30
     * @param uri
     */
    private fun crop(uri: Uri?,outFile: File?) {
        // 裁剪图片意图
        val intent = Intent("com.android.camera.action.CROP")
        intent.setDataAndType(uri, "image/*")
        intent.putExtra("crop", "true")
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)

        // 裁剪框的比例，1：1
        intent.putExtra("aspectX", 1)
        intent.putExtra("aspectY", 1)
        // 裁剪后输出图片的尺寸大小
        intent.putExtra("outputX", 250)
        intent.putExtra("outputY", 250)
        // 图片格式
        intent.putExtra("outputFormat", "JPEG")
        intent.putExtra("noFaceDetection", true)// 取消人脸识别
        intent.putExtra("return-data", false)// true:不返回uri，false：返回uri
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(outFile))
        startActivityForResult(intent, PHOTO_REQUEST_CUT)
    }

    private fun hasSdcard(): Boolean {
        return Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED
    }

    /**
     * 支付工具
     */
    private fun goToPay() {
        val view = View.inflate(activity, R.layout.dialog_change_head_picture, null)
        (view.findViewById<View>(R.id.text1) as TextView).text = "微信支付"
        (view.findViewById<View>(R.id.text2) as TextView).text = "支付宝支付"
        //要用 android.support.v7.app.AlertDialog 并且设置主题
        val dialog = AlertDialog.Builder(activity!!)
                .setTitle("支付方式")
                .setView(view)
                .create()
        dialog.show()
        val params = dialog.window!!.attributes
        params.width = activity!!.windowManager.defaultDisplay.width * 5 / 6
        //	params.height = 200 ;
        dialog.window!!.attributes = params
        view.findViewById<View>(R.id.ll_from_camera).setOnClickListener {
            SnackbarUtils.ShortToast(mContentView, "应用需要审核才能使用")
            //				doWXPay("");
            dialog.dismiss()
        }
        view.findViewById<View>(R.id.ll_from_gallery).setOnClickListener {
            SnackbarUtils.ShortToast(mContentView, "应用需要审核才能使用")
            //				doAlipay("");
            dialog.dismiss()
        }
    }

    /**
     * 微信支付
     * 支付流程参见https://pay.weixin.qq.com/wiki/doc/api/app/app.php?chapter=8_3
     * 商户系统和微信支付系统主要交互说明：
     * 步骤1：用户在商户APP中选择商品，提交订单，选择微信支付。
     * 步骤2：商户后台收到用户支付单，调用微信支付统一下单接口。参见【统一下单API】。
     * 步骤3：统一下单接口返回正常的prepay_id，再按签名规范重新生成签名后，将数据传输给APP。
     * 参与签名的字段名为appid，partnerid，prepayid，noncestr，timestamp，package。注意：package的值格式为Sign=WXPay
     * 步骤4：商户APP调起微信支付。api参见本章节【app端开发步骤说明】
     * 步骤5：商户后台接收支付通知。api参见【支付结果通知API】
     * 步骤6：商户后台查询支付结果。，api参见【查询订单API】
     *
     *
     * 微信客户端支付完成后会返回给你客户端一个支付结果。
     * 同时微信的服务端会主动调用你服务端的接口发送支付结果通知。
     * 逻辑处理应该是你服务端接收到支付结果后处理，比如修改订单状态，发货等等。
     * 不能依赖客户端的返回结果认为支付成功，是不可靠的，微信的文档也是这么建议的。
     * 你前后端的时序可以这样。客户端支付完成收到支付结果后，在一定时间内不断轮询查询服务端订单的状态有没有修改。
     * （比如5s内每s查询一次）这样以服务端的交易状态为准（参见微信流程图）
     *
     * @param pay_param 支付服务生成的支付参数
     */
    private fun doWXPay(pay_param: String) {
        val wx_appid = "wxXXXXXXX"     //替换为自己的appid
        WXPay.init(XPlanApplication.instance, wx_appid)      //要在支付前调用
        WXPay.getInstance().doPay(pay_param, object : WXPay.WXPayResultCallBack {
            override fun onSuccess() {
                //				Toast.makeText(XPlanApplication.getInstance(), "支付成功", Toast.LENGTH_SHORT).show();
            }

            override fun onCancel() {
                //				Toast.makeText(XPlanApplication.getInstance(), "支付取消", Toast.LENGTH_SHORT).show();
            }

            override fun onError(error_code: Int) {
                when (error_code) {
                    WXPay.NO_OR_LOW_WX -> {
                    }
                    WXPay.ERROR_PAY_PARAM -> {
                    }
                    WXPay.ERROR_PAY -> {
                    }
                    else -> {
                    }
                }//						Toast.makeText(XPlanApplication.getInstance(), "未安装微信或微信版本过低", Toast.LENGTH_SHORT).show();
                //						Toast.makeText(XPlanApplication.getInstance(), "参数错误", Toast.LENGTH_SHORT).show();
                //						Toast.makeText(XPlanApplication.getInstance(), "支付失败", Toast.LENGTH_SHORT).show();
            }
        })
    }

    /**
     * 支付宝支付
     * @param pay_param 支付服务生成的支付参数
     */
    private fun doAlipay(pay_param: String) {
        Alipay(activity, pay_param, object : Alipay.AlipayResultCallBack {
            override fun onSuccess() {
                //				Toast.makeText(XPlanApplication.getInstance(), "支付成功", Toast.LENGTH_SHORT).show();
            }

            override fun onDealing() {
                //				Toast.makeText(XPlanApplication.getInstance(), "支付处理中...", Toast.LENGTH_SHORT).show();
            }

            override fun onCancel() {
                //				Toast.makeText(XPlanApplication.getInstance(), "支付取消", Toast.LENGTH_SHORT).show();
            }

            override fun onError(error_code: Int) {
                when (error_code) {
                    Alipay.ERROR_RESULT -> {
                    }
                    Alipay.ERROR_NETWORK -> {
                    }
                    Alipay.ERROR_PAY -> {
                    }
                    else -> {
                    }
                }//						Toast.makeText(XPlanApplication.getInstance(), "支付失败:支付结果解析错误", Toast.LENGTH_SHORT).show();
                //						Toast.makeText(XPlanApplication.getInstance(), "支付失败:网络连接错误", Toast.LENGTH_SHORT).show();
                //						Toast.makeText(XPlanApplication.getInstance(), "支付错误:支付码支付失败", Toast.LENGTH_SHORT).show();
                //						Toast.makeText(XPlanApplication.getInstance(), "支付错误", Toast.LENGTH_SHORT).show();
            }
        }).doPay()
    }

    override fun isShowLoading(isShow: Boolean, message: String) {}

    override fun updateCityWeather(weatherBean: WeatherBeseModel.WeatherBean, temperature: String, pm: String, resid: Int, airCondition: String, cityName: String, weather: String, weatherRes: Int) {
        resultBean = weatherBean
        if (resultBean != null) {
            header_tv_temperature.text = temperature
            tv_pm.text = pm
            tv_pm.setBackgroundResource(resid)
            tv_pm_str.text = airCondition
            tv_city.text = cityName
            tv_weathr.text = weather
            header_iv_weather.setImageDrawable(resources.getDrawable(weatherRes))
        }
    }

    override fun updateCacheSize(cacheSize: String) {
        mProgressDialog?.let {
            if(it.isShowing){
                it.dismiss()
            }
        }
        tv_cache.text = cacheSize
    }

    /**
     * 显示弹框公告
     */
    private fun showAdDialog() {
        //		String url = "http://cn.bing.com/az/hprichbg/rb/TartumaaEstonia_ZH-CN13968964399_720x1280.jpg";
        val view = LayoutInflater.from(activity).inflate(R.layout.ad_dialog_contentview, null)
        val diaLog = AdDialog(activity, view, AdDialog.ViewType.IMG)
        diaLog //设置外边距
                .setContentView_Margin_Top(0)
                .setContentView_Margin_Bottom(0)
                .setContentView_Margin_Left(0)
                .setContentView_Margin_Right(0)
                .setOverScreen(true) //设置是否全屏,覆盖状态栏
                .setCloseButtonImg(R.drawable.ad_dialog_closebutton) //设置关闭按钮图片
                .setCloseButtonListener { }
                .show()
    }


    override fun onDestroy() {
        mProgressDialog?.dismiss()
        presenter?.onDestory()
        super.onDestroy()
    }

    companion object {
        private val PHOTO_REQUEST_CAMERA = 1// 拍照
        private val PHOTO_REQUEST_GALLERY = 2// 从相册中选择
        private val PHOTO_REQUEST_CUT = 3// 结果
        /* 头像名称 */
        private val PHOTO_FILE_NAME = "temp_photo.jpg"
        private val HEAD_PATH = XPlanApplication.instance.getExternalFilesDir(null)!!.absolutePath + "/head.jpg"

        /**
         * 从url中，获得默认文件名
         */
        fun getDefaultDownLoadFileName(url: String?): String {
            if (url == null || url.length == 0) return ""
            val nameStart = url.lastIndexOf('/') + 1
            return url.substring(nameStart)
        }
    }
}
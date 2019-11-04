package com.zh.xplan.ui.flutter

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.zh.xplan.R
import com.zh.xplan.ui.playeractivity.PlayerDetailActivity
import com.zh.xplan.ui.zxing.activity.CaptureActivity
import io.flutter.facade.Flutter
import io.flutter.plugin.common.MethodChannel
import io.flutter.view.FlutterView
import kotlinx.android.synthetic.main.activity_flutter.*

class MyFlutterActivity : AppCompatActivity() {
    private val NATIVE_CHANNEL = "flutter_open_native"
    private val NATIVE_OPEN_CAPTURE = "flutter_open_capture"
    private val NATIVE_OPEN_PLAY_DETAIL = "flutter_open_play_detail"

    private var flutterView: FlutterView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        transparentStatusBar(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_flutter)
        flutterView = Flutter.createView(
                this,
                lifecycle,
                "route1"
        )
        flutterView?.let { flutterView ->
            flutter_container.addView(flutterView)
            val listeners = arrayOfNulls<FlutterView.FirstFrameListener>(1)
            listeners[0] = FlutterView.FirstFrameListener { flutter_container.visibility = View.VISIBLE }
            flutterView.addFirstFrameListener(listeners[0])

            /**
             * Flutter调用native
             */
            MethodChannel(flutterView, NATIVE_CHANNEL).setMethodCallHandler { methodCall, result ->
                when {
                    NATIVE_OPEN_CAPTURE == methodCall.method -> //打开扫一扫
                        startActivity(Intent(this@MyFlutterActivity, CaptureActivity::class.java))
                    NATIVE_OPEN_PLAY_DETAIL == methodCall.method -> {
                        //视频
                        val intent = Intent(this@MyFlutterActivity, PlayerDetailActivity::class.java)
                        intent.putExtra("playUrl", methodCall.argument<Any>("playUrl")?.toString())
                        intent.putExtra("playTitle", methodCall.argument<Any>("playTitle")?.toString())
                        intent.putExtra("playDescription", "")
                        intent.putExtra("playPic", methodCall.argument<Any>("playPic")?.toString())
                        intent.putExtra("playId", methodCall.argument<Any>("playId")?.toString())
                        startActivity(intent)
                    }
                    else -> result.notImplemented()
                }
            }
        }
    }


    /**
     * 使状态栏透明,虚拟按键导航不透明
     */
    private fun transparentStatusBar(activity: Activity) {
        activity.window.requestFeature(Window.FEATURE_NO_TITLE)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity.window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS or WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
            activity.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            activity.window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            activity.window.statusBarColor = Color.TRANSPARENT
        } else {
            activity.window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        }
    }

    override fun onBackPressed() {
        flutterView?.popRoute() ?: super.onBackPressed()
    }

}

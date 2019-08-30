package com.zh.xplan.ui.flutter;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import androidx.appcompat.app.AppCompatActivity;
import com.zh.xplan.R;
import com.zh.xplan.ui.playeractivity.PlayerDetailActivity;
import com.zh.xplan.ui.zxing.activity.CaptureActivity;
import org.jetbrains.annotations.NotNull;
import io.flutter.facade.Flutter;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.view.FlutterView;

public class MyFlutterActivity extends AppCompatActivity {
    private static final String NATIVE_CHANNEL = "flutter_open_native";
    private static final String NATIVE_OPEN_CAPTURE = "flutter_open_capture";
    private static final String NATIVE_OPEN_PLAY_DETAIL = "flutter_open_play_detail";

    private FlutterView flutterView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flutter);
        flutterView = Flutter.createView(
                this,
                getLifecycle(),
                "route1"
        );
        final FrameLayout layout = findViewById(R.id.flutter_container);
        layout.addView(flutterView);
        final FlutterView.FirstFrameListener[] listeners = new FlutterView.FirstFrameListener[1];
        listeners[0] = new FlutterView.FirstFrameListener() {
            @Override
            public void onFirstFrame() {
                layout.setVisibility(View.VISIBLE);
            }
        };
        flutterView.addFirstFrameListener(listeners[0]);

        /**
         * Flutter调用native
         */
        new MethodChannel(flutterView, NATIVE_CHANNEL).setMethodCallHandler(
                new MethodChannel.MethodCallHandler() {

                    @Override
                    public void onMethodCall(@NotNull MethodCall methodCall, @NotNull MethodChannel.Result result) {
                        if (NATIVE_OPEN_CAPTURE.equals(methodCall.method)) {
                            //打开扫一扫
                            startActivity(new Intent(MyFlutterActivity.this, CaptureActivity.class));
                        } else if (NATIVE_OPEN_PLAY_DETAIL.equals(methodCall.method)) {
                            //视频
                            Intent intent = new Intent(MyFlutterActivity.this, PlayerDetailActivity.class);
                            intent.putExtra("playUrl", methodCall.argument("playUrl").toString());
                            intent.putExtra("playTitle", methodCall.argument("playTitle").toString());
                            intent.putExtra("playDescription", "");
                            intent.putExtra("playPic",methodCall.argument("playPic").toString());
                            intent.putExtra("playId", methodCall.argument("playId").toString());
                            startActivity(intent);
                        }{
                            result.notImplemented();
                        }
                    }
                });
    }

    @Override
    public void onBackPressed() {
        if(this.flutterView!=null){
            this.flutterView.popRoute();
        }else {
            super.onBackPressed();
        }
    }

}

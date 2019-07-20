package com.zh.xplan.ui.flutter;

import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import androidx.appcompat.app.AppCompatActivity;
import com.zh.xplan.R;
import io.flutter.facade.Flutter;
import io.flutter.view.FlutterView;

public class MyFlutterActivity extends AppCompatActivity {
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

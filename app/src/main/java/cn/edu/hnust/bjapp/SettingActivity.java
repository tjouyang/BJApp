package cn.edu.hnust.bjapp;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;

import cn.edu.hnust.bjapp.cache.CacheUtils;
import cn.edu.hnust.bjapp.utils.NetworkHelper;
import cn.edu.hnust.tbapp.R;
import cn.edu.hnust.bjapp.mytest.ImageLoaderUtil;

/**
 * Created by tjouyang on 2016/10/22.
 * 设置界面
 */

public class SettingActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        gestureDetector = new GestureDetector(SettingActivity.this,onGestureListener);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.slide_right_in, R.anim.slide_right_out);
            }
        });
        ((TextView)findViewById(R.id.toolbar_title)).setText("设置");
        SwitchCompat switchCompat = (SwitchCompat) findViewById(R.id.setting_load_enable);
        switchCompat.setChecked(false);
        switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                getSharedPreferences("Config", MODE_PRIVATE).edit().putBoolean("load_enable", b).apply();
                if (NetworkHelper.GetNetype(SettingActivity.this) != NetworkHelper.NETTYPE_WIFI) {
                    ImageLoaderUtil.setLoadEnable(b);
                }
            }
        });
        findViewById(R.id.setting_clean_cache).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO 清除缓存
                CacheUtils.cleanAll();
            }
        });
    }

    final int RIGHT = 0;
    final int LEFT = 1;
    private GestureDetector gestureDetector;

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return super.dispatchTouchEvent(ev);
    }

    private GestureDetector.OnGestureListener onGestureListener =
            new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                                       float velocityY) {
                    float x = e2.getX() - e1.getX();
//                    float y = e2.getY() - e1.getY();
                    if (x > 0) {
                        doResult(RIGHT);
                    } else if (x < 0) {
                        doResult(LEFT);
                    }
                    return true;
                }
            };

    public boolean onTouchEvent(MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }

    public void doResult(int action) {

        switch (action) {
            case RIGHT:
                finish();
                overridePendingTransition(R.anim.slide_right_in, R.anim.slide_right_out);
                break;

            case LEFT:
                break;

        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}

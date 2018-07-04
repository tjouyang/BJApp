package cn.edu.hnust.bjapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import cn.edu.hnust.tbapp.R;

/**
 * Created by tjouyang on 2016/11/28.
 * 欢迎页面
 */

public class WelcomeActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(WelcomeActivity.this, LoginActivity.class));
                finish();
            }
        }, 400);
    }
}

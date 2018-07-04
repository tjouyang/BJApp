package cn.edu.hnust.bjapp;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import cn.edu.hnust.bjapp.utils.Utils;
import cn.edu.hnust.tbapp.R;

/**
 * Created by tjouyang on 2016/10/23.
 *
 */
public class GroupInfoActivity extends Activity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_info);
        gestureDetector = new GestureDetector(GroupInfoActivity.this,onGestureListener);
        ((ImageView) findViewById(R.id.group_QR_code)).setImageBitmap(Utils.createQRImage("3", 240, 480));
        //TODO 调用接口查询用户加的团id
        ((Toolbar)findViewById(R.id.toolbar)).setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.slide_right_in, R.anim.slide_right_out);
            }
        });
        ((TextView)findViewById(R.id.toolbar_title)).setText("游团信息");
    }

    @Override
    protected void onPause() {
        super.onPause();
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
}

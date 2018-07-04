package cn.edu.hnust.bjapp;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import cn.edu.hnust.bjapp.cptr.recyclerview.RecyclerAdapterWithHF;
import cn.edu.hnust.tbapp.R;
import cn.edu.hnust.bjapp.adapter.MyProductAdapter;
import cn.edu.hnust.bjapp.cptr.PtrClassicFrameLayout;
import cn.edu.hnust.bjapp.tasks.UpdateMyProductTask;

/**
 * Created by tjouyang on 2016/10/26.
 * 我的订单
 */

public class MyProductActivity extends Activity {
    SharedPreferences sp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_product);
        gestureDetector = new GestureDetector(MyProductActivity.this,onGestureListener);
        ((Toolbar) findViewById(R.id.toolbar)).setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.slide_right_in, R.anim.slide_right_out);
            }
        });
        ((TextView) findViewById(R.id.toolbar_title)).setText("我的订单");
        sp = getSharedPreferences("Config", MODE_PRIVATE);
        initListView(getSharedPreferences("Config", MODE_PRIVATE).getInt("sign", 2));
    }

    private void initListView(int identify) {
        int id_rv, id_frame;
        if (identify == 2) {
            id_rv = R.id.my_product_tourist_rv;
            id_frame = R.id.my_product_tourist_frame;
        } else {
            id_rv = R.id.my_product_guide_rv;
            id_frame = R.id.my_product_guide_frame;
        }
        RecyclerView recyclerView = (RecyclerView) findViewById(id_rv);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        PtrClassicFrameLayout ptrClassicFrameLayout = (PtrClassicFrameLayout) findViewById(id_frame);
        ptrClassicFrameLayout.setVisibility(View.VISIBLE);
        MyProductAdapter productAdapter = new MyProductAdapter(this, identify, sp.getInt("id", -1));
        RecyclerAdapterWithHF recyclerAdapterWithHF = new RecyclerAdapterWithHF(productAdapter);
        recyclerView.setAdapter(recyclerAdapterWithHF);
        ptrClassicFrameLayout.setLastUpdateTimeRelateObject(this);
        UpdateMyProductTask task = new UpdateMyProductTask(recyclerAdapterWithHF, ptrClassicFrameLayout, productAdapter);
        ptrClassicFrameLayout.setOnLoadMoreListener(task);
        ptrClassicFrameLayout.setPtrHandler(task);
        ptrClassicFrameLayout.autoRefresh();
    }

    final int RIGHT = 0;
    final int LEFT = 1;
    private GestureDetector gestureDetector;

//    @Override
//    public boolean dispatchTouchEvent(MotionEvent ev) {
//        return super.dispatchTouchEvent(ev);
//    }

    private GestureDetector.OnGestureListener onGestureListener =
            new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                                       float velocityY) {
                    if (e1 == null || e2 == null){
                        Log.e("onFling", "Null");
                        return false;
                    }
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

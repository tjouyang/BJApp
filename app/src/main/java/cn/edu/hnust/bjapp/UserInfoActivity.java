package cn.edu.hnust.bjapp;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;

import cn.edu.hnust.bjapp.service.QueryInfomationService;
import cn.edu.hnust.bjapp.ui.ProgressDialog;
import cn.edu.hnust.bjapp.utils.DemoApplication;
import cn.edu.hnust.bjapp.utils.Utils;
import cn.edu.hnust.tbapp.R;
import cn.edu.hnust.bjapp.cache.CacheUtils;
import cn.edu.hnust.bjapp.mytest.ImageLoaderUtil;

/**
 * Created by tjouyang on 2016/10/21.
 * 用户资料
 */

public class UserInfoActivity extends Activity {
    final int SUCCESS = 1;
    final int FAIL = 2;
    final int CONNECT_FAIL = 3;
    SharedPreferences sp;
    TextView age;
    TextView address;
    RadioGroup radioGroup;
    int sex;
    ImageView imageView;
    LinearLayout tourist;   //游客职业
    TextView job;
    TextView nick;
    ProgressDialog progressDialog;
    CoordinatorLayout container;
    private CustomHandler handler = new CustomHandler(this);

    static class CustomHandler extends Handler {
        WeakReference<UserInfoActivity> mActivity;

        CustomHandler(UserInfoActivity aFragment) {
            mActivity = new WeakReference<>(aFragment);
        }

        @Override
        public void handleMessage(Message message) {
            if (mActivity != null) {
                UserInfoActivity theActivity = mActivity.get();
                if (theActivity != null)
                    theActivity.updateUiByMessage(message);
            }
        }
    }

    private void updateUiByMessage(Message msg) {
        progressDialog.dismiss();
        switch (msg.what) {
            case SUCCESS:
                showSnackBar("修改成功");
                startService(new Intent(this, QueryInfomationService.class));
                break;
            case FAIL:
                showSnackBar("修改失败");
                break;
            case CONNECT_FAIL:
                showSnackBar("连接服务器失败");
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        sp = getSharedPreferences("Config", MODE_PRIVATE);
        gestureDetector = new GestureDetector(UserInfoActivity.this,onGestureListener);
//        Intent intent = getIntent();
//        if(intent != null){
//            String path = intent.getStringExtra("path");
//            ImageLoaderUtil.setBitmapFromNetwork(path, imageView);
//        }
        init();
        addData();
    }

    private void addData(){
        JSONObject js = CacheUtils.getUserInfo();
        if(js != null){
            try{
                ImageLoaderUtil.setBitmapFromNetwork(js.getString("img"), imageView);
                nick.setText(js.getString("nickname"));

                if(js.getInt("sex") == 1){
                    radioGroup.check(R.id.man);
                } else {
                    radioGroup.check(R.id.woman);
                }
                age.setText("" + js.getInt("age") + "");
                address.setText(js.getString("address"));
                if(job.getVisibility() == View.VISIBLE){
                    job.setText(js.getString("job"));
                }
            }catch (JSONException e){
                e.printStackTrace();
            }
        }
    }

    /**
     * 初始化控件
     */
    private void init(){
        container = (CoordinatorLayout) findViewById(R.id.user_info_container);
        age = (TextView) findViewById(R.id.user_info_age);
        address = (TextView) findViewById(R.id.user_info_address);
        radioGroup = (RadioGroup) findViewById(R.id.user_info_rg);
        job = (TextView) findViewById(R.id.user_info_job);
        nick = (TextView) findViewById(R.id.user_info_nick);
        tourist = (LinearLayout) findViewById(R.id.user_info_tourist_job);
        imageView = (ImageView) findViewById(R.id.user_info_header);
        if(sp.getInt("sign", 2) == 2){
            tourist.setVisibility(View.VISIBLE);
        }
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (i == R.id.man){
                    sex = 1;
                } else {
                    sex = 2;
                }
            }
        });
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(UserInfoActivity.this, MainActivity.class));
                finish();
            }
        });
        ((TextView) findViewById(R.id.toolbar_title)).setText("我的资料");
        findViewById(R.id.user_info_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!check())
                    return;
                String path = Utils.getPath() + "/pigapp/PersonalDataServlet";
                progressDialog = new ProgressDialog(UserInfoActivity.this);
                progressDialog.setMsg("登陆中，请稍后");
                progressDialog.show();
                JSONObject js = new JSONObject();
                try {
                    js.put("sign", sp.getInt("sign", 2));
                    js.put("id", sp.getInt("id", 0));
                    js.put("age", Integer.parseInt(age.getText().toString().trim()));
                    js.put("sex", sex);
                    js.put("address", address.getText().toString() + " ");
                    js.put("job", job == null ? "" : job.getText().toString() + " ");
                    js.put("nickname", nick.getText().toString().trim());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                JsonObjectRequest request = new JsonObjectRequest(
                        Request.Method.POST, path, js,
                        new Response.Listener<JSONObject>() {

                            @Override
                            public void onResponse(JSONObject response) {
                                Message msg = Message.obtain();
                                try {
                                    String s = response.getString("result");
                                    if (s.equals("success")) {
                                        msg.what = SUCCESS;
                                    } else {
                                        msg.what = FAIL;
                                    }
                                } catch (JSONException e) {
                                    msg.what = FAIL;
                                }
                                handler.sendMessage(msg);
                            }

                        }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        handler.sendEmptyMessage(CONNECT_FAIL);
                    }
                });
                DemoApplication.getHttpRequestQueue().add(request);
            }
        });
    }

    private boolean check(){
        if(age.getText().toString().equals("")){
            showSnackBar("请填写年龄!");
            return false;
        }
        if(nick.getText().toString().trim().equals("")){
            showSnackBar("请填写昵称!");
            return false;
        }
        return true;
    }

    private void showSnackBar(String text) {
        Utils.showSnackBar(container, text);
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

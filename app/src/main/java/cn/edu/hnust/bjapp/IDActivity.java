package cn.edu.hnust.bjapp;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import cn.edu.hnust.tbapp.R;
import cn.edu.hnust.bjapp.ui.ProgressDialog;
import cn.edu.hnust.bjapp.utils.DemoApplication;
import cn.edu.hnust.bjapp.utils.Utils;

/**
 * Created by tjouyang on 2016/10/29.
 * 信息认证
 */

public class IDActivity extends Activity {
    SharedPreferences sp;
    ProgressDialog progressDialog;
    TextView phone, email, identification, company, certification;
    View container;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_identify);
        gestureDetector = new GestureDetector(IDActivity.this,onGestureListener);
        sp = getSharedPreferences("Config", MODE_PRIVATE);
        phone = (TextView)findViewById(R.id.identify_phone);
        email = (TextView)findViewById(R.id.identify_email);
        identification = (TextView) findViewById(R.id.identify_id_card);
        company = (TextView) findViewById(R.id.identify_guide_company);
        certification = (TextView) findViewById(R.id.identify_guide_number);
        container = findViewById(R.id.identify_container);
        if (sp.getInt("sign", 2) == 2){
            //游客不显示
            findViewById(R.id.identify_guide_watch1).setVisibility(View.GONE);
            findViewById(R.id.identify_guide_watch2).setVisibility(View.GONE);
        }
        findViewById(R.id.identify_commit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!check(view))
                    return;
                progressDialog = new ProgressDialog(IDActivity.this);
                progressDialog.setMsg("正在提交");
                progressDialog.show();
                String path = Utils.getPath() + "/pigapp/IdentificationServlet";
                JSONObject js = new JSONObject();
                try {
                    js.put("sign", sp.getInt("sign", -1));
                    js.put("id", sp.getInt("id", -1));
                    js.put("phone", phone.getText().toString());
                    js.put("email", email.getText().toString());
                    js.put("identification", identification.getText().toString());
                    js.put("company", company.getText().toString());
                    js.put("certification", certification.getText().toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                DemoApplication.getHttpRequestQueue().add(new JsonObjectRequest(path, js, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        try {
                            if ("success".equals(jsonObject.getString("result")))
                            {
                                if (progressDialog != null){
                                    progressDialog.dismiss();
                                }
                                Utils.showSnackBar(container, "提交成功,等待审核");
                                sp.edit().putInt("auth", 2).apply();//TODO 这里直接给他认证成功了,审核以后做
                            } else {
                                if (progressDialog != null){
                                    progressDialog.dismiss();
                                }
                                Utils.showSnackBar(container, "提交失败!");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            if (progressDialog != null){
                                progressDialog.dismiss();
                            }
                            Utils.showSnackBar(container, "提交失败!请稍候再试");
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        if (progressDialog != null){
                            progressDialog.dismiss();
                        }
                        Utils.showSnackBar(container, "连接服务器失败!");
                    }
                }));
            }
        });
        ((Toolbar)findViewById(R.id.toolbar)).setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.slide_right_in, R.anim.slide_right_out);
            }
        });
        ((TextView)findViewById(R.id.toolbar_title)).setText("信息认证");
    }

    private boolean check(View view){
        String text;
        text = (phone).getText().toString();
        if(!Utils.isMobileNO(text)){
            Utils.showSnackBar(view, "手机号不正确!");
            return false;
        }
        text = (email).getText().toString().trim();
        if("".equals(text)){
            Utils.showSnackBar(view, "邮箱不能为空!");
            return false;
        }
        return  true;
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

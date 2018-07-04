package cn.edu.hnust.bjapp;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;

import cn.edu.hnust.bjapp.ui.ProgressDialog;
import cn.edu.hnust.bjapp.utils.DemoApplication;
import cn.edu.hnust.tbapp.R;
import cn.edu.hnust.bjapp.utils.Utils;

/**
 * Created by tjouyang on 2016/10/10.
 * register activity
 */
public class RegisterActivity extends AppCompatActivity {
    private final int REGISTER_SUCCESS = 0X01;
    private final int REGISTER_FAIL = 0x02;
    private final int CONNECT_FAIL = 0x03;
    private TextInputEditText username, nickname, password, passwordConfirm;
    private ProgressDialog progressDialog;
    RadioGroup radioGroup;
    private int select = 2; //1导游 , 2游客
    CoordinatorLayout container;    //作为Snackbar的容器
    private CustomHandler handler = new CustomHandler(this);

    static class CustomHandler extends Handler {
        WeakReference<RegisterActivity> mActivity;

        CustomHandler(RegisterActivity aFragment) {
            mActivity = new WeakReference<>(aFragment);
        }

        @Override
        public void handleMessage(Message message) {
            if (mActivity != null) {
                RegisterActivity theActivity = mActivity.get();
                if (theActivity != null)
                    theActivity.updateUiByMessage(message);
            }
        }
    }

    private void updateUiByMessage(Message msg){
        progressDialog.dismiss();
        switch (msg.what) {
            case REGISTER_FAIL:
                showSnackBar("账号已存在!");
                break;
            case REGISTER_SUCCESS:
                showSnackBar("注册成功!");
                break;
            case CONNECT_FAIL:
                showSnackBar("连接服务器失败!");
                break;
        }
    }

    private void showSnackBar(String text) {
        Utils.showSnackBar(container, text);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        gestureDetector = new GestureDetector(RegisterActivity.this,onGestureListener);
        username = (TextInputEditText) findViewById(R.id.register_username);
        password = (TextInputEditText) findViewById(R.id.register_password);
        nickname = (TextInputEditText) findViewById(R.id.register_nickname);
        container = (CoordinatorLayout) findViewById(R.id.register_container);
        passwordConfirm = (TextInputEditText) findViewById(R.id.register_password_confirm);
        radioGroup = (RadioGroup) findViewById(R.id.register_rg);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        ((TextView)findViewById(R.id.toolbar_title)).setText("注册");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.slide_right_in, R.anim.slide_right_out);
            }
        });
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if(i == R.id.register_guide)
                    select = 1;
                if(i == R.id.register_tourist)
                    select = 2;
            }
        });
        findViewById(R.id.register_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(username.getText().toString().trim().isEmpty())
                {
                    showSnackBar("账号不能为空!");
                    return;
                }
                if(password.getText().toString().trim().isEmpty())
                {
                    showSnackBar("密码不能为空!");
                    return;
                }
                if(nickname.getText().toString().trim().isEmpty())
                {
                    showSnackBar("昵称不能为空!");
                    return;
                }
                if(!password.getText().toString().equals(passwordConfirm.getText().toString()))
                {
                    showSnackBar("两次输入的密码不一致!");
                    return;
                }
                register();
            }
        });
    }

    private void register() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMsg("正在注册");
        progressDialog.show();
        String Path = Utils.getPath() + "/pigapp/RegisterServlet";
        JSONObject js = new JSONObject();
        try {
            js.put("username", username.getText().toString());
            js.put("password", password.getText().toString());
            js.put("sign", select);
            js.put("isthird",0);
            js.put("nickname",nickname.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest newMissRequest = new JsonObjectRequest(
                Request.Method.POST, Path, js,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Message msg = Message.obtain();
                        try {
                            String s = response.getString("result");
                            if (s.equals("success")) {
                                msg.what = REGISTER_SUCCESS;
                            } else {
                                msg.what = REGISTER_FAIL;
                            }
                        } catch (JSONException e) {
                            msg.what = REGISTER_FAIL;
                        }
                        handler.sendMessage(msg);
                    }

                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                handler.sendEmptyMessage(CONNECT_FAIL);
            }
        });
        DemoApplication.getHttpRequestQueue().add(newMissRequest);
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

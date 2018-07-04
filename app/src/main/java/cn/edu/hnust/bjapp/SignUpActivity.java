package cn.edu.hnust.bjapp;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;

import cn.edu.hnust.bjapp.adapter.GrouperAdapter;
import cn.edu.hnust.bjapp.cptr.PtrClassicFrameLayout;
import cn.edu.hnust.bjapp.cptr.recyclerview.RecyclerAdapterWithHF;
import cn.edu.hnust.bjapp.tasks.UpdateSignResultTask;
import cn.edu.hnust.bjapp.ui.ProgressDialog;
import cn.edu.hnust.bjapp.utils.DemoApplication;
import cn.edu.hnust.bjapp.utils.Utils;
import cn.edu.hnust.tbapp.R;

/**
 * Created by tjouyang on 2016/10/23.
 * 签到,分散活动
 */

public class SignUpActivity extends Activity {
    final private int FAIL = 2;
    final private int SUCCESS = 1;
    final private int CONNECT_FAIL = 3;
    final private int RE_SIGN = 4;
    final private int GUIDE_SUCCESS = 5;
    final private int INIT_BUTTON = 6;
    final private int BUTTON_START_ENABLE = 7;
    final private int BUTTON_FINISH_ENABLE = 8;
    Button start, end, sign_up;     //开始, 结束, 签到
    SharedPreferences sp;
    ProgressDialog progressDialog;
    Message msg;
    TextView text;
    TextView code;
    View about;    //导游aboutview
    View watch;     //导游查看按钮
    View container;          //导游查看游客list容器
    //至于为什么这样写handler请看login部分
    private CustomHandler handler = new CustomHandler(this);

    static class CustomHandler extends Handler {
        WeakReference<SignUpActivity> mActivity;

        CustomHandler(SignUpActivity aFragment) {
            mActivity = new WeakReference<>(aFragment);
        }

        @Override
        public void handleMessage(Message message) {
            if (mActivity != null) {
                SignUpActivity theActivity = mActivity.get();
                if (theActivity != null)
                    theActivity.updateUiByMessage(message);
            }
        }
    }

    private void updateUiByMessage(Message msg) {
        if (progressDialog != null)
            progressDialog.dismiss();
        switch (msg.what) {
            case SUCCESS:
                Utils.showSnackBar((View) msg.obj, "签到成功");
                break;
            case FAIL:
                Utils.showSnackBar((View) msg.obj, "签到失败");
                break;
            case CONNECT_FAIL:
                Utils.showSnackBar((View) msg.obj, "连接服务器失败");
                break;
            case RE_SIGN:
                Utils.showSnackBar((View) msg.obj, "你已经签过到了");
                break;
            case GUIDE_SUCCESS:
                code.setText("签到码 : " + msg.obj.toString() + "");
                break;
            case INIT_BUTTON:
                setButtonEnable(start, false);
                setButtonEnable(end, false);
                break;
            case BUTTON_FINISH_ENABLE:
                setButtonEnable(start, false);
                setButtonEnable(end, true);
                break;
            case BUTTON_START_ENABLE:
                setButtonEnable(start, true);
                setButtonEnable(end, false);
                break;
        }
    }

    private void setButtonEnable(Button b, Boolean enable) {
        b.setEnabled(enable);
        if (enable) {
            b.setAlpha(1);
        } else {
            b.setAlpha(0.5f);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sp = getSharedPreferences("Config", MODE_PRIVATE);
//        gestureDetector = new GestureDetector(SignUpActivity.this,onGestureListener);
        if (sp.getInt("sign", 2) == 1) {
            setContentView(R.layout.activity_guide_sign_up);
            initGuide();
            initActivity();
        } else {
            setContentView(R.layout.activity_tourist_sign_up);
            initTourist();
        }
        ((Toolbar) findViewById(R.id.toolbar)).setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.slide_right_in, R.anim.slide_right_out);
            }
        });
        ((TextView) findViewById(R.id.toolbar_title)).setText("分散活动");
        initListView();
    }

    private void initTourist() {
        text = (TextView) findViewById(R.id.sign_up_text);
        findViewById(R.id.sign_up_bt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (progressDialog == null)
                    progressDialog = new ProgressDialog(SignUpActivity.this);
                progressDialog.setMsg("正在签到");
                progressDialog.show();
                if (text.getText().toString().equals("")) {
                    Utils.showSnackBar(view, "请填写签到码");
                    progressDialog.dismiss();
                    return;
                }
                String path = Utils.getPath() + "/pigapp/TouristSignInServlet";
                JSONObject js = new JSONObject();
                try {
                    js.put("tourist_id", sp.getInt("id", 0));
                    js.put("code", text.getText().toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                msg = Message.obtain();
                msg.obj = view;
                JsonObjectRequest request = new JsonObjectRequest(path, js, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        try {
                            String tmp = jsonObject.getString("result");
                            if ("success".equals(tmp))
                                msg.what = SUCCESS;
                            else if ("resign".equals(tmp))
                                msg.what = RE_SIGN;
                            else
                                msg.what = FAIL;
                        } catch (JSONException e) {
                            e.printStackTrace();
                            msg.what = FAIL;
                        }
                        handler.sendMessage(msg);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        msg.what = CONNECT_FAIL;
                        handler.sendMessage(msg);
                    }
                });
                DemoApplication.getHttpRequestQueue().add(request);
            }
        });
    }

    private void initGuide() {
        start = (Button) findViewById(R.id.sign_up_start);
        end = (Button) findViewById(R.id.sign_up_finish);
        sign_up = (Button) findViewById(R.id.sign_up_bt);
        text = (TextView) findViewById(R.id.sign_up_description);
        code = (TextView) findViewById(R.id.sign_up_notice_text);
        sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (text.getText().toString().equals("")) {
                    Utils.showSnackBar(view, "请填写描述!");
                    return;
                }
                if (progressDialog == null)
                    progressDialog = new ProgressDialog(SignUpActivity.this);
                progressDialog.setMsg("定位中");
                progressDialog.show();
                String path = Utils.getPath() + "/pigapp/StartSignInServlet";
                JSONObject js = new JSONObject();
                try {
                    js.put("group_id", sp.getInt("group_id", 0));
                    js.put("description", text.getText().toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                msg = Message.obtain();
                msg.obj = view;
                JsonObjectRequest request = new JsonObjectRequest(path, js, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        try {
                            String tmp = jsonObject.getString("result");
                            if ("success".equals(tmp)) {
                                msg.what = GUIDE_SUCCESS;
                                msg.obj = jsonObject.getString("code");
                            } else
                                msg.what = FAIL;
                        } catch (JSONException e) {
                            e.printStackTrace();
                            msg.what = FAIL;
                        }
                        handler.sendMessage(msg);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        msg.what = CONNECT_FAIL;
                        handler.sendMessage(msg);
                    }
                });
                DemoApplication.getHttpRequestQueue().add(request);
            }
        });
        container = findViewById(R.id.sign_up_guide_container);
        about = findViewById(R.id.sign_up_guide_about);
        watch = findViewById(R.id.sign_up_guide_watch);
        watch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                about.setVisibility(View.GONE);
                container.setVisibility(View.VISIBLE);
            }
        });
    }

    /**
     * 初始化ListView
     */
    private void initListView() {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.sign_up_recycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        PtrClassicFrameLayout ptrClassicFrameLayout = (PtrClassicFrameLayout) findViewById(R.id.sign_up_home_frame);
        GrouperAdapter groupRvAdapter = new GrouperAdapter(this);
        RecyclerAdapterWithHF recyclerAdapterWithHF = new RecyclerAdapterWithHF(groupRvAdapter);
        recyclerView.setAdapter(recyclerAdapterWithHF);
        ptrClassicFrameLayout.setLastUpdateTimeRelateObject(this);
        UpdateSignResultTask task = new UpdateSignResultTask(recyclerAdapterWithHF, ptrClassicFrameLayout, groupRvAdapter, sp.getInt("group_id", -1));
        ptrClassicFrameLayout.setOnLoadMoreListener(task);
        ptrClassicFrameLayout.setPtrHandler(task);
        ptrClassicFrameLayout.autoRefresh();
    }

    /**
     * 初始化分散活动按钮
     */
    private void initActivity() {
        start = (Button) findViewById(R.id.sign_up_start);
        end = (Button) findViewById(R.id.sign_up_finish);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (progressDialog == null) {
                    progressDialog = new ProgressDialog(SignUpActivity.this);
                }
                progressDialog.setMsg("开启分散活动中");
                progressDialog.show();
                JSONObject js = new JSONObject();
				try {
					js.put("group_id", sp.getInt("group_id", 1));
				} catch (JSONException e) {
					e.printStackTrace();
				}
                String path = Utils.getPath() + "/pigapp/StartActivityServlet";
                JsonObjectRequest request = new JsonObjectRequest(path, js, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        try {
                            if ("success".equals(jsonObject.getString("result"))) {
                                handler.sendEmptyMessage(BUTTON_FINISH_ENABLE);
                            } else
                                handler.sendEmptyMessage(BUTTON_START_ENABLE);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            handler.sendEmptyMessage(INIT_BUTTON);
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        handler.sendEmptyMessage(INIT_BUTTON);
                    }
                });
                DemoApplication.getHttpRequestQueue().add(request);
            }
        });
        end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (progressDialog == null) {
                    progressDialog = new ProgressDialog(SignUpActivity.this);
                }
                progressDialog.setMsg("关闭分散活动中");
                progressDialog.show();
                JSONObject js = new JSONObject();
				try {
					js.put("group_id", sp.getInt("group_id", 1));
				} catch (JSONException e) {
					e.printStackTrace();
				}
                String path = Utils.getPath() + "/pigapp/StopActivityServlet";
                JsonObjectRequest request = new JsonObjectRequest(path, js, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        try {
                            if ("success".equals(jsonObject.getString("result"))) {
                                handler.sendEmptyMessage(BUTTON_START_ENABLE);
                            } else
                                handler.sendEmptyMessage(BUTTON_FINISH_ENABLE);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            handler.sendEmptyMessage(INIT_BUTTON);
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        handler.sendEmptyMessage(INIT_BUTTON);
                    }
                });
                DemoApplication.getHttpRequestQueue().add(request);
            }
        });
        JSONObject js = new JSONObject();
        try {
            js.put("group_id", sp.getInt("group_id", 1));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String path = Utils.getPath() + "/pigapp/QueryGroupStateServlet";
        JsonObjectRequest request = new JsonObjectRequest(path, js, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                try {
                    if (jsonObject.getInt("state") == 1) {
                        handler.sendEmptyMessage(BUTTON_START_ENABLE);
                    } else {
                        handler.sendEmptyMessage(BUTTON_FINISH_ENABLE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    handler.sendEmptyMessage(INIT_BUTTON);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                handler.sendEmptyMessage(INIT_BUTTON);
            }
        });
        DemoApplication.getHttpRequestQueue().add(request);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    final int RIGHT = 0;
    final int LEFT = 1;
    private GestureDetector gestureDetector;

//    @Override
//    public boolean dispatchTouchEvent(MotionEvent ev) {
//        return super.dispatchTouchEvent(ev);
//    }
//
//    private GestureDetector.OnGestureListener onGestureListener =
//            new GestureDetector.SimpleOnGestureListener() {
//                @Override
//                public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
//                                       float velocityY) {
//                    float x = e2.getX() - e1.getX();
////                    float y = e2.getY() - e1.getY();
//                    if (x > 0) {
//                        doResult(RIGHT);
//                    } else if (x < 0) {
//                        doResult(LEFT);
//                    }
//                    return true;
//                }
//            };
//
//    public boolean onTouchEvent(MotionEvent event) {
//        return gestureDetector.onTouchEvent(event);
//    }
//
//    public void doResult(int action) {
//
//        switch (action) {
//            case RIGHT:
//                finish();
//                overridePendingTransition(R.anim.slide_right_in, R.anim.slide_right_out);
//                break;
//
//            case LEFT:
//                break;
//
//        }
//    }
}

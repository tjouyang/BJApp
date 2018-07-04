package cn.edu.hnust.bjapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;

import cn.edu.hnust.tbapp.R;
import cn.edu.hnust.bjapp.date.DateChooseWheelViewDialog;
import cn.edu.hnust.bjapp.ui.ProgressDialog;
import cn.edu.hnust.bjapp.utils.DemoApplication;
import cn.edu.hnust.bjapp.utils.Utils;

/**
 * Created by tjouyang on 2016/10/16.
 * this Activity is used for request product
 */

public class ProductRequestActivity extends Activity implements View.OnClickListener {
    CoordinatorLayout container;
    private final int CONNECT_FAIL = 1;
    private final int SUCCESS = 2;
    private final int FAIL = 3;
    TextView endTime;
    TextView startTime;
    TextView deadline;
    AutoCompleteTextView dest;  //目的地
    AutoCompleteTextView limit; //人数
    AutoCompleteTextView description;    //简单描述
    AutoCompleteTextView name;  //
    AutoCompleteTextView num;   //游客当前人数
    Button apply;
    ImageView guide_photo;
    private ProgressDialog progressDialog;
    SharedPreferences sp;

    private CustomHandler handler = new CustomHandler(this);

    static class CustomHandler extends Handler {
        WeakReference<ProductRequestActivity> mActivity;

        CustomHandler(ProductRequestActivity aFragment) {
            mActivity = new WeakReference<>(aFragment);
        }

        @Override
        public void handleMessage(Message message) {
            if (mActivity != null) {
                ProductRequestActivity theActivity = mActivity.get();
                if (theActivity != null)
                    theActivity.updateUiByMessage(message);
            }
        }
    }

    private void updateUiByMessage(Message msg) {
        progressDialog.dismiss();
        switch (msg.what) {
            case CONNECT_FAIL:
                showSnackbar("连接服务器失败!");
                break;
            case SUCCESS:
                showSnackbar("发布成功!");
                break;
            case FAIL:
                showSnackbar("发布失败!");
                break;
        }
    }

    private void showSnackbar(String text) {
        if(container == null){
            return;
        }
        Utils.showSnackBar(container, text);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sp = getSharedPreferences("Config", Context.MODE_PRIVATE);
        gestureDetector = new GestureDetector(ProductRequestActivity.this,onGestureListener);
        if (sp.getInt("sign", 2) == 1) {
            //  导游界面
            setContentView(R.layout.request_product_guide);
            deadline = (TextView) findViewById(R.id.request_deadline);
            deadline.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showDateDialogWithResult(deadline, "报名截止时间");
                }
            });
            limit = (AutoCompleteTextView) findViewById(R.id.request_limit);
            name = (AutoCompleteTextView) findViewById(R.id.request_title);
            guide_photo = (ImageView) findViewById(R.id.product_imageView);
            guide_photo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final PopupWindow pop = new PopupWindow(ProductRequestActivity.this);
                    View view = getLayoutInflater().inflate(R.layout.item_popupwindows, null);
                    final LinearLayout ll_popup = (LinearLayout) view.findViewById(R.id.ll_popup);
                    pop.setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
                    pop.setHeight(RelativeLayout.LayoutParams.WRAP_CONTENT);
                    pop.setBackgroundDrawable(new BitmapDrawable());
                    pop.setFocusable(true);
                    pop.setOutsideTouchable(true);
                    pop.setContentView(view);
                    ll_popup.startAnimation(AnimationUtils.loadAnimation(ProductRequestActivity.this, R.anim.activity_translate_in));
                    pop.showAtLocation(v, Gravity.BOTTOM, 0, 0);
                    RelativeLayout parent = (RelativeLayout) view.findViewById(R.id.parent);
                    Button bt1 = (Button) view
                            .findViewById(R.id.item_popupwindows_camera);
                    Button bt2 = (Button) view
                            .findViewById(R.id.item_popupwindows_Photo);
                    Button bt3 = (Button) view
                            .findViewById(R.id.item_popupwindows_cancel);
                    parent.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            pop.dismiss();
                            ll_popup.clearAnimation();
                        }
                    });
                    bt1.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            photo();
                            pop.dismiss();
                            ll_popup.clearAnimation();
                        }
                    });
                    bt2.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            //TODO 导游从相册选择照片
                        }
                    });
                    bt3.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            pop.dismiss();
                        }
                    });
                }
            });
        } else {
            //TODO  游客界面
            setContentView(R.layout.request_product_tourist);
            num = (AutoCompleteTextView) findViewById(R.id.request_num);
        }
        //共有的...
        endTime = (TextView) findViewById(R.id.request_endtime);
        endTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDateDialogWithResult(endTime, "结束时间");
            }
        });

        startTime = (TextView) findViewById(R.id.request_starttime);
        startTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDateDialogWithResult(startTime, "开始时间");
            }
        });
        description = (AutoCompleteTextView) findViewById(R.id.request_description);
        dest = (AutoCompleteTextView) findViewById(R.id.request_dest);
        apply = (Button) findViewById(R.id.request_apply);
        apply.setOnClickListener(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.slide_right_in, R.anim.slide_right_out);
            }
        });
        ((TextView) findViewById(R.id.toolbar_title)).setText("发布订单");
        container = (CoordinatorLayout) findViewById(R.id.request_container);
    }

    private static final int TAKE_PICTURE = 0x000001;

    /**
     * 拍照
     */
    private void photo() {
        Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(openCameraIntent, TAKE_PICTURE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case TAKE_PICTURE:
                if (resultCode == RESULT_OK) {
                    Bitmap bm = (Bitmap) data.getExtras().get("data");
                    guide_photo.setImageBitmap(bm);
                }
                break;
        }
    }

    /**
     * 顾名思义
     *
     * @param t textView
     * @param notice string
     */
    private void showDateDialogWithResult(final TextView t, String notice) {
        DateChooseWheelViewDialog startDateChooseDialog = new DateChooseWheelViewDialog(ProductRequestActivity.this, new DateChooseWheelViewDialog.DateChooseInterface() {
            @Override
            public void getDateTime(String time, boolean longTimeChecked) {
                t.setText(time);
            }
        });
        startDateChooseDialog.setDateDialogTitle(notice);
        startDateChooseDialog.setTimePickerGone(true);
        startDateChooseDialog.showDateChooseDialog();
    }

    @Override
    public void onClick(View view) {
        Log.e("ProductRequest", "onClick1");
        if (sp.getInt("sign", 2) == 1) {
            if (sp.getInt("auth", 1) == 1) {
                showSnackbar("您还未认证,请先去认证!");
                return;
            }
        }
        Log.e("ProductRequest", "onClick2");
        if (!check())
            return;
        Log.e("ProductRequest", "onClick");
        progressDialog = new ProgressDialog(ProductRequestActivity.this);
        progressDialog.setMsg("发布中,请稍候");
        progressDialog.show();
        JSONObject js = new JSONObject();
        String Path = Utils.getPath();
        if (sp.getInt("sign", 2) == 1) {
            // 导游点击发布
            int guide_id = sp.getInt("id", 0);
            Path = Path + "/pigapp/TourProductServlet";
            try {
                js.put("guide_id", guide_id);
                js.put("dest", dest.getText().toString().trim());
                js.put("tour_name", name.getText().toString().trim());
                js.put("tour_limit", limit.getText().toString().trim());
                js.put("starttime", startTime.getText().toString().trim());
                js.put("endtime", endTime.getText().toString().trim());
                js.put("deadlinetime", deadline.getText().toString().trim());
                js.put("description", description.getText().toString().trim());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            // 游客点击发布
            Path = Path + "/pigapp/TouristRequestServlet";
            int tourist_id = sp.getInt("id", 0);
            try {
                js.put("tourist_id", tourist_id);
                js.put("dest", dest.getText().toString().trim());
                js.put("starttime", startTime.getText().toString().trim());
                js.put("endtime", endTime.getText().toString().trim());
                js.put("description", description.getText().toString().trim());
                js.put("num", num.getText().toString().trim());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        JsonObjectRequest newMissRequest = new JsonObjectRequest(
                Request.Method.POST, Path, js,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Message msg = Message.obtain();
                        try {
                            String s = response.getString("result");
                            if ("success".equals(s)) {
                                msg.what = SUCCESS;
                                msg.obj = response.getInt("id");
                            } else {
                                msg.what = FAIL;
                            }
                            handler.sendMessage(msg);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                handler.sendEmptyMessage(CONNECT_FAIL);
            }
        }
        );
        DemoApplication.getHttpRequestQueue().add(newMissRequest);
    }

    /**
     * 检查是否有未填的资料
     *
     * @return checked
     */
    private boolean check() {
        if (endTime.getText().toString().trim().equals("") ||
                startTime.getText().toString().trim().equals("") ||
                dest.getText().toString().trim().equals("") ||
                description.getText().toString().trim().equals("")) {
            showSnackbar("请完善订单哦~");
            return false;
        }
        if (sp.getInt("sign", 2) == 1) {
            //导游填写不允许空
            if (deadline.getText().toString().trim().equals("") ||
                    limit.getText().toString().trim().equals("") ||
                    name.getText().toString().trim().equals("")) {
                showSnackbar("请完善订单哦~");
                return false;
            }
        } else {
            if (num.getText().toString().trim().equals("")) {
                showSnackbar("请完善订单哦~");
                return false;
            }
        }
        if(endTime.getText().toString().compareTo(startTime.getText().toString()) <= 0){
            showSnackbar("旅行结束时间不对哦~");
            return false;
        }
        return true;
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

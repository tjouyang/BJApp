package cn.edu.hnust.bjapp.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import cn.edu.hnust.bjapp.ProductRequestActivity;
import cn.edu.hnust.bjapp.cptr.recyclerview.RecyclerAdapterWithHF;
import cn.edu.hnust.bjapp.tasks.UpdateGrouperTask;
import cn.edu.hnust.bjapp.tasks.UpdateNoticeTask;
import cn.edu.hnust.bjapp.utils.DemoApplication;
import cn.edu.hnust.bjapp.utils.Utils;
import cn.edu.hnust.bjapp.GroupInfoActivity;
import cn.edu.hnust.bjapp.MainActivity;
import cn.edu.hnust.bjapp.PositionActivity;
import cn.edu.hnust.tbapp.R;
import cn.edu.hnust.bjapp.SignUpActivity;
import cn.edu.hnust.bjapp.adapter.GroupNoticeAdapter;
import cn.edu.hnust.bjapp.adapter.GrouperAdapter;
import cn.edu.hnust.bjapp.cptr.PtrClassicFrameLayout;
import cn.edu.hnust.bjapp.scan.MipcaActivityCapture;

/**
 * Created by tjouyang on 2016/10/12.
 * 团界面
 */

public class GroupFragment extends Fragment {
    public final static int SCANNIN_GREQUEST_CODE = 1;
    SharedPreferences sp;
    AlertDialog dialog;     //导游弹出的对话框
    TextView title;
    CoordinatorLayout container;

    //    PtrClassicFrameLayout ptrClassicFrameLayout;    //上拉加载更多,下拉刷新的layout
//    RecyclerView recyclerView;     //支持actionbar拉伸的"listView"
//    GrouperAdapter grouperAdapter;  //RecyclerView自定义Adapter
//    RecyclerAdapterWithHF recyclerAdapterWithHF; //添加Header和Footer的封装类
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.e("Group", "onCreate");
        sp = getActivity().getSharedPreferences("Config", Context.MODE_PRIVATE);
        if (!sp.getBoolean("group", false)) {
            //未加团
            View v = inflater.inflate(R.layout.fragment_no_add_group, container, false);
            View tv = v.findViewById(R.id.goto_home);
            tv.setBackgroundResource(R.drawable.view_selector);
            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((MainActivity) getActivity()).gotoHome();
                }
            });
            if (sp.getInt("sign", 2) == 2) {
                View scan = v.findViewById(R.id.scan);
                scan.setVisibility(View.VISIBLE);
                scan.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent();
                        intent.setClass(getActivity(), MipcaActivityCapture.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivityForResult(intent, SCANNIN_GREQUEST_CODE);
						getActivity().overridePendingTransition(R.anim.slide_right_in, R.anim.slide_right_out);
                    }
                });
            } else {
                View request = v.findViewById(R.id.request);
                request.setVisibility(View.VISIBLE);
                request.setBackgroundResource(R.drawable.view_selector);
                request.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(getActivity(), ProductRequestActivity.class));
						getActivity().overridePendingTransition(R.anim.slide_right_in, R.anim.slide_right_out);
                    }
                });
            }
            v.findViewById(R.id.test).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (sp.getBoolean("group", false)) {
                        sp.edit().putBoolean("group", false).apply();
                    } else {
                        sp.edit().putBoolean("group", true).apply();
                    }
                    ((MainActivity) getActivity()).refresh();
                }
            });
            return v;
        }
        if (sp.getInt("sign", 2) == 1)          //初始化该页为导游页面
            return inflater.inflate(R.layout.fragment_guide_group, container, false);
        return inflater.inflate(R.layout.fragment_tourist_group, container, false); //初始化该页为游客页面
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (!sp.getBoolean("group", false))
            return;
        if (sp.getInt("sign", 2) == 1) {
            initGuide(view);
        } else {
            initTourist(view);
        }
        title = (TextView) view.findViewById(R.id.fragment_group_title);
        title.setText(sp.getString("group_name", "团名"));
        //导游与游客相同部分,查看团员
        initListView(view);
    }

    /**
     * 初始化ListView
     *
     * @param view the view which is parent
     */
    private void initListView(View view) {
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.fragment_group_rv);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        PtrClassicFrameLayout ptrClassicFrameLayout = (PtrClassicFrameLayout) view.findViewById(R.id.fragment_group_frame);
        GrouperAdapter grouperAdapter = new GrouperAdapter(getActivity());
        RecyclerAdapterWithHF recyclerAdapterWithHF = new RecyclerAdapterWithHF(grouperAdapter);
        recyclerView.setAdapter(recyclerAdapterWithHF);
        ptrClassicFrameLayout.setLastUpdateTimeRelateObject(this);
        UpdateGrouperTask task = new UpdateGrouperTask(recyclerAdapterWithHF, ptrClassicFrameLayout, grouperAdapter, sp.getInt("group_id", -1));
        ptrClassicFrameLayout.setOnLoadMoreListener(task);
        ptrClassicFrameLayout.setPtrHandler(task);
        ptrClassicFrameLayout.autoRefresh();
    }

    private void initGuide(View v) {
        container = (CoordinatorLayout) v.findViewById(R.id.group_container);
        v.findViewById(R.id.fragment_guide_control_pop).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final PopupWindow pop = new PopupWindow(getActivity());
                View view = LayoutInflater.from(getActivity()).inflate(R.layout.pop_guide_control, null);
                pop.setWidth(LinearLayout.LayoutParams.WRAP_CONTENT);
                pop.setHeight(RelativeLayout.LayoutParams.WRAP_CONTENT);
                pop.setBackgroundDrawable(new BitmapDrawable());
                pop.setFocusable(true);
                pop.setOutsideTouchable(true);
                view.findViewById(R.id.fragment_guide_control_finish).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        pop.dismiss();
                        new AlertDialog.Builder(getActivity()).setTitle("提示")
                                .setMessage("确定结束该团?")
                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                        //TODO  导游点击了确定结束游团,此处返回消息到后台
                                        String path = Utils.getPath() + "/pigapp/EndTravelByGuideServlet";
                                        JSONObject js = new JSONObject();
                                        try {
                                            js.put("group_id", sp.getInt("group_id", -1));
                                            js.put("guide_id", sp.getInt("id", -1));
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        DemoApplication.getHttpRequestQueue().add(new JsonObjectRequest(path, js, new Response.Listener<JSONObject>() {
                                            @Override
                                            public void onResponse(JSONObject jsonObject) {
                                                try {
                                                    if ("success".equals(jsonObject.getString("result"))) {
                                                        sp.edit().putBoolean("group", false).apply();
                                                        ((MainActivity) getActivity()).refresh();
                                                    }
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                    Utils.showSnackBar(container, "结束游团失败");
                                                }
                                            }
                                        }, new Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError volleyError) {
                                                Utils.showSnackBar(container, "连接服务器失败");
                                                ((MainActivity)getActivity()).refresh();
                                            }
                                        }));
                                    }
                                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        }).create().show();
//这三条语句可以自定义标题栏                    LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
//                    View mTitleView = layoutInflater.inflate(R.layout., null);
//                    builder.setCustomTitle(mTitleView);
                    }
                });
                view.findViewById(R.id.fragment_guide_control_sign).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //TODO  导游点击了分散活动,开启分散活动控制界面,与签到界面
                        pop.dismiss();
                        startActivity(new Intent(getActivity(), SignUpActivity.class));
						getActivity().overridePendingTransition(R.anim.slide_right_in, R.anim.slide_right_out);
                    }
                });
                view.findViewById(R.id.fragment_guide_control_info).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //TODO 导游点击了游团信息管理
                        pop.dismiss();
                        startActivity(new Intent(getActivity(), GroupInfoActivity.class));
						getActivity().overridePendingTransition(R.anim.slide_right_in, R.anim.slide_right_out);
                    }
                });
                view.findViewById(R.id.fragment_guide_control_send).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //TODO 导游点击了推送消息
                        pop.dismiss();
                        View v = LayoutInflater.from(getActivity()).inflate(R.layout.guide_send_message, null);
                        RecyclerView recyclerView = (RecyclerView) v.findViewById(R.id.guide_send_message_rv);
                        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                        GroupNoticeAdapter groupNoticeAdapter = new GroupNoticeAdapter(getActivity());
                        new UpdateNoticeTask(groupNoticeAdapter).execute();
                        recyclerView.setAdapter(groupNoticeAdapter);
                        final EditText command = (EditText) v.findViewById(R.id.textInputEditText);
                        dialog = new AlertDialog.Builder(getActivity()).setTitle("发送消息").setView(v).setPositiveButton("发送", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //TODO 发送消息
                                String path = Utils.getPath() + "/pigapp/PostMessageByGuideServlet";
                                JSONObject js = new JSONObject();
                                try {
                                    js.put("commands", command.getText().toString());
                                    js.put("group_id", sp.getInt("group_id", -1));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                DemoApplication.getHttpRequestQueue().add(new JsonObjectRequest(path, js, new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject jsonObject) {
                                        try {
                                            if ("success".equals(jsonObject.getString("result"))) {
                                                Utils.showSnackBar(container, "发送消息成功");
                                            } else {
                                                Utils.showSnackBar(container, "发送消息失败");
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                            Utils.showSnackBar(container, "发送消息失败");
                                        }
                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError volleyError) {
                                        Utils.showSnackBar(container, "连接服务器失败");
                                    }
                                }));
                            }
                        }).setNegativeButton("取消", null).create();
                        dialog.show();
                    }
                });
                view.findViewById(R.id.fragment_guide_control_warning).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //TODO 位置警报,调用百度地图接口
                        pop.dismiss();
                        startActivity(new Intent(getActivity(), PositionActivity.class));
						getActivity().overridePendingTransition(R.anim.slide_right_in, R.anim.slide_right_out);
                    }
                });
                pop.setContentView(view);
                pop.showAsDropDown(v);
            }
        });
    }

    private void initTourist(View view) {
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.fragment_group_notice);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        GroupNoticeAdapter groupNoticeAdapter = new GroupNoticeAdapter(getActivity());
        new UpdateNoticeTask(groupNoticeAdapter).execute();
        recyclerView.setAdapter(groupNoticeAdapter);
        view.findViewById(R.id.tourist_sign_up_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), SignUpActivity.class));
				getActivity().overridePendingTransition(R.anim.slide_right_in, R.anim.slide_right_out);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case SCANNIN_GREQUEST_CODE:
                if (resultCode == Activity.RESULT_OK) {
                    Bundle bundle = data.getExtras();
                    //显示扫描到的内容
                    //TODO 查询该团信息,需要一个activity
                    Toast.makeText(getActivity(), bundle.getString("result"), Toast.LENGTH_SHORT).show();
//                    mImageView.setImageBitmap((Bitmap) data.getParcelableExtra("bitmap"));
                }
                break;
        }
    }
}
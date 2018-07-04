package cn.edu.hnust.bjapp.tasks;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cn.edu.hnust.bjapp.cptr.PtrFrameLayout;
import cn.edu.hnust.bjapp.cptr.recyclerview.RecyclerAdapterWithHF;
import cn.edu.hnust.bjapp.utils.DemoApplication;
import cn.edu.hnust.bjapp.utils.Utils;
import cn.edu.hnust.bjapp.adapter.GrouperAdapter;
import cn.edu.hnust.bjapp.cptr.PtrClassicFrameLayout;
import cn.edu.hnust.bjapp.cptr.PtrDefaultHandler;
import cn.edu.hnust.bjapp.cptr.loadmore.OnLoadMoreListener;
import cn.edu.hnust.bjapp.entity.UserEntity;

/**
 * Created by tjouyang on 2016/10/20.
 * 更新grouper
 */

public class UpdateGrouperTask extends PtrDefaultHandler implements OnLoadMoreListener {
    private RecyclerAdapterWithHF adapter;
    private PtrClassicFrameLayout container;
    private GrouperAdapter myAdapter;
    private int group_id;

    public UpdateGrouperTask(RecyclerAdapterWithHF r, PtrClassicFrameLayout p, GrouperAdapter prd, int id) {
        adapter = r;
        container = p;
        myAdapter = prd;
        group_id = id;
    }

    @Override
    public void onRefreshBegin(PtrFrameLayout frame) {
//        Log.e("on", "on");
//        ArrayList<UserEntity> data = new ArrayList<>();
//        UserEntity u = new UserEntity();
//        for(int i = 0; i < 3; i ++){
//            u.setId(i);
//            u.setNickname("测试账号" + i);
//            data.add(u);
//        }
//        myAdapter.update(data);
//        adapter.notifyDataSetChanged();
//        container.refreshComplete();
//        container.setLoadMoreEnable(false);
//        Log.e("on", "finish");
        //TODO 先判断是否有网络连接,若无网络连接,则从缓存加载,若有网络连接则从网络获取,并存入缓存
        if (myAdapter.isConnectNet()) {
            String path = Utils.getPath() + "/pigapp/LoadGroupMemberServlet";
            JSONObject js = new JSONObject();
            try {
                js.put("group_id", group_id);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            JsonObjectRequest request = new JsonObjectRequest(path, js, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jsonObject) {
                    ArrayList<UserEntity> data = new ArrayList<>();
                    UserEntity u = new UserEntity();
                    try {
                        //添加导游
                        u.setNickname(jsonObject.getString("guide_nickname"));
                        u.setUrl(jsonObject.getString("guide_img"));
                        u.setId(jsonObject.getInt("guide_id"));
                        data.add(u);
                        JSONArray jsonArray = jsonObject.getJSONArray("tourists");
                        JSONObject tmp;
                        for (int i = 0; i < jsonArray.length(); i++)
                        {
                            tmp = (JSONObject) jsonArray.get(i);
                            u = u.clone();//克隆一个
                            u.setNickname(tmp.getString("tourist_nickname"));
                            u.setUrl(tmp.getString("tourist_img"));
                            u.setId(tmp.getInt("tourist_id"));
                            u.setDate(tmp.getString("tourist_date"));
                            data.add(u);
                        }
                        myAdapter.update(data);
                        adapter.notifyDataSetChanged();
                        container.refreshComplete();
                        container.setLoadMoreEnable(false);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        for(int i = 0; i < 3; i ++){
                            u = u.clone();
                            u.setId(i);
                            u.setNickname("测试账号"+i);
                            data.add(u);
                        }
                        myAdapter.update(data);
                        adapter.notifyDataSetChanged();
                        container.refreshComplete();
                        container.setLoadMoreEnable(false);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    ArrayList<UserEntity> data = new ArrayList<>();
                    UserEntity u = new UserEntity();
                    for(int i = 0; i < 20; i ++){
                        u = u.clone();
                        u.setId(i);
                        u.setNickname("测试账号"+i);
                        u.setDate("2016-7-7"+"入团");
                        data.add(u);
                    }
                    myAdapter.update(data);
                    adapter.notifyDataSetChanged();
                    container.refreshComplete();
                    container.setLoadMoreEnable(false);
                }
            });
            DemoApplication.getHttpRequestQueue().add(request);
        }
    }

    @Override
    public void loadMore() {
        //TODO loadMore暂时不做
        container.setNoMoreData();
//        ArrayList<UserEntity> data = new ArrayList<>();
//        for (int i = 0; i < 3; i++) {
//            UserEntity p = new UserEntity();
//            p.setNickname("那马");
//            data.add(p);
//        }
//        myAdapter.add(data);
//        adapter.notifyDataSetChanged();
//        container.loadMoreComplete(true);
//        myAdapter.notifyDataSetChanged();
    }

}

package cn.edu.hnust.bjapp.tasks;

/**
 * Created by tjouyang on 2016/11/1.
 * 更新我的订单
 */

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cn.edu.hnust.bjapp.cptr.PtrClassicFrameLayout;
import cn.edu.hnust.bjapp.cptr.PtrFrameLayout;
import cn.edu.hnust.bjapp.cptr.loadmore.OnLoadMoreListener;
import cn.edu.hnust.bjapp.cptr.recyclerview.RecyclerAdapterWithHF;
import cn.edu.hnust.bjapp.entity.ProductEntity;
import cn.edu.hnust.bjapp.utils.DemoApplication;
import cn.edu.hnust.bjapp.utils.Utils;
import cn.edu.hnust.bjapp.adapter.MyProductAdapter;
import cn.edu.hnust.bjapp.cptr.PtrDefaultHandler;

public class UpdateMyProductTask extends PtrDefaultHandler implements OnLoadMoreListener {
    private RecyclerAdapterWithHF adapter;
    private PtrClassicFrameLayout container;
    private MyProductAdapter myAdapter;

    private int number = 0;     //加载更多的标志

    public UpdateMyProductTask(RecyclerAdapterWithHF r, PtrClassicFrameLayout p, MyProductAdapter prd) {
        adapter = r;
        container = p;
        myAdapter = prd;
    }

    @Override
    public void onRefreshBegin(PtrFrameLayout frame) {
        //TODO 先判断是否有网络连接,若无网络连接,则从缓存加载,若有网络连接则从网络获取,并存入缓存
        //如果不是我的订单界面,则调用普通方法!!!!
        //我的订单
        if (myAdapter.getIdentify() == 1) {
            //调用导游接口
            number = 0;         //刷新时把这个控制值重置
            //if (myAdapter.isConnectNet()) {
                //联网了就从网络获取,并存入缓存
                JSONObject js = new JSONObject();
                try {
                    js.put("id", myAdapter.getId());
                    js.put("sign", 1);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String path = Utils.getPath() + "/pigapp/QueryProductAndRequestBuildBySelfServlet";
                JsonObjectRequest request = new JsonObjectRequest(path, js, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        try {
                            JSONArray j = new JSONArray(jsonObject.getString("result"));
                            ArrayList<ProductEntity> data = new ArrayList<>();
                            ProductEntity pe = new ProductEntity();
                            for (int i = 0; i < j.length(); i++) {
                                JSONObject tmp = ((JSONObject) j.get(i));
                                pe = pe.clone();
                                pe.setId(tmp.getInt("product_id"));
                                pe.setDest(tmp.getString("dest"));
                                pe.setDescription(tmp.getString("description"));
                                pe.setNum(tmp.getInt("num"));
                                pe.setDeadlinetime(tmp.getString("deadlinetime"));
                                pe.setImg(tmp.getString("img"));
                                pe.setName(tmp.getString("tour_name"));
                                pe.setLimit(tmp.getInt("tour_limit"));
                                data.add(pe);
                            }
//                            CacheUtils;
                            myAdapter.update(data);
                            adapter.notifyDataSetChanged();
                            container.refreshComplete();
                            container.setLoadMoreEnable(true);
                            if (data.size() == 0)
                                container.setNoMoreData();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            container.refreshComplete();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        container.refreshComplete();
//                            container.setLoadMoreEnable(true);
//                            container.setLoadMoreEnable(false);
//                            container.setNoMoreData();
                    }
                });
                DemoApplication.getHttpRequestQueue().add(request);

            //} else {

            //}
        } else {
            //调用游客接口
            number = 0;         //刷新时把这个控制值重置
            if (myAdapter.isConnectNet()) {
                //联网了就从网络获取,并存入缓存
                JSONObject js = new JSONObject();
                try {
                    js.put("id", myAdapter.getId());
                    js.put("sign", 2);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String path = Utils.getPath() + "/pigapp/QueryProductAndRequestBuildBySelfServlet";
                JsonObjectRequest request = new JsonObjectRequest(path, js, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        try {
                            JSONArray j = new JSONArray(jsonObject.getString("result"));
                            ArrayList<ProductEntity> data = new ArrayList<>();
                            ProductEntity pe = new ProductEntity();
                            for (int i = 0; i < j.length(); i++) {
                                JSONObject tmp = ((JSONObject) j.get(i));
                                pe = pe.clone();
                                pe.setId(tmp.getInt("request_id"));
                                pe.setDest(tmp.getString("dest"));
                                pe.setDescription(tmp.getString("description"));
                                pe.setNum(tmp.getInt("num"));
                                pe.setStarttime(tmp.getString("starttime"));
                                pe.setEndtime(tmp.getString("endtime"));
                                data.add(pe);
                            }
//TODO                            CacheUtils;
                            myAdapter.update(data);
                            adapter.notifyDataSetChanged();
                            container.refreshComplete();
                            container.setLoadMoreEnable(true);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            container.refreshComplete();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        ArrayList<ProductEntity> data = new ArrayList<>();
                        ProductEntity u = new ProductEntity();
                        for(int i = 0; i < 20; i ++){
                            u = u.clone();
                            u.setId(i);
                            data.add(u);
                        }
                        myAdapter.update(data);
                        adapter.notifyDataSetChanged();
                        myAdapter.notifyDataSetChanged();
                        container.refreshComplete();
                        container.setLoadMoreEnable(false);
                    }
                });
                DemoApplication.getHttpRequestQueue().add(request);
            } else {

            }
        }
    }

    @Override
    public void loadMore() {
        //TODO              loadMore
        try {
            container.setNoMoreData();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
//        ArrayList<ProductEntity> data = new ArrayList<>();
//        for (int i = 0; i < 3; i++) {
//            ProductEntity p = new ProductEntity();
//            p.setName("住址");
//            data.add(p);
//        }
//        myAdapter.add(data);
//        adapter.notifyDataSetChanged();
//        container.loadMoreComplete(true);
    }
}


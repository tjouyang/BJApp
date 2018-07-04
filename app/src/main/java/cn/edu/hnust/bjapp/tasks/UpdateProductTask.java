package cn.edu.hnust.bjapp.tasks;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cn.edu.hnust.bjapp.adapter.ProductAdapter;
import cn.edu.hnust.bjapp.cptr.PtrClassicFrameLayout;
import cn.edu.hnust.bjapp.cptr.PtrDefaultHandler;
import cn.edu.hnust.bjapp.cptr.PtrFrameLayout;
import cn.edu.hnust.bjapp.cptr.loadmore.OnLoadMoreListener;
import cn.edu.hnust.bjapp.cptr.recyclerview.RecyclerAdapterWithHF;
import cn.edu.hnust.bjapp.entity.ProductEntity;
import cn.edu.hnust.bjapp.utils.DemoApplication;
import cn.edu.hnust.bjapp.utils.Utils;

/**
 * Created by tjouyang on 2016/10/14.
 * 下拉刷新
 */

public class UpdateProductTask extends PtrDefaultHandler implements OnLoadMoreListener {
    private RecyclerAdapterWithHF adapter;
    private PtrClassicFrameLayout container;
    private ProductAdapter myAdapter;

    private int number = 0;     //加载更多的标志

    public UpdateProductTask(RecyclerAdapterWithHF r, PtrClassicFrameLayout p, ProductAdapter prd) {
        adapter = r;
        container = p;
        myAdapter = prd;
    }

    @Override
    public void onRefreshBegin(PtrFrameLayout frame) {
        //TODO 先判断是否有网络连接,若无网络连接,则从缓存加载,若有网络连接则从网络获取,并存入缓存
        if (myAdapter.getIdentify() == 1) {
            //调用导游接口
            number = 0;         //刷新时把这个控制值重置
            if (myAdapter.isConnectNet()) {
                //联网了就从网络获取,并存入缓存
                JSONObject js = new JSONObject();
                try {
                    js.put("number", number);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String path = Utils.getPath() + "/pigapp/FindRequestServlet";
                JsonObjectRequest request = new JsonObjectRequest(path, js, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        try {
                            number = jsonObject.getInt("number");
                            JSONArray j = new JSONArray(jsonObject.getString("request"));
                            ProductEntity pe = new ProductEntity();
                            ArrayList<ProductEntity> data = new ArrayList<>();
                            for (int i = 0; i < j.length(); i++) {
                                JSONObject tmp = ((JSONObject) j.get(i));
                                pe = pe.clone();
                                pe.setId(tmp.getInt("request_id"));
                                pe.setName(tmp.getString("tourist_nickname"));
                                pe.setDest(tmp.getString("dest"));
                                pe.setDescription(tmp.getString("description"));
                                pe.setImg(tmp.getString("tourist_img"));
                                pe.setNum(tmp.getInt("num"));
                                pe.setStarttime(tmp.getString("starttime"));
                                pe.setEndtime(tmp.getString("endtime"));
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
//                                container.setLoadMoreEnable(true);
//                                container.setLoadMoreEnable(false);
//                                container.setNoMoreData();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        ArrayList<ProductEntity> data = new ArrayList<>();
                        ProductEntity u = new ProductEntity();
                        for (int i = 0; i < 20; i++) {
                            u = u.clone();
                            u.setId(i);
                            data.add(u);
                        }
                        myAdapter.update(data);
                        adapter.notifyDataSetChanged();
                        container.refreshComplete();
                        container.setLoadMoreEnable(false);
                    }
                });
                DemoApplication.getHttpRequestQueue().add(request);

            } else {

            }
        } else {
            //调用游客接口
            number = 0;         //刷新时把这个控制值重置
            if (myAdapter.isConnectNet()) {
                //联网了就从网络获取,并存入缓存
                JSONObject js = new JSONObject();
                try {
                    js.put("number", number);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String path = Utils.getPath() + "/pigapp/FindProductServlet";
                JsonObjectRequest request = new JsonObjectRequest(path, js, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        try {
                            number = jsonObject.getInt("number");
                            JSONArray j = new JSONArray(jsonObject.getString("product"));
                            ArrayList<ProductEntity> data = new ArrayList<>();
                            ProductEntity pe = new ProductEntity();
                            for (int i = 0; i < j.length(); i++) {
                                JSONObject tmp = ((JSONObject) j.get(i));
                                pe = pe.clone();
                                pe.setId(tmp.getInt("product_id"));
                                pe.setName(tmp.getString("tour_name"));
                                pe.setDest(tmp.getString("dest"));
                                pe.setLimit(tmp.getInt("tour_limit"));
                                pe.setDescription(tmp.getString("description"));
                                pe.setImg(tmp.getString("img"));
                                pe.setDeadlinetime(tmp.getString("deadlinetime"));
                                pe.setNum(tmp.getInt("num"));
                                data.add(pe);
                            }
//TODO                            CacheUtils;
                            myAdapter.update(data);
                            adapter.notifyDataSetChanged();
                            container.refreshComplete();
                            container.setLoadMoreEnable(true);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        ArrayList<ProductEntity> data = new ArrayList<>();
                        ProductEntity u = new ProductEntity();
                        for (int i = 0; i < 20; i++) {
                            u = u.clone();
                            u.setId(i);
                            data.add(u);
                        }
                        myAdapter.update(data);
                        adapter.notifyDataSetChanged();
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
        if (myAdapter.getIdentify() == 1) {
            //调用导游接口
            if (myAdapter.isConnectNet()) {
                //联网了就从网络获取,并存入缓存
                JSONObject js = new JSONObject();
                try {
                    js.put("number", number);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String path = Utils.getPath() + "/pigapp/FindRequestServlet";
                JsonObjectRequest request = new JsonObjectRequest(path, js, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        try {
                            number = jsonObject.getInt("number");
                            JSONArray j = new JSONArray(jsonObject.getString("request"));
                            ArrayList<ProductEntity> data = new ArrayList<>();
                            ProductEntity pe = new ProductEntity();
                            for (int i = 0; i < j.length(); i++) {
                                JSONObject tmp = ((JSONObject) j.get(i));
                                pe = pe.clone();
                                pe.setId(tmp.getInt("request_id"));
                                pe.setName(tmp.getString("tourist_nickname"));
                                pe.setDest(tmp.getString("dest"));
                                pe.setDescription(tmp.getString("description"));
                                pe.setImg(tmp.getString("tourist_img"));
                                pe.setNum(tmp.getInt("num"));
                                pe.setStarttime(tmp.getString("starttime"));
                                pe.setEndtime(tmp.getString("endtime"));
                                data.add(pe);
                            }
                            myAdapter.add(data);
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
                        for (int i = 0; i < 20; i++) {
                            u = u.clone();
                            u.setId(i);
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
        } else {
            if (myAdapter.isConnectNet()) {
                //联网了就从网络获取,并存入缓存
                JSONObject js = new JSONObject();
                try {
                    js.put("number", number);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String path = Utils.getPath() + "/pigapp/FindProductServlet";
                JsonObjectRequest request = new JsonObjectRequest(path, js, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        try {
                            number = jsonObject.getInt("number");
                            JSONArray j = new JSONArray(jsonObject.getString("product"));
                            ArrayList<ProductEntity> data = new ArrayList<>();ProductEntity pe = new ProductEntity();
                            for (int i = 0; i < j.length(); i++) {
                                JSONObject tmp = ((JSONObject) j.get(i));
                                pe = pe.clone();
                                pe.setId(tmp.getInt("product_id"));
                                pe.setName(tmp.getString("tour_name"));
                                pe.setDest(tmp.getString("dest"));
                                pe.setLimit(tmp.getInt("tour_limit"));
                                pe.setDescription(tmp.getString("description"));
                                pe.setImg(tmp.getString("img"));
                                pe.setDeadlinetime(tmp.getString("deadlinetime"));
                                pe.setNum(tmp.getInt("num"));
                                data.add(pe);
                            }
//TODO
                            myAdapter.add(data);
                            adapter.notifyDataSetChanged();
                            container.refreshComplete();
                            container.setLoadMoreEnable(true);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        ArrayList<ProductEntity> data = new ArrayList<>();
                        ProductEntity u = new ProductEntity();
                        for (int i = 0; i < 20; i++) {
                            u = u.clone();
                            u.setId(i);
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
    }
}

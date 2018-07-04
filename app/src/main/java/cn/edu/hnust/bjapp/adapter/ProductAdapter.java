package cn.edu.hnust.bjapp.adapter;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.widget.AppCompatAutoCompleteTextView;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.edu.hnust.tbapp.R;
import cn.edu.hnust.bjapp.entity.ProductEntity;
import cn.edu.hnust.bjapp.mytest.ImageLoaderUtil;
import cn.edu.hnust.bjapp.utils.DemoApplication;
import cn.edu.hnust.bjapp.utils.NetworkHelper;
import cn.edu.hnust.bjapp.utils.Utils;

import static cn.edu.hnust.bjapp.utils.Utils.showSnackBar;

/**
 * Created by tjouyang on 2016/10/14.
 * 相当于listView适配,处理数据
 */

public class ProductAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<ProductEntity> mdata;
    private Context context;
    private static int identify;           //1导游,2游客
    private int id;                         //导游或者游客id
    private Boolean auth;

    public ProductAdapter(Context context, int idf, int id, boolean isAuth) {
        this.context = context.getApplicationContext();         //为了避免保持多个context的引用,只使用ApplicationContext
        mdata = new ArrayList<>();
        identify = idf;
        auth = isAuth;
        this.id = id;
        notifyDataSetChanged();
    }

    public int getId() {
        return id;
    }

    public int getIdentify() {
        return identify;
    }

    public boolean isConnectNet() {
        return NetworkHelper.isNetworkConnected(context);
    }

    public void add(List<ProductEntity> data) {
        mdata.addAll(data);
    }

    public void update(List<ProductEntity> data) {
        mdata.clear();
        mdata.addAll(data);
    }

    //自定义的ViewHolder，持有每个Item的的所有界面元素
    private static class NormalViewHolder extends RecyclerView.ViewHolder {
        //游客显示的
        TextView title; //导游标题
        ImageView guide_imageview;  //导游描述图片
        TextView product_description;   //如果是导游的
        TextView deadline;  //截止日期
        View item;
        TextView prise_text;    //星标人数
        ImageButton prise_button; //星标按钮
        //导游显示的
        TextView time;
        TextView dest;
        //共有的
        TextView person;    //游客方:当前人数与最大人数组合,导游方:旅行人数
        ImageView user_header;  //用户头像
        TextView user_nick, id; //用户昵称,订单id
        Button product_button;  //抢单/报名按钮

        NormalViewHolder(View itemView) {
            super(itemView);
            if (identify == 2) {
                //游客方的导游订单控件
                title = (AppCompatAutoCompleteTextView) itemView.findViewById(R.id.product_guide_title);
                guide_imageview = (ImageView) itemView.findViewById(R.id.product_guide_imageView);
                deadline = (TextView) itemView.findViewById(R.id.product_deadline);
                person = (TextView) itemView.findViewById(R.id.product_personWithLimit);
                prise_text = (TextView) itemView.findViewById(R.id.product_guide_priseText);
                prise_button = (ImageButton) itemView.findViewById(R.id.product_guide_priseButton);
            } else {
                //导游控件
                time = (TextView) itemView.findViewById(R.id.product_startAndEndTime);
                user_header = (ImageView) itemView.findViewById(R.id.user_header);
                user_nick = (TextView) itemView.findViewById(R.id.user_nick);
                dest = (TextView) itemView.findViewById(R.id.product_tourist_aim);
                person = (TextView) itemView.findViewById(R.id.product_tourist_num);
            }
            //共有控件
            id = (TextView) itemView.findViewById(R.id.product_id);
            product_description = (TextView) itemView.findViewById(R.id.product_description);
            product_button = (Button) itemView.findViewById(R.id.product_button);
            item = itemView.findViewById(R.id.item_product);
        }
    }

    private static final int NO_DATA = -1;

    /**
     * Return the view type of the item at <code>position</code> for the purposes
     * of view recycling.
     * <p>
     * <p>The default implementation of this method returns 0, making the assumption of
     * a single view type for the adapter. Unlike ListView adapters, types need not
     * be contiguous. Consider using id resources to uniquely identify item view types.
     *
     * @param position position to query
     * @return integer value identifying the type of the view needed to represent the item at
     * <code>position</code>. Type codes need not be contiguous.
     */
    @Override
    public int getItemViewType(int position) {
        if (mdata.size() <= 0) {
            return NO_DATA;
        }
        return super.getItemViewType(position);
    }

    //在该方法中我们创建一个ViewHolder并返回，ViewHolder必须有一个带有View的构造函数，这个View就是我们Item的根布局，在这里我们使用自定义Item的布局；
    @Override
    public NormalViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        if (NO_DATA == viewType) {
            return new NormalViewHolder(inflater.inflate(R.layout.item_no_data, parent, false));
        }
        if (identify == 2) {        //游客
            return new NormalViewHolder(inflater.inflate(R.layout.item_product_guide_listview, parent, false));      //显示导游的订单
        }
        return new NormalViewHolder(inflater.inflate(R.layout.item_product_tourist__listview, parent, false));  //显示游客的订单
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (mdata.size() <= 0)
            return;
        final NormalViewHolder viewHolder = (NormalViewHolder) holder;
        final ProductEntity productEntity = mdata.get(position);
        if (identify == 2) {
            // product游客事件看导游订单
            viewHolder.title.setText(productEntity.getName() + "【" + productEntity.getDest() + "】");
            viewHolder.product_description.setText(productEntity.getDescription());
            if (productEntity.getIsPraised() == 0)
                viewHolder.prise_button.setBackgroundResource(R.mipmap.mine_collection);
            viewHolder.prise_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    viewHolder.prise_button.setBackgroundResource(R.mipmap.collection_checked);
                }
            });
            viewHolder.item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                        showSnackBar(view, "Onclick");
                }
            });
            viewHolder.product_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    // 调用游客报名接口
                    String path = Utils.getPath() + "/pigapp/TouristApplyTourProductServlet";
                    JSONObject js = new JSONObject();
                    try {
                        js.put("product_id", productEntity.getId());
                        js.put("tourist_id", id);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    JsonObjectRequest request = new JsonObjectRequest(path, js, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject jsonObject) {
                            try {
                                String result = jsonObject.getString("result");
                                if ("success".equals(result)) {
                                    showSnackBar(view, "报名成功,等待入团");
                                } else
                                    showSnackBar(view, "你已经报过该团或人数已满!");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            showSnackBar(view, "连接服务器失败!");
                        }
                    });
                    DemoApplication.getHttpRequestQueue().add(request);
                }
            });
            viewHolder.deadline.setText("" + productEntity.getDeadlinetime() + "截止");
            viewHolder.person.setText("" + productEntity.getNum() + "/" + productEntity.getLimit());
            ImageLoaderUtil.setBitmapFromNetwork(productEntity.getImg(), viewHolder.guide_imageview);
        } else {
            //product导游看游客订单
            viewHolder.user_nick.setText(productEntity.getName());
            viewHolder.time.setText(productEntity.getStarttime() + "至" + productEntity.getEndtime());
            viewHolder.dest.setText(productEntity.getDest());
            viewHolder.person.setText("" + productEntity.getNum() + "");
            viewHolder.product_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    // 调用导游接单接口
                    if (!auth) {
                        showSnackBar(view, "您还未认证,请先认证!");
                        return;
                    }
                    String path = Utils.getPath() + "/pigapp/GuideReceiveTheTouristRequestServlet";
                    JSONObject js = new JSONObject();
                    try {
                        js.put("request_id", productEntity.getId());
                        js.put("guide_id", id);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    JsonObjectRequest request = new JsonObjectRequest(path, js, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject jsonObject) {
                            try {
                                String result = jsonObject.getString("result");
                                if ("success".equals(result)) {
                                    showSnackBar(view, "接单成功,等待游客同意");
                                } else
                                    showSnackBar(view, "不能重复接单!");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            showSnackBar(view, "连接服务器失败!");
                        }
                    });
                    DemoApplication.getHttpRequestQueue().add(request);
                }
            });
            viewHolder.item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                }
            });
            viewHolder.product_description.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //弹出描述
                    final PopupWindow pop = new PopupWindow(context);
                    View v = LayoutInflater.from(context).inflate(R.layout.pop_description, null);
                    ((TextView) v.findViewById(R.id.all_description)).setText("" + productEntity.getDescription() + "");
                    pop.setWidth(LinearLayout.LayoutParams.WRAP_CONTENT);
                    pop.setHeight(RelativeLayout.LayoutParams.WRAP_CONTENT);
                    pop.setBackgroundDrawable(new BitmapDrawable());
                    pop.setFocusable(true);
                    pop.setOutsideTouchable(true);
                    pop.setContentView(v);
                    pop.showAtLocation(view, Gravity.CENTER, 0, 0);
                }
            });
        }
        //共有事件
        viewHolder.id.setText("" + productEntity.getId() + "");
//            viewHolder.product_button.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
////                        final PopupWindow pop = new PopupWindow(context);
////                        View v = LayoutInflater.from(context).inflate(R.layout.pop_product_info, null);
////                        ((TextView)v.findViewById(R.id.pop_product_text)).setText("");
////                        pop.setWidth(LinearLayout.LayoutParams.WRAP_CONTENT);
////                        pop.setHeight(RelativeLayout.LayoutParams.WRAP_CONTENT);
////                        pop.setBackgroundDrawable(new BitmapDrawable());
////                        pop.setFocusable(true);
////                        pop.setOutsideTouchable(true);
////                        pop.setContentView(v);
////                        pop.showAtLocation(view, Gravity.CENTER, 0, 0);
//
//                    }
//                }
//            });
    }

    //获取数据的数量,即使为0,也要数据
    @Override
    public int getItemCount() {
        return mdata.size() > 0 ? mdata.size() : 1;
    }

}


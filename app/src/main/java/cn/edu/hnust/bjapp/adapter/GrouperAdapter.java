package cn.edu.hnust.bjapp.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.edu.hnust.bjapp.entity.UserEntity;
import cn.edu.hnust.bjapp.mytest.ImageLoaderUtil;
import cn.edu.hnust.bjapp.utils.NetworkHelper;
import cn.edu.hnust.tbapp.R;

/**
 * Created by tjouyang on 2016/10/20.
 * 团员listView,和身份无关
 */

public class GrouperAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private LayoutInflater inflater;
    private List<UserEntity> mdata;
    private Context context;

    public GrouperAdapter(Context context) {
        this.context = context.getApplicationContext();
        inflater = LayoutInflater.from(this.context);
        mdata = new ArrayList<>();
    }

    public boolean isConnectNet() {
        return NetworkHelper.isNetworkConnected(context);
    }

    public void add(List<UserEntity> data) {
        mdata.addAll(data);
    }

    public void update(List<UserEntity> data) {
        mdata.clear();
        mdata.addAll(data);
    }

    private static class NormalViewHolder extends RecyclerView.ViewHolder {
        private TextView user_id, user_nick, user_date;
        ImageView user_header;
        View grouper;

        NormalViewHolder(View itemView) {
            super(itemView);
            user_header = (ImageView) itemView.findViewById(R.id.user_header);
            user_id = (TextView) itemView.findViewById(R.id.user_id);
            user_nick = (TextView) itemView.findViewById(R.id.user_nick);
            user_date = (TextView) itemView.findViewById(R.id.user_date);
            grouper = itemView.findViewById(R.id.grouper);
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

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (NO_DATA == viewType) {
            return new NormalViewHolder(inflater.inflate(R.layout.item_no_data, parent, false));
        }
        return new NormalViewHolder(inflater.inflate(R.layout.item_group, parent, false));
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if(mdata.size() <= 0){
            return;
        }
        final NormalViewHolder viewHolder = (NormalViewHolder) holder;
        final UserEntity temp = mdata.get(position);
        viewHolder.user_nick.setText(temp.getNickname());
        viewHolder.user_id.setText("" + temp.getId() + "");
        if(temp.getDate() == null){
            //viewHolder.user_date.setVisibility(View.GONE);
            viewHolder.user_date.setText("" + "2016-6-6" +"");
        } else {
            viewHolder.user_date.setText(temp.getDate());
        }
        ImageLoaderUtil.setBitmapFromNetwork(mdata.get(position).getUrl(), viewHolder.user_header);
		viewHolder.grouper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return mdata.size() > 0 ? mdata.size() : 1;
    }

}

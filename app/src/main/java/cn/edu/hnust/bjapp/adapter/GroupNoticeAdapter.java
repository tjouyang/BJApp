package cn.edu.hnust.bjapp.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.edu.hnust.bjapp.utils.NetworkHelper;
import cn.edu.hnust.tbapp.R;

/**
 * Created by tjouyang on 2016/10/20.
 * 团通知适配
 */

public class GroupNoticeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private LayoutInflater inflater;
    private List<String> mdata;
    private Context context;

    public GroupNoticeAdapter(Context context) {
        this.context = context.getApplicationContext();
        inflater = LayoutInflater.from(this.context);
        mdata = new ArrayList<>();
    }

    public boolean isConnectNet(){return NetworkHelper.isNetworkConnected(context);}

    public void add(List<String> data) {
        mdata.addAll(data);
    }

    public void update(List<String> data) {
        mdata.clear();
        mdata.addAll(data);
    }

    private static class NormalViewHolder extends RecyclerView.ViewHolder {
        TextView notice,time;

        NormalViewHolder(View itemView) {
            super(itemView);
            notice = (TextView) itemView.findViewById(R.id.notice_item_text);
            time = (TextView) itemView.findViewById(R.id.notice_item_time);
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
        return new NormalViewHolder(inflater.inflate(R.layout.item_notice, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(mdata.size() <= 0)
            return;
        NormalViewHolder viewHolder = (NormalViewHolder) holder;
        String tmp[];
        tmp = mdata.get(position).split("commands_time");
        viewHolder.notice.setText(tmp[0]);
        viewHolder.time.setText("导游发布于" + tmp[1] + "");     //.substring(0, 19)
    }

    @Override
    public int getItemCount() {
        return mdata.size() > 0 ? mdata.size() : 1;
    }
}
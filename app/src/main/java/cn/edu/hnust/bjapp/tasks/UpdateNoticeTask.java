package cn.edu.hnust.bjapp.tasks;

import android.os.AsyncTask;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cn.edu.hnust.bjapp.adapter.GroupNoticeAdapter;
import cn.edu.hnust.bjapp.utils.DemoApplication;
import cn.edu.hnust.bjapp.utils.Utils;

/**
 * Created by tjouyang on 2016/10/21.
 *
 */

public class UpdateNoticeTask extends AsyncTask<Void, Void, Void> {
    private GroupNoticeAdapter adapter;
    public UpdateNoticeTask(GroupNoticeAdapter adapter){
        this.adapter = adapter;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
    }

    @Override
    protected Void doInBackground(Void... voids) {
        String path = Utils.getPath() + "/pigapp/LoadGroupMessageServlet";
        int group_id = 1;
        JSONObject js = new JSONObject();
        try {
            js.put("group_id", group_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(path, js, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                JSONArray jsonArray = new JSONArray();
                ArrayList<String> data = new ArrayList<>();
                try {
                    jsonArray = jsonObject.getJSONArray("content");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String tmp;
                for (int i = jsonArray.length() - 1; i >= 0 ; i--){
                    try {
                        tmp =  jsonArray.getJSONObject(i).getString("commands") + "commands_time" + jsonArray.getJSONObject(i).getString("time");
                        data.add(tmp);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                adapter.update(data);
                adapter.notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                ArrayList<String> data = new ArrayList<>();
                for(int i = 0; i < 20; i ++){
                    data.add("" + i + "commands_time" + " ");
                }
                adapter.update(data);
                adapter.notifyDataSetChanged();
            }
        });
        DemoApplication.getHttpRequestQueue().add(jsonObjectRequest);
        return  null;
    }
}

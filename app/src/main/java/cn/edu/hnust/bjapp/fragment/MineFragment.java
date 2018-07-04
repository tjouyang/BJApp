package cn.edu.hnust.bjapp.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


import org.json.JSONException;

import cn.edu.hnust.bjapp.IDActivity;
import cn.edu.hnust.bjapp.LoginActivity;
import cn.edu.hnust.bjapp.MyFootActivity;
import cn.edu.hnust.bjapp.MyProductActivity;
import cn.edu.hnust.tbapp.R;
import cn.edu.hnust.bjapp.SettingActivity;
import cn.edu.hnust.bjapp.UserInfoActivity;
import cn.edu.hnust.bjapp.cache.CacheUtils;
import cn.edu.hnust.bjapp.mytest.ImageLoaderUtil;
import cn.edu.hnust.bjapp.ui.ImageButtonWithText;

/**
 * Created by tjouyang on 2016/10/12.
 * 我的界面
 */

public class MineFragment extends Fragment {
    SharedPreferences sp;
    ImageButtonWithText imageView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_mine, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sp = getActivity().getSharedPreferences("Config", Context.MODE_PRIVATE);
        imageView = (ImageButtonWithText) view.findViewById(R.id.user_header);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //TODO 从后台请求用户头像地址
                try {
                    ImageLoaderUtil.setBitmapFromNetwork(CacheUtils.getUserInfo().getString("img"), imageView.getImageViewbutton());
                    imageView.getTextView().setText(CacheUtils.getUserInfo().getString("nickname"));
                } catch (JSONException | NullPointerException e) {
                    e.printStackTrace();
                }
            }
        }, 1000);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                Activity activity = getActivity();
                intent.setClass(activity, UserInfoActivity.class);
                startActivity(intent);
                activity.overridePendingTransition(R.anim.slide_right_in, R.anim.slide_right_out);
            }
        });
        Button button = (Button) view.findViewById(R.id.button_exit_login);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sp.edit().putBoolean("isLogin", false).apply();
                Activity activity = getActivity();
                startActivity(new Intent(activity, LoginActivity.class));
                activity.finish();
				activity.overridePendingTransition(R.anim.slide_right_in, R.anim.slide_right_in);
            }
        });
        view.findViewById(R.id.setting).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO 设置界面,流量下不加载图片,检查更新,关于软件,意见反馈,清除缓存,帮助?
                Activity activity = getActivity();
                startActivity(new Intent(activity, SettingActivity.class));
                activity.overridePendingTransition(R.anim.slide_right_in, R.anim.slide_right_out);
            }
        });
        view.findViewById(R.id.my_product).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //我的订单模块
                Activity activity = getActivity();
                startActivity(new Intent(activity, MyProductActivity.class));
                activity.overridePendingTransition(R.anim.slide_right_in, R.anim.slide_right_out);
            }
        });
        view.findViewById(R.id.auth).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //认证界面
                Activity activity = getActivity();
                startActivity(new Intent(activity, IDActivity.class));
                activity.overridePendingTransition(R.anim.slide_right_in, R.anim.slide_right_out);
            }
        });
        view.findViewById(R.id.my_foot).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //我的足迹
                Activity activity = getActivity();
                startActivity(new Intent(activity, MyFootActivity.class));
                activity.overridePendingTransition(R.anim.slide_right_in, R.anim.slide_right_out);
            }
        });
    }


}

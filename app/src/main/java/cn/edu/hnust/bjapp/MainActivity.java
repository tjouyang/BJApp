package cn.edu.hnust.bjapp;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import cn.edu.hnust.bjapp.fragment.GroupFragment;
import cn.edu.hnust.bjapp.fragment.MineFragment;
import cn.edu.hnust.bjapp.service.QueryService;
import cn.edu.hnust.bjapp.ui.MyViewPager;
import cn.edu.hnust.bjapp.utils.NetworkHelper;
import cn.edu.hnust.bjapp.utils.Utils;
import cn.edu.hnust.tbapp.R;
import cn.edu.hnust.bjapp.fragment.HomeFragment;
import cn.edu.hnust.bjapp.mytest.ImageLoaderUtil;

/**
 * Created by tjouyang on 2016/10/8.
 * main activity
 */
public class MainActivity extends AppCompatActivity {
    private QueryService.MyBinder myBinder;
    private MyConn myConn;
    MyViewPager myViewPager;    //自定义的viewpager
    MyAdapter myAdapter;    //自定义viewpager的adapter
    //    ArrayList<Fragment> list; // 作为切换fragment用的数组
    ImageView ivs[];    //底部三个图片
    TextView tvs[];     //底部三个文字
    Drawable drawables[];   //底部三个按钮的drawable,保存下来用于换颜色
    LinearLayout lls[];     //底部三个布局,有的手机点击不灵敏,用这个实现
    ArrayList<Fragment> fragments;      //viewPager三页
    CoordinatorLayout container;        //提示的container
    BroadcastReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.e("Receiver", intent.getAction());                    //收到自定义广播根据情况处理
                if (intent.getBooleanExtra("isRefresh", false)) {
                    refresh();
                } else {
                    if (myBinder != null){
                        myBinder.callMethodInService();
                    } else {
                        if (myConn == null) {
                            myConn = new MyConn();
                        }
                        bindService(new Intent(MainActivity.this, QueryService.class), myConn, BIND_AUTO_CREATE);
                    }
                }
            }
        };

        IntentFilter intentFilter = new IntentFilter("tj.ouyang.action");
        registerReceiver(receiver, intentFilter);
        if (myConn == null) {
            Log.e("myConn", "null");
            myConn = new MyConn();
        }
        bindService(new Intent(MainActivity.this, QueryService.class), myConn, BIND_AUTO_CREATE);
        //流量下不加载图片
        if (NetworkHelper.GetNetype(this) != NetworkHelper.NETTYPE_WIFI) {
            ImageLoaderUtil.setLoadEnable(getSharedPreferences("Config", MODE_PRIVATE).getBoolean("load_enable", false));
        }
        container = (CoordinatorLayout) MainActivity.this.findViewById(R.id.main_container);
        initViewPager();
//        rootView = getWindow().getDecorView().findViewById(android.R.id.content);//查找根视图的语句
//        startService(new Intent(this, QueryInformationService.class));
        //注册广播收集信息

    }

    @Override
    protected void onPause() {
        if (drawables != null && ivs != null && tvs != null) {
            for (int i = 0; i < 3; i++) {
                ivs[i].setImageDrawable(Utils.tintDrawable(drawables[i], ColorStateList.valueOf(Color.GRAY)));
                tvs[i].setTextColor(Color.GRAY);
            }
        }
        super.onPause();
    }

    private void initViewPager() {
        myViewPager = (MyViewPager) findViewById(R.id.main_vp);
        //关闭预加载，默认一次只加载一个Fragment
//        myViewPager.setOffscreenPageLimit(2);
        ivs = new ImageView[3];
        tvs = new TextView[3];
        lls = new LinearLayout[3];
        ivs[0] = (ImageView) findViewById(R.id.main_ib0);
        ivs[1] = (ImageView) findViewById(R.id.main_ib1);
        ivs[2] = (ImageView) findViewById(R.id.main_ib2);
        tvs[0] = (TextView) findViewById(R.id.main_tv0);
        tvs[1] = (TextView) findViewById(R.id.main_tv1);
        tvs[2] = (TextView) findViewById(R.id.main_tv2);
        lls[0] = (LinearLayout) findViewById(R.id.main_ll_0);
        lls[1] = (LinearLayout) findViewById(R.id.main_ll_1);
        lls[2] = (LinearLayout) findViewById(R.id.main_ll_2);
        drawables = new Drawable[3];
        for (int i = 0; i < 3; i++) {
            drawables[i] = ivs[i].getDrawable();
        }
        ivs[0].setImageDrawable(Utils.tintDrawable(drawables[0], ColorStateList.valueOf(Color.BLUE)));
        tvs[0].setTextColor(Color.BLUE);
        fragments = new ArrayList<>();
        fragments.add(new HomeFragment());
        fragments.add(new GroupFragment());
        fragments.add(new MineFragment());
        myAdapter = new MyAdapter(this.getSupportFragmentManager(), fragments);
        myViewPager.setAdapter(myAdapter);
        myViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int arg0) {
                for (int i = 0; i < ivs.length; i++) {
                    if (i == arg0) {
                        ivs[i].setImageDrawable(Utils.tintDrawable(drawables[i],
                                ColorStateList.valueOf(Color.BLUE)));
                        tvs[i].setTextColor(Color.BLUE);
                    } else {
                        ivs[i].setImageDrawable(Utils.tintDrawable(drawables[i],
                                ColorStateList.valueOf(Color.GRAY)));
                        tvs[i].setTextColor(Color.GRAY);
                    }
                }
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm.isActive()) {
                    imm.hideSoftInputFromWindow(myViewPager.getWindowToken(), 0); //强制隐藏键盘
                }
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {

            }
        });
        lls[0].setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                myViewPager.setCurrentItem(0, false);
            }
        });
        lls[1].setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                myViewPager.setCurrentItem(1, false);
            }
        });
        lls[2].setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                myViewPager.setCurrentItem(2, false);
            }
        });
    }

    private class MyAdapter extends FragmentStatePagerAdapter {
        ArrayList<Fragment> list = new ArrayList<>();

        MyAdapter(FragmentManager fm, ArrayList<Fragment> fragments) {
            super(fm);
            list = fragments;
            notifyDataSetChanged();
        }

        public void setData(ArrayList<Fragment> data) {
            list = data;
            notifyDataSetChanged();
        }

        @Override
        public int getItemPosition(Object object) {
            return PagerAdapter.POSITION_NONE;
        }

        @Override
        public Fragment getItem(int arg0) {
            return list.get(arg0);
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            super.destroyItem(container, position, object);
        }
    }

    public void refresh() {
        Log.e("Main", "onRefresh");
        int oldPosition = myViewPager.getCurrentItem();
        //所有碎片重新加载
        fragments.clear();
        fragments.add(new HomeFragment());
        fragments.add(new GroupFragment());
        fragments.add(new MineFragment());
        myAdapter.setData(fragments);
        myViewPager.setCurrentItem(oldPosition);
        System.gc();//通知回收垃圾
    }

    public void gotoHome() {
        myViewPager.setCurrentItem(0, false);
    }

    @Override
    protected void onDestroy() {
        if (myConn != null)
            unbindService(myConn);
        unregisterReceiver(receiver);
        super.onDestroy();
    }

    private class MyConn implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Log.e("MyConn", "onServiceConnected");
            myBinder = (QueryService.MyBinder) iBinder;
            myBinder.callMethodInService();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    }

}

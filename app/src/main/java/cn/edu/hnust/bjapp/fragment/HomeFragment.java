package cn.edu.hnust.bjapp.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import cn.edu.hnust.bjapp.ProductRequestActivity;
import cn.edu.hnust.bjapp.adapter.ProductAdapter;
import cn.edu.hnust.bjapp.cptr.PtrClassicFrameLayout;
import cn.edu.hnust.bjapp.cptr.recyclerview.RecyclerAdapterWithHF;
import cn.edu.hnust.bjapp.tasks.UpdateProductTask;
import cn.edu.hnust.tbapp.R;

/**
 * Created by tjouyang on 2016/10/11.
 * 主界面
 */

public class HomeFragment extends Fragment {
    //    private int[] imageResId;   //图片广告的ID
//    private List<View> dots;    //图片标题正文的点
//    private List<ImageView> imageViews; //滑动图片合集
//    ViewPager viewPager;    //控制图片滑动的viewPager
//    private int currentItem = 0;    //当前图片索引号
//    private ScheduledExecutorService scheduledExecutorService;  //控制自动滑动的服务
    PtrClassicFrameLayout ptrClassicFrameLayout;    //上拉加载更多,下拉刷新的layout
    RecyclerView recyclerView;     //支持actionbar拉伸的"listView"
    ProductAdapter productAdapter;  //RecyclerView自定义Adapter
    RecyclerAdapterWithHF recyclerAdapterWithHF; //添加Header和Footer的封装类
    Button searchBt;    //搜索按钮
    private SharedPreferences sp;
    private EditText searchEt;  //搜索框

//    private Handler handler = new Handler(){
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            viewPager.setCurrentItem(currentItem);
//        }
//    };

    @Override
    public void onPause() {
        super.onPause();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        initViewPager(view);
        sp = getActivity().getSharedPreferences("Config", Context.MODE_PRIVATE);
        initListView(view);
        searchBt = (Button) view.findViewById(R.id.fragment_home_searchButton);
        searchEt = (EditText) view.findViewById(R.id.fragment_home_searchText);
        searchBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO search
                searchEt.getText();
            }
        });
        view.findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), ProductRequestActivity.class));
				getActivity().overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
            }
        });
    }

    /**
     * 初始化ListView
     *
     * @param view the view which is parent
     */
    private void initListView(View view) {
        recyclerView = (RecyclerView) view.findViewById(R.id.fragment_home_rvList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        ptrClassicFrameLayout = (PtrClassicFrameLayout) view.findViewById(R.id.fragment_home_frame);
        productAdapter = new ProductAdapter(getActivity(), sp.getInt("sign", 2), sp.getInt("id", -1), sp.getInt("auth", 1) != 1);
        recyclerAdapterWithHF = new RecyclerAdapterWithHF(productAdapter);
        recyclerView.setAdapter(recyclerAdapterWithHF);
        ptrClassicFrameLayout.setLastUpdateTimeRelateObject(this);
        UpdateProductTask task = new UpdateProductTask(recyclerAdapterWithHF, ptrClassicFrameLayout, productAdapter);
        ptrClassicFrameLayout.setOnLoadMoreListener(task);
        ptrClassicFrameLayout.setPtrHandler(task);
        ptrClassicFrameLayout.autoRefresh();
    }

    //    /**
//     * 初始化ViewPager
//     * @param view the view which is parent
//     */
//    private void initViewPager(View view){
//        imageResId = new int[]{R.mipmap.ic,R.mipmap.back,
//                R.mipmap.ic_launcher,R.mipmap.bg_login,R.mipmap.bg_loading};
//        imageViews = new ArrayList<>();
//        // 初始化图片资源
//        for (int i = 0; i < imageResId.length; i++) {
//            ImageView imageView = new ImageView(getContext());
//            imageView.setImageResource(imageResId[i]);
//            imageView.setPadding(1, 1, 1, 1);
//            imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
//            imageViews.add(imageView);
//        }
//        dots = new ArrayList<>();
//        dots.add(view.findViewById(R.id.fragment_home_dot0));
//        dots.add(view.findViewById(R.id.fragment_home_dot1));
//        dots.add(view.findViewById(R.id.fragment_home_dot2));
//        dots.add(view.findViewById(R.id.fragment_home_dot3));
//        dots.add(view.findViewById(R.id.fragment_home_dot4));
//        viewPager = (ViewPager) view.findViewById(R.id.fragment_home_viewpager);
//        viewPager.setAdapter(new ViewPagerAdapter());// 设置填充ViewPager页面的适配器
//        // 设置一个监听器，当ViewPager中的页面改变时调用
//        viewPager.addOnPageChangeListener(new ViewPageChangeListener());
//    }
    @Override
    public void onStart() {
//        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
//        // 当Activity显示出来后，每两秒钟切换一次图片显示
//        scheduledExecutorService.scheduleAtFixedRate(new ScrollTask(), 1, 2, TimeUnit.SECONDS);
        super.onStart();
    }

    @Override
    public void onStop() {
        // 当Activity不可见的时候停止切换
//        scheduledExecutorService.shutdown();
        super.onStop();
    }

//    /**
//     * listView加载更多回调接口
//     */
//    @Override
//    public void onLoad() {
//        LoadProductTask task = new LoadProductTask();
//        task.execute((Void)null);
//    }
//
//    /**
//     * 刷新listView回调接口
//     */
//    @Override
//    public void onUpdate() {
//        UpdateProductTask task = new UpdateProductTask();
//        task.execute((Void)null);
//    }

//    /**
//     * 换行切换任务
//     */
//    private class ScrollTask implements Runnable {
//
//        public void run() {
//            synchronized (new Object()) {
//                currentItem = (currentItem + 1) % imageViews.size();
//                handler.obtainMessage().sendToTarget(); // 通过Handler切换图片
//            }
//        }
//    }
//    /**
//     * 填充图片ViewPager页面的适配器
//     *
//     */
//    private class ViewPagerAdapter extends PagerAdapter {
//
//        @Override
//        public int getCount() {
//            return imageResId.length;
//        }
//
//        @Override
//        public Object instantiateItem(View arg0, int arg1) {
//            ((ViewPager) arg0).addView(imageViews.get(arg1));
//            return imageViews.get(arg1);
//        }
//
//        @Override
//        public void destroyItem(View arg0, int arg1, Object arg2) {
//            ((ViewPager) arg0).removeView((View) arg2);
//        }
//
//        @Override
//        public boolean isViewFromObject(View arg0, Object arg1) {
//            return arg0 == arg1;
//        }
//
//        @Override
//        public void restoreState(Parcelable arg0, ClassLoader arg1) {
//
//        }
//
//        @Override
//        public Parcelable saveState() {
//            return null;
//        }
//    }
//    /**
//     * 当图片ViewPager中页面的状态发生改变时调用
//     *
//     */
//    private class ViewPageChangeListener implements ViewPager.OnPageChangeListener {
//        private int oldPosition = 0;
//
//        /**
//         * This method will be invoked when a new page becomes selected.
//         * position: Position index of the new selected page.
//         */
//        public void onPageSelected(int position) {
//            currentItem = position;
//            dots.get(oldPosition).setBackgroundResource(R.drawable.dot_normal);
//            dots.get(position).setBackgroundResource(R.drawable.dot_focused);
//            oldPosition = position;
//        }
//
//        public void onPageScrollStateChanged(int arg0) {
//
//        }
//
//        public void onPageScrolled(int arg0, float arg1, int arg2) {
//
//        }
//    }
}

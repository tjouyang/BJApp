package cn.edu.hnust.bjapp.mytest;

import android.graphics.Bitmap;
import android.widget.ImageView;

import com.android.volley.toolbox.ImageLoader;

import cn.edu.hnust.bjapp.cache.ACache;
import cn.edu.hnust.bjapp.utils.DemoApplication;
import cn.edu.hnust.tbapp.R;

/**
 * Created by tjouyang on 2016/10/20.
 * 测试从网络加载图片
 */

public class ImageLoaderUtil {
    private static boolean loadEnable = true;

    public ImageLoaderUtil() {

    }

    public static void setLoadEnable(boolean enable) {
        loadEnable = enable;
    }

    public static void setBitmapFromNetwork(String path, ImageView imageView) {
        if (loadEnable) {
            ImageLoader loader = new ImageLoader(DemoApplication.getHttpRequestQueue(), new BitmapCash());
            ImageLoader.ImageListener listener = ImageLoader.getImageListener(imageView, R.mipmap.ic_launcher, R.mipmap.ic);
            try {
                loader.get(path, listener);
            } catch (NullPointerException e) {
                e.printStackTrace();
                imageView.setImageResource(R.mipmap.ic);
            }
            return;
        }
        //不加载图片则先判断缓存有没有图片
        Bitmap bitmap = DemoApplication.getACache().getAsBitmap(path);
        if (bitmap == null) {
            imageView.setImageResource(R.mipmap.ic);
        } else {
            imageView.setImageBitmap(bitmap);
        }
    }

    private static class BitmapCash implements ImageLoader.ImageCache {
        ACache aCache;

        BitmapCash() {
            aCache = DemoApplication.getACache();
        }

        //这里会获取到图片
        @Override
        public Bitmap getBitmap(String url) {
            return aCache.getAsBitmap(url);
        }

        @Override
        public void putBitmap(String url, Bitmap bitmap) {
            aCache.put(url, bitmap);
        }
    }
}

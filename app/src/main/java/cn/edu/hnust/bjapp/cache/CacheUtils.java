package cn.edu.hnust.bjapp.cache;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.util.List;

import cn.edu.hnust.bjapp.entity.ProductEntity;
import cn.edu.hnust.bjapp.utils.DemoApplication;

/**
 * Created by tjouyang on 2016/10/12.
 * 缓存工具类
 */

public class CacheUtils {
    private static ACache aCache;
    private static final String product = "ProductList";
    private static final String userInfo = "userInfo";

    /**
     * 把product存入缓存
     *
     * @param list list
     */
    public static void putProductCache(List<ProductEntity> list) {
        if (aCache == null)
            aCache = DemoApplication.getACache();
        Gson gson = new Gson();
        aCache.put(product, gson.toJson(list));
    }

    /**
     * 获取缓存中product数据
     *
     * @return  list
     */
    public static List<ProductEntity> getProductCache() {
        if (aCache == null)
            aCache = DemoApplication.getACache();
        String cacheData = aCache.getAsString(product);
        Gson gson = new Gson();
        return gson.fromJson(cacheData,
                new TypeToken<List<ProductEntity>>() {
                }.getType());
    }

    /**
     * 清除product缓存
     */
    public static void removeProductCache() {
        if (aCache == null)
            aCache = DemoApplication.getACache();
        aCache.remove(product);
    }

    public static void putUserInfo(JSONObject object) {
        if (aCache == null)
            aCache = DemoApplication.getACache();
        aCache.put(userInfo, object);
    }

    public static JSONObject getUserInfo() {
        if (aCache == null)
            aCache = DemoApplication.getACache();
        return aCache.getAsJSONObject(userInfo);
    }

    public static void removeUserInfo() {
        if (aCache == null)
            aCache = DemoApplication.getACache();
        aCache.remove(userInfo);
    }

    public static void cleanAll(){
        if (aCache == null)
            aCache = DemoApplication.getACache();
        aCache.clear();
    }
}

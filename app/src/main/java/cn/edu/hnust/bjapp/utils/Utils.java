package cn.edu.hnust.bjapp.utils;

/**
 * Created by tjouyang on 2016/10/8.
 * 工具类
 */


import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.design.widget.Snackbar;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.view.View;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.edu.hnust.tbapp.R;

public class Utils {

    /**
     * 获取url地址
     *
     * @return the url
     */
    public static String getPath() {
//        Properties prop = new Properties();// 属性集合对象
//        String Path = "";
//        try {
//            prop.load(Utils.class
//                    .getResourceAsStream("/assets/address.properties"));
//            Path = prop.getProperty("address");
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        return "http://119.29.160.141:8080";
    }
//
//    public static ArrayList<String> getMenu() {
//        ArrayList<String> menuLists = null;
//        menuLists = new ArrayList<String>(); // 菜单
//        menuLists.add("游团");
//        menuLists.add("旅游须知");
//        menuLists.add("车票查询");
//        menuLists.add("旅游达人");
//        menuLists.add("旅游分享");
//        menuLists.add("我的足迹");
//        menuLists.add("我的游团");
//        menuLists.add("我的收藏");
//        menuLists.add("系统设置");
//        return menuLists;
//    }
//
//    public static int[] getMenuIcon() {
//        int MenuIcon[] = {};
//        return MenuIcon;
//    }

//    /**
//     * 获取ddpush服务器IP
//     *
//     * @return 服务器IP
//     */
//    public static String getServerIp() {
//        Properties prop = new Properties();// 属性集合对象
//        String IP = "";
//        try {
//            prop.load(Utils.class
//                    .getResourceAsStream("/assets/address.properties"));
//            IP = prop.getProperty("serverip");
//            Log.e("100", "----IP---" + IP);
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        return "121.201.8.196";
//    }

//    /**
//     * 将定位结果写入文件
//     *
//     * @param str1
//     */
//    @SuppressLint("SimpleDateFormat")
//    public static void file(String str1, String filename) {
//
//        try {
//            // 判断是否存在SD卡
//            if (Environment.getExternalStorageState().equals(
//                    Environment.MEDIA_MOUNTED)) {
//
//                String str = "写入时间："
//                        + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
//                        .format(new Date(System.currentTimeMillis()));
//                // 获取SD卡的目录
//                File file = new File(Environment.getExternalStorageDirectory()
//                        + File.separator + filename);
//                OutputStream fileW = new FileOutputStream(file, true);
//                fileW.write((str + "\n").getBytes());
//                fileW.write((str1 + "\n\n").getBytes());
//                fileW.close();
//
//            } else {
//
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    /**
     * 设置Snack样式
     *
     * @param s               SnackBar
     * @param messageColor    color of message
     * @param backgroundColor color of background
     */
    public static void setSnackBarStyle(Snackbar s, int messageColor, int backgroundColor) {
        View view = s.getView();
        view.setBackgroundColor(backgroundColor);//修改view的背景色
        ((TextView) view.findViewById(R.id.snackbar_text)).setTextColor(messageColor);//获取Snackbar的message控件，修改字体颜色
//        ((Button) view.findViewById(R.id.snackbar_action)).setTextColor(messageColor);
    }

    public static void showSnackBar(View view, String text) {
//        SpannableString spannableString = new SpannableString(text);
//        spannableString.setSpan(new BackgroundColorSpan(R.color.red), 0, spannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        final Snackbar snackbar = Snackbar.make(view, text, Snackbar.LENGTH_SHORT);
        setSnackBarStyle(snackbar, Color.BLACK, Color.WHITE);
        snackbar.setAction("我知道了", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                snackbar.dismiss();
            }
        });
        snackbar.show();
    }

    /**
     * drawable着色方法
     *
     * @param drawable the drawable
     * @param colors   the color you want to draw
     * @return the drawable
     */
    public static Drawable tintDrawable(Drawable drawable, ColorStateList colors) {
        final Drawable wrappedDrawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTintList(wrappedDrawable, colors);
        return wrappedDrawable;
    }

    /**
     * 以url生成二维码
     *
     * @param url       the url
     * @param QR_WIDTH  width
     * @param QR_HEIGHT height
     * @return the qr code of url
     */
    public static Bitmap createQRImage(String url, int QR_WIDTH, int QR_HEIGHT) {
        Bitmap bitmap = null;
        try {
            // 判断URL合法性
            if (url == null || "".equals(url) || url.length() < 1) {
                return null;
            }
            Hashtable<EncodeHintType, String> hints = new Hashtable<>();
            hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
            // 图像数据转换，使用了矩阵转换
            BitMatrix bitMatrix = new QRCodeWriter().encode(url,
                    BarcodeFormat.QR_CODE, QR_WIDTH, QR_HEIGHT, hints);
            int[] pixels = new int[QR_WIDTH * QR_HEIGHT];
            // 下面这里按照二维码的算法，逐个生成二维码的图片，
            // 两个for循环是图片横列扫描的结果
            for (int y = 0; y < QR_HEIGHT; y++) {
                for (int x = 0; x < QR_WIDTH; x++) {
                    if (bitMatrix.get(x, y)) {
                        pixels[y * QR_WIDTH + x] = 0xff000000;
                    } else {
                        pixels[y * QR_WIDTH + x] = 0xffffffff;
                    }
                }
            }
            // 生成二维码图片的格式，使用ARGB_8888
            bitmap = Bitmap.createBitmap(QR_WIDTH, QR_HEIGHT,
                    Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, QR_WIDTH, 0, 0, QR_WIDTH, QR_HEIGHT);
            // 显示到一个ImageView上面
            // sweepIV.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    /**
     * 是否是合法的手机号
     *
     * @param mobiles phone number
     * @return true if mobiles is phone number, else false
     */
    public static boolean isMobileNO(String mobiles) {
        Pattern p = Pattern
                .compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
        Matcher m = p.matcher(mobiles);
        return m.matches();
    }
}


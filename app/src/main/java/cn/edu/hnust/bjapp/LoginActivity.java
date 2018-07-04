package cn.edu.hnust.bjapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.CoordinatorLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;

import cn.edu.hnust.bjapp.ui.ProgressDialog;
import cn.edu.hnust.bjapp.utils.DemoApplication;
import cn.edu.hnust.bjapp.utils.Utils;
import cn.edu.hnust.tbapp.R;

/**
 * Created by tjouyang on 2016/10/8.
 * bg_login activity
 */
public class LoginActivity extends Activity {
    /**
     * 立即登录按钮
     */
    private final int LOGIN_SUCCESS = 0X01;
    private final int LOGIN_FAIL = 0x02;
    private final int CONNECT_FAIL = 0x03;
    final int TEST_FAIL = 0x04;
    Button btn; //登录按钮
    View forgetPassword, register;    //忘记密码,立即注册
    private EditText userEdit, pswEdit;
    private ProgressDialog progressDialog;
    /**
     * 关于sharedPreference
     * sign 身份   1 导游 2 游客
     *
     *
     */
    private SharedPreferences sharedPreferences;
    RadioGroup radioGroup;    //控制互斥的radio group
    private int select = 2;    //选中了哪个radio button,初始化为选中游客
    CoordinatorLayout container;    //作为Snackbar的容器

    /**
     * handler是有可能内存泄漏，此处新建一个static类继承于handler,防止内存泄漏
     */
    private CustomHandler handler = new CustomHandler(this);

    static class CustomHandler extends Handler {
        WeakReference<LoginActivity> mActivity;

        CustomHandler(LoginActivity aFragment) {
            mActivity = new WeakReference<>(aFragment);
        }

        @Override
        public void handleMessage(Message message) {
            if (mActivity != null) {
                LoginActivity theActivity = mActivity.get();
                if (theActivity != null)
                    theActivity.updateUiByMessage(message);
            }
        }
    }

    private void updateUiByMessage(Message msg) {
        progressDialog.dismiss();
        switch (msg.what) {
            case LOGIN_FAIL:
                showSnackbar("账号或密码错误!");
                break;
            case LOGIN_SUCCESS:
                sharedPreferences.edit().putBoolean("isLogin", true).apply();   //是否登录,方便以后不跳转该页面
                sharedPreferences.edit().putBoolean("isThird", false).apply();  //第三方先不做,直接置为false
                sharedPreferences.edit().putBoolean("group", false).apply();
                sharedPreferences.edit().putInt("sign", select).apply();    //1导游,2游客
                sharedPreferences.edit().putInt("id", (Integer) msg.obj).apply();   //账号id
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                overridePendingTransition(R.anim.slide_right_in, R.anim.slide_right_out);
                LoginActivity.this.finish();
                // startService(new
                // Intent(LoginActivity.this,CheckUpdateService.class));
                // Toast.makeText(LoginActivity.this, "success",
                // Toast.LENGTH_LONG).show();
                break;
            case CONNECT_FAIL:
                showSnackbar("连接服务器失败!");
                break;
            case TEST_FAIL:
                showSnackbar("测试失败!");
                break;
        }
    }

//    /**一个犯过错的方法
//     * 此处handler 的警告是有可能内存泄漏，此处不可能内存泄漏所以没有修正， 如果修正，需要把handler
//     * 变为static类，变为static需要做很多修改
//     */
//    @SuppressLint("HandlerLeak")
//    private Handler handler = new Handler() {
//
//        @Override
//        public void handleMessage(Message msg) {
//            progressDialog.dismiss();
//            switch (msg.what) {
//                case LOGIN_FAIL:
//                    showSnackbar("账号或密码错误!");
//                    break;
//                case LOGIN_SUCCESS:
//                    sharedPreferences.edit().putBoolean("isLogin", true).apply();   //是否登录,方便以后不跳转该页面
//                    sharedPreferences.edit().putBoolean("isThird", false).apply();  //第三方先不做,直接置为false
//                    sharedPreferences.edit().putInt("sign", select).apply();    //1导游,2游客
//                    sharedPreferences.edit().putInt("id", (Integer) msg.obj).apply();   //账号id
//                    Log.e("+++++++++++++++", sharedPreferences.getInt("id", 0) + "");
//                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
//                    LoginActivity.this.finish();
//                    // startService(new
//                    // Intent(LoginActivity.this,CheckUpdateService.class));
//                    // Toast.makeText(LoginActivity.this, "success",
//                    // Toast.LENGTH_LONG).show();
//                    break;
//                case CONNECT_FAIL:
//                    showSnackbar("连接服务器失败!");
//                    break;
//                case TEST_FAIL:
//                    showSnackbar("测试失败!");
//                    break;
//            }
//            super.handleMessage(msg);
//        }
//
//    };

    /**
     * use Snackbar to show the text
     *
     * @param text which string to show
     */
    private void showSnackbar(String text) {
        Utils.showSnackBar(container, text);
    }


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        container = (CoordinatorLayout) findViewById(R.id.login_container);
        sharedPreferences = getSharedPreferences("Config", Context.MODE_PRIVATE);
        if (sharedPreferences.getBoolean("isLogin", false)) {
            startActivity(new Intent(this, MainActivity.class));
            overridePendingTransition(R.anim.slide_right_in, R.anim.slide_right_out);
            finish();
        }
        btn = (Button) findViewById(R.id.login_button);
        userEdit = (EditText) findViewById(R.id.login_username);
        pswEdit = (EditText) findViewById(R.id.login_password);
        radioGroup = (RadioGroup) findViewById(R.id.login_rg);
        forgetPassword = findViewById(R.id.login_forgetPassword);
        register = findViewById(R.id.login_register);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.login_rb_guide)
                    select = 1;
                if (checkedId == R.id.login_rb_tourist)
                    select = 2;
            }
        });
        // 消除非法字符
        pswEdit.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    String temp = s.toString();
                    String tem = temp.substring(temp.length() - 1,
                            temp.length());
                    char[] temC = tem.toCharArray();
                    int mid = temC[0];
                    if (mid >= 0 && mid <= 128) {
                        return;
                    }
                    s.delete(temp.length() - 1, temp.length());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String User = userEdit.getText().toString();
                String Password = pswEdit.getText().toString();

                if ("".equals(User)) {
                    showSnackbar("账号不能为空");
                    return;
                }
                if ("".equals(Password)) {
                    showSnackbar("密码不能为空!");
                    return;
                }
                if ("128".equals(User) && "128".equals(Password)) {
                    Toast.makeText(LoginActivity.this, "当前是无后台测试账号登陆",
                            Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(LoginActivity.this,
                            MainActivity.class));
                    overridePendingTransition(R.anim.slide_right_in, R.anim.slide_right_out);
                    sharedPreferences.edit().putInt("sign", select).apply();
                    sharedPreferences.edit().putBoolean("isLogin", true).apply();
                    LoginActivity.this.finish();
                    return;
                }
                login(User, Password);
            }
        });
        forgetPassword.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //TODO
                test();
            }
        });
        register.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                overridePendingTransition(R.anim.slide_right_in, R.anim.slide_right_out);
            }
        });
    }

    protected void test() {
//        startActivity(new Intent(this, TestActivity.class));
    }

    protected void login(String user, String password) {
        progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setMsg("登陆中，请稍后");
        progressDialog.show();
        String Path = Utils.getPath() + "/pigapp/LoginServlet";
        Log.e("Path", Path);
        JSONObject js = new JSONObject();
        try {
            js.put("username", user);
            js.put("password", password);
            js.put("sign", select);
            js.put("isthird", 0);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest newMissRequest = new JsonObjectRequest(
                Request.Method.POST, Path, js,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Message msg = Message.obtain();
                        try {
                            String s = response.getString("result");
//                            Toast.makeText(LoginActivity.this, s,
//                                    Toast.LENGTH_LONG).show();

                            if (s.equals("success")) {
                                int i = response.getInt("id");
                                msg.what = LOGIN_SUCCESS;
                                msg.obj = i;
                            } else {
                                msg.what = LOGIN_FAIL;
                            }
                        } catch (JSONException e) {
                            msg.what = LOGIN_FAIL;
                        }
                        handler.sendMessage(msg);
                    }

                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                handler.sendEmptyMessage(CONNECT_FAIL);
            }
        });
        DemoApplication.getHttpRequestQueue().add(newMissRequest);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        // if(keyCode == KeyEvent.KEYCODE_BACK){
        // Intent intent = new Intent();
        // intent.setAction(Intent.ACTION_MAIN);
        // intent.addCategory(Intent.CATEGORY_HOME);
        // startActivity(intent);
        // System.exit(0);
        // }
//        String url =
//                "http://minimg.hexun.com/i1.hexunimg.cn/2011-11-07/134970028_200x150.jpg";
//        new DownLoadImage(v).execute(url);
//        class DownLoadImage
//                extends AsyncTask<String, Void, Bitmap> {
//            ImageView imageView;
//
//            public DownLoadImage(ImageView imageView) {
//                this.imageView = imageView;
//            }
//
//            @Override
//            protected Bitmap doInBackground(String... urls) {
//                String
//                        url = urls[0];
//                Bitmap tmpBitmap = null;
//                try {
//                    InputStream is = new
//                            java.net.URL(url).openStream();
//                    tmpBitmap =
//                            BitmapFactory.decodeStream(is);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                return tmpBitmap;
//            }
//
//            @Override
//            protected void onPostExecute(Bitmap result) {
//                imageView.setImageBitmap(result);
//            }
//        }
        return super.onKeyUp(keyCode, event);
    }

}

/*
 * public class loginServlet extends HttpServlet {
 *
 * private static final long serialVersionUID = -6485973345643890505L;
 *
 * public void doGet(HttpServletRequest request, HttpServletResponse response)
 * throws ServletException, IOException { doPost(request, response); }
 *
 * public void doPost(HttpServletRequest request, HttpServletResponse response)
 * throws ServletException, IOException {
 *
 * response.setContentType("text/plain;charset=utf-8"); /*BufferedReader reader
 * = request.getReader(); StringBuffer buffer = new StringBuffer(); String
 * string; while ((string = reader.readLine()) != null) { buffer.append(string);
 * } reader.close(); String result = null; JSONObject object;
 */
/*
 * try { // object = new JSONObject(buffer.toString()); // String user_name =
 * object.getString("user_name"); // String user_password =
 * object.getString("user_password"); String result = null; String user_name =
 * request.getParameter("user_name"); String user_password =
 * request.getParameter("user_password"); //System.out.println("user_name:" +
 * user_name + "  user_password:" + user_password);
 * response.setCharacterEncoding("utf-8"); // PrintWriter out =
 * response.getWriter(); // JSONObject obj = new JSONObject(); //String
 * agency_id = null; //String guide_id = null; dbConnection db = new
 * dbConnection();
 *
 * String strSQL =
 * "select bg_login.user_name, bg_login.user_password from bg_login where bg_login.user_name = '"
 * + user_name + "' and bg_login.user_password = '"+user_password+"'"; String
 * strSQL2 =
 * "select bg_login.user_name, bg_login.user_password from bg_login where bg_login.user_name = '"
 * + user_name + "' and bg_login.user_password != '"+user_password+"'";
 *
 * try{ ResultSet rs = db.executeQuery(strSQL); String name =null;
 * while(rs.next()) name = rs.getString("user_name"); String name2=null;
 * ResultSet rs2 = db.executeQuery(strSQL2); while(rs2.next()){ name2=
 * rs2.getString("user_name"); } // System.out.println("11111111111");
 * //System.out.println(name); /* if(name == null& name2 == null) { result =
 * "wrong11"; }else if (name2 != null){ result = "password_error"; }else result
 * = "success"; // identify_card wrong, no such guide }catch(SQLException e){
 * e.printStackTrace(); }
 */
/*
 * if(name == null& name2 == null) { result = "wrong"; }else if (name2 != null){
 * result = "password_error"; }else result = "success"; // identify_card wrong,
 * no such guide }catch(SQLException e){ e.printStackTrace(); } //
 * System.out.println("12"); // obj.put("code", result); //
 * out.print(obj.toString()); response.setCharacterEncoding("utf-8");
 * PrintWriter out = response.getWriter(); JSONObject obj = new JSONObject();
 * System.out.println(result); obj.put("code", result);
 * out.print(obj.toString()); out.flush(); out.close();
 *
 * } catch (Exception e) { e.printStackTrace(); } } }
 */


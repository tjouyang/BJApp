package cn.edu.hnust.bjapp.ui;
import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.TextView;

import cn.edu.hnust.tbapp.R;
/**
 * Created by tjouyang on 2016/10/8.
 */


public class ProgressDialog extends Dialog {

    private TextView txt;

    public ProgressDialog(Context context) {
        super(context, R.style.MyDialogStyle);

        // 加载布局文件
        View view = View.inflate(context, R.layout.loading, null);
        // dialog添加视图
        setContentView(view);
        txt = (TextView) view.findViewById(R.id.tv_msg);
        // 下一步给图片添加动态效果

    }

    /**
     * 对话框设置内容
     *
     * @param msg
     */
    public void setMsg(String msg) {
        txt.setText(msg);
    }

    /**
     * 显示对话框
     */
    public void showProgersssDialog() {
        this.show();
    }

    /**
     * 关闭对话框
     */
    public void closeProgersssDialog() {
        this.dismiss();
    }

}


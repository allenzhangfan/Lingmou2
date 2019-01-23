package cloud.antelope.lingmou.app.utils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import cloud.antelope.lingmou.R;
import cloud.antelope.lingmou.mvp.ui.widget.MDLoadingView;


/**
 * Created by liucheng on 16/5/11.
 */
public class ViewUtils {

    /**
     * 显示一个圆形进度对话框
     *
     * @param message        进度条提示信息,如果不传，默认显示：正在登录...
     * @param context        运行所在的容器
     * @param cancelListener 如果对话框可以被用户手动取消，则传递取消按钮的监听器； 如果对话框不可以被用户手动取消，则传null
     * @return 进度对话框实例
     */
    public static Dialog showProgressMessage(String message,
                                             final Context context,
                                             DialogInterface.OnCancelListener cancelListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View container = inflater
                .inflate(R.layout.common_progress_dialog, null);
        final MDLoadingView loadingView = (MDLoadingView) container
                .findViewById(R.id.loadingView);
        // 提示文字控件
        TextView msgTV = (TextView) container
                .findViewById(R.id.common_progress_msg);
        msgTV.setText(message);
        dialog.setContentView(container);
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                loadingView.clearAnimation();
            }
        });
        if (cancelListener != null) {
            dialog.setCancelable(true);
            dialog.setOnCancelListener(cancelListener);
        } else {
            dialog.setCancelable(false);
        }
        return dialog;
    }

    /**
     * 显示一个圆形进度对话框 (IOS)
     *
     * @param message        进度条提示信息,如果不传，默认显示：正在登录...
     * @param context        运行所在的容器
     * @param cancelListener 如果对话框可以被用户手动取消，则传递取消按钮的监听器； 如果对话框不可以被用户手动取消，则传null
     * @return 进度对话框实例
     */
    public static Dialog showAppleProgressMessage(String message,
                                                  final Context context,
                                                  DialogInterface.OnCancelListener cancelListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View container = inflater
                .inflate(R.layout.common_apple_progress_dialog, null);
        final MDLoadingView loadingView = (MDLoadingView) container
                .findViewById(R.id.loadingView);
        // 提示文字控件
        TextView msgTV = (TextView) container
                .findViewById(R.id.common_progress_msg);
        msgTV.setText(message);
        dialog.setContentView(container);
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                loadingView.clearAnimation();
            }
        });
        if (cancelListener != null) {
            dialog.setCancelable(true);
            dialog.setOnCancelListener(cancelListener);
        } else {
            dialog.setCancelable(false);
        }
        return dialog;
    }

    /**
     * 弹出退出登录对话框.
     *
     * @param message
     * @param context
     * @param isForceUpgrade 是否强制更新
     * @return
     */
    public static Dialog showUpgradeDialog(boolean isForceUpgrade, String message,
                                           Context context,
                                           View.OnClickListener posListener,
                                           View.OnClickListener negativeListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.dialogViewStyle);
        AlertDialog dialog = builder.create();
        dialog.show();
        // dimAmount在0.0f和1.0f之间，0.0f完全不暗，即背景是可见的 ，1.0f时候，背景全部变黑暗。
        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        lp.dimAmount = 0.4f;
        dialog.getWindow().setAttributes(lp);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        View container = LayoutInflater.from(context).inflate(R.layout.layout_upgrade_dialog, null);
        TextView messageTV = (TextView) container
                .findViewById(R.id.message);
        Button confirmBtn = (Button) container
                .findViewById(R.id.btn_confirm);
        ImageView cancelBtn = (ImageView) container
                .findViewById(R.id.btn_cacel);
        messageTV.setText(message);

        if (isForceUpgrade) {
            cancelBtn.setVisibility(View.GONE);
        }

        confirmBtn.setOnClickListener(posListener);
        cancelBtn.setOnClickListener(negativeListener);
        dialog.setContentView(container);
        dialog.setCancelable(false);
        return dialog;
    }

}

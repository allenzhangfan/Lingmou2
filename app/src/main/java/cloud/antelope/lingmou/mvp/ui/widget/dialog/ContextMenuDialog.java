/*
 * Copyright (C) 2017 LingDaNet.Co.Ltd. All Rights Reserved.
 */

package cloud.antelope.lingmou.mvp.ui.widget.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import cloud.antelope.lingmou.R;

/**
 * 作者：安兴亚
 * 创建日期：2017/1/8
 * 描述：重命名Dialog，继承自Dialog
 */
public abstract class ContextMenuDialog extends Dialog {

    private Context context;

    /**
     * 构造器
     *
     * @param context 上下文
     */
    public ContextMenuDialog(Context context) {
        super(context, R.style.Base_Dialog);
        this.context = context;
        setContentView(R.layout.dialog_context_menu);
        createDialog();
        setCancelable(true);
        setCanceledOnTouchOutside(true);
    }

    /**
     * 设置dialog
     */
    public void createDialog() {
        Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.gravity = Gravity.CENTER;
        window.setAttributes(params);
        initView();
        if (!(context instanceof Activity)) {
            getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        }
    }

    public abstract void initView();

    public ContextMenuDialog closeDialog() {
        this.dismiss();
        return this;
    }

}

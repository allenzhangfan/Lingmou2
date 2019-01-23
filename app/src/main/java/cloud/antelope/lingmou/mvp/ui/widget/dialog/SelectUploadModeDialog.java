package cloud.antelope.lingmou.mvp.ui.widget.dialog;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import cloud.antelope.lingmou.R;



public class SelectUploadModeDialog extends Dialog implements View.OnClickListener {
    private Context mContext;
    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public SelectUploadModeDialog(Context context) {
        super(context, R.style.bottom_dialog);
        this.mContext=context;
        init();
    }

    private void init() {
        View view = View.inflate(getContext(), R.layout.dialog_upload_mode, null);
        view.findViewById(R.id.iv_face).setOnClickListener(this);
        view.findViewById(R.id.iv_take_photo).setOnClickListener(this);
        view.findViewById(R.id.iv_photos).setOnClickListener(this);
        setContentView(view);
        Window window = getWindow();
        // 设置显示动画
        window.setWindowAnimations(R.style.main_menu_animstyle);
        WindowManager.LayoutParams wl = window.getAttributes();
        wl.x = 0;
        wl.y = ((Activity)mContext).getWindowManager().getDefaultDisplay().getHeight();
        // 以下这两句是为了保证按钮可以水平满屏
        wl.width = ViewGroup.LayoutParams.MATCH_PARENT;
        wl.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        // 设置显示位置
        onWindowAttributesChanged(wl);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_face:
                if(onItemClickListener!=null){
                    onItemClickListener.onClick(0);
                }
                break;
            case R.id.iv_take_photo:
                if(onItemClickListener!=null){
                    onItemClickListener.onClick(1);
                }
                break;
            case R.id.iv_photos:
                if(onItemClickListener!=null){
                    onItemClickListener.onClick(2);
                }
                break;
        }
    }

    public interface OnItemClickListener{
        void onClick(int position);
    }
}



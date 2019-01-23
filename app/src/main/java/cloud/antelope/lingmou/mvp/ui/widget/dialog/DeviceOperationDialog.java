package cloud.antelope.lingmou.mvp.ui.widget.dialog;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import cloud.antelope.lingmou.R;


public class DeviceOperationDialog extends Dialog implements View.OnClickListener {
    private Context mContext;
    private OnItemClickListener onItemClickListener;
    private boolean isSelected;
    private TextView tv;
    private ImageView iv;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public DeviceOperationDialog(Context context,boolean isSelected) {
        super(context, R.style.bottom_dialog);
        this.isSelected=isSelected;
        this.mContext=context;
        init();
    }

    private void init() {
        View view = View.inflate(getContext(), R.layout.dialog_device_operation, null);
        view.findViewById(R.id.ll_favorite).setOnClickListener(this);
        view.findViewById(R.id.ll_face).setOnClickListener(this);
        view.findViewById(R.id.ll_body).setOnClickListener(this);
        view.findViewById(R.id.ll_location).setOnClickListener(this);
        view.findViewById(R.id.ll_information).setOnClickListener(this);
        view.findViewById(R.id.tv_cancel).setOnClickListener(this);
        tv = view.findViewById(R.id.tv_favorite);
        iv = view.findViewById(R.id.iv_favorite);
        tv.setText(isSelected?R.string.cancel_collect:R.string.favorite_device);
        iv.setBackgroundResource(isSelected?R.drawable.following:R.drawable.following_grey);


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
            case R.id.ll_favorite:
                if(onItemClickListener!=null){
                    onItemClickListener.onClick(0);
                }
                break;
            case R.id.ll_face:
                if(onItemClickListener!=null){
                    onItemClickListener.onClick(1);
                }
                break;
            case R.id.ll_body:
                if(onItemClickListener!=null){
                    onItemClickListener.onClick(2);
                }
                break;
            case R.id.ll_location:
                if(onItemClickListener!=null){
                    onItemClickListener.onClick(3);
                }
                break;
            case R.id.ll_information:
                if(onItemClickListener!=null){
                    onItemClickListener.onClick(4);
                }
                break;
            case R.id.tv_cancel:
                dismiss();
                break;
        }
    }

    public void setSelected(boolean isSelected){
        this.isSelected=isSelected;
        tv.setText(isSelected?R.string.cancel_collect:R.string.favorite_device);
        iv.setBackgroundResource(isSelected?R.drawable.following:R.drawable.following_grey);
    }
    public interface OnItemClickListener{
        void onClick(int position);
    }
}



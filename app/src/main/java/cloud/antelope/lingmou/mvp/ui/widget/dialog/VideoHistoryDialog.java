package cloud.antelope.lingmou.mvp.ui.widget.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.lingdanet.safeguard.common.utils.TimeUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import cloud.antelope.lingmou.R;
import cloud.antelope.lingmou.app.utils.FormatUtils;
import timber.log.Timber;


public class VideoHistoryDialog extends Dialog implements View.OnClickListener {
    private Context mContext;
    private List<PlayTime> list;
    private BaseQuickAdapter<PlayTime, BaseViewHolder> adapter;
    private int day;
    private int hour = -1;
    private List<Date> dateList;
    private TextView tvDay;
    private OnTimeConfirmListener onTimeConfirmListener;

    public void setOnTimeConfirmListener(OnTimeConfirmListener onTimeConfirmListener) {
        this.onTimeConfirmListener = onTimeConfirmListener;
    }

    public VideoHistoryDialog(Context context) {
        super(context, R.style.right_dialog);
        this.mContext = context;
        init();
    }

    private void setDay(int day) {
        if (day < 0) day = 0;
        if (day > 6) day = 6;
        this.day = day;
        Date date = dateList.get(day);
        String date2String = TimeUtils.date2String(date, new SimpleDateFormat("yyyy-MM-dd"));
        tvDay.setText(date2String);
    }

    private void setHour(int hour) {
        this.hour = hour;
        for (int i = 0; i < list.size(); i++) {
            list.get(i).selected = i == hour;
        }
        adapter.notifyDataSetChanged();
    }

    private void init() {
        View view = View.inflate(getContext(), R.layout.dialog_video_history, null);
        view.findViewById(R.id.iv_left).setOnClickListener(this);
        view.findViewById(R.id.iv_right).setOnClickListener(this);
        view.findViewById(R.id.tv_confirm).setOnClickListener(this);
        tvDay = view.findViewById(R.id.tv_data);
        RecyclerView rv = view.findViewById(R.id.rv);
        rv.setLayoutManager(new GridLayoutManager(mContext, 3));
        adapter = new BaseQuickAdapter<PlayTime, BaseViewHolder>(R.layout.item_video_history) {
            @Override
            protected void convert(BaseViewHolder helper, PlayTime item) {
                TextView tv = helper.getView(R.id.tv_hour);
                tv.setText(item.time);
                tv.setSelected(item.selected);
            }
        };
        rv.setAdapter(adapter);
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                setHour(position);
            }
        });
        rv.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                int position = parent.getChildAdapterPosition(view);
                Resources resources = mContext.getResources();
                outRect.left = position % 3 == 0 ? resources.getDimensionPixelSize(R.dimen.dp16) : resources.getDimensionPixelSize(R.dimen.dp12);
                outRect.right = position % 3 == 2 ? resources.getDimensionPixelSize(R.dimen.dp16) : resources.getDimensionPixelSize(R.dimen.dp12);
                outRect.bottom = mContext.getResources().getDimensionPixelSize(R.dimen.dp16);
            }
        });
        setContentView(view);

        list = new ArrayList<>();
        for (int i = 0; i < 24; i++) {
            PlayTime playTime = new PlayTime();
            playTime.time = FormatUtils.twoNumber(i) + ":00";
            list.add(playTime);
        }
        adapter.setNewData(list);

        dateList = new ArrayList<>();
        long currentTimeMillis = System.currentTimeMillis();
        for (int i = 6; i >= 0; i--) {
            Date date = TimeUtils.millis2Date(currentTimeMillis - i * 86400 * 1000);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH) ;
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            calendar.set(year,month,day,0,0,0);
            dateList.add(calendar.getTime());
        }
        setDay(6);

        Window window = getWindow();
        window.setWindowAnimations(R.style.right_animstyle);
        WindowManager.LayoutParams wl = window.getAttributes();
        wl.x = 0;
        wl.y = ((Activity) mContext).getWindowManager().getDefaultDisplay().getHeight();
        wl.width = mContext.getResources().getDimensionPixelSize(R.dimen.dp272);
        wl.height = ViewGroup.LayoutParams.MATCH_PARENT;
        wl.gravity = Gravity.RIGHT;
        window.setDimAmount(0f);
        // 设置显示位置
        onWindowAttributesChanged(wl);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_left:
                setDay(--day);
                break;
            case R.id.iv_right:
                setDay(++day);
                break;
            case R.id.tv_confirm:
                dismiss();
                if (onTimeConfirmListener != null) {
                    long l = dateList.get(day).getTime() + hour * 60 * 60 * 1000;
                    Timber.e("选择："+TimeUtils.date2String(new Date(l), new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")));
                    onTimeConfirmListener.onTimeConfirm(new Date(l));
                }
                break;
        }
    }

    class PlayTime {
        public boolean selected;
        public String time;
    }

    public interface OnTimeConfirmListener {
        void onTimeConfirm(Date date);
    }
}



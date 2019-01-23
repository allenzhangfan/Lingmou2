package cloud.antelope.lingmou.mvp.ui.widget;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputConnectionWrapper;

public class InterceptableEditText extends android.support.v7.widget.AppCompatEditText {
    private OnCommitTextListener onCommitTextListener;

    public void setOnCommitTextListener(OnCommitTextListener onCommitTextListener) {
        this.onCommitTextListener = onCommitTextListener;
    }

    public InterceptableEditText(Context context) {
        super(context);
    }

    public InterceptableEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public InterceptableEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
        return new MyInputConnection(super.onCreateInputConnection(outAttrs), false);
    }

    class MyInputConnection extends InputConnectionWrapper implements
            InputConnection {

        public MyInputConnection(InputConnection target, boolean mutable) {
            super(target, mutable);
        }

        @Override
        public boolean commitText(CharSequence text, int newCursorPosition) {
            if(!isNumber(text.toString())){
                text="";
            }else if (onCommitTextListener != null) {
                onCommitTextListener.commitText(text);
            }
            return super.commitText(text, newCursorPosition);
        }
    }
    private boolean isNumber(String text) {
        String telRegex = "^\\d+$";
        if (TextUtils.isEmpty(text)) {
            return false;
        } else {
            return text.matches(telRegex);
        }
    }
    public interface OnCommitTextListener {
        void commitText(CharSequence text);
    }
}

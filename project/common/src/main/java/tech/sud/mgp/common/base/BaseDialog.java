package tech.sud.mgp.common.base;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public abstract class BaseDialog extends Dialog {

    protected View mRootView;
    private OnDestroyListener mOnDestroyListener;

    public BaseDialog(@NonNull Context context) {
        super(context);
    }

    public BaseDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    protected BaseDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int layoutId = getLayoutId();
        if (layoutId > 0) {
            mRootView = View.inflate(getContext(), layoutId, null);
            setContentView(mRootView);
        }
        Window window = getWindow();
        if (window != null) {
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); // 去掉默认的背景
            WindowManager.LayoutParams attributes = window.getAttributes();
            attributes.width = getWidth();
            attributes.height = getHeight();
            attributes.gravity = getGravity();
            window.setAttributes(attributes);
        }
        setCancelable(cancelable());
        setCanceledOnTouchOutside(canceledOnTouchOutside());
        initWidget();
        initData();
        setListeners();
    }

    protected int getLayoutId() {
        return 0;
    }

    protected void initWidget() {
    }

    protected void initData() {
    }

    protected void setListeners() {
        setOnCancelListener(new OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                OnDestroyListener listener = mOnDestroyListener;
                if (listener != null) {
                    listener.onDestroy();
                }
            }
        });
        setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                OnDestroyListener listener = mOnDestroyListener;
                if (listener != null) {
                    listener.onDestroy();
                }
            }
        });
    }

    protected int getWidth() {
        return ViewGroup.LayoutParams.WRAP_CONTENT;
    }

    protected int getHeight() {
        return ViewGroup.LayoutParams.WRAP_CONTENT;
    }

    protected int getGravity() {
        return Gravity.CENTER;
    }

    protected boolean cancelable() {
        return true;
    }

    protected boolean canceledOnTouchOutside() {
        return true;
    }

    public void setOnDestroyListener(OnDestroyListener listener) {
        mOnDestroyListener = listener;
    }

    public interface OnDestroyListener {
        void onDestroy();
    }

}

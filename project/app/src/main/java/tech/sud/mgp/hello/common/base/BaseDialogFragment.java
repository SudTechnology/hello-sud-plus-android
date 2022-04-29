package tech.sud.mgp.hello.common.base;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public abstract class BaseDialogFragment extends DialogFragment {

    protected View mRootView;
    private OnDestroyListener mOnDestroyListener;
    private Boolean customCanceledOnTouchOutside;
    private Boolean customCancelable;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        int layoutId = getLayoutId();
        if (layoutId > 0) {
            mRootView = inflater.inflate(layoutId, container, false);
            return mRootView;
        }
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Dialog dialog = getDialog();
        if (dialog != null) {
            Window window = dialog.getWindow();
            if (window != null) {
                window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); // 去掉默认的背景
                window.getDecorView().setBackgroundColor(Color.TRANSPARENT);//不加这个行代码 dialog宽度无法铺满
                WindowManager.LayoutParams attributes = window.getAttributes();
                attributes.width = getWidth();
                attributes.height = getHeight();
                attributes.gravity = getGravity();
                window.setAttributes(attributes);
                customStyle(window);
            }
            if (customCancelable == null) {
                dialog.setCancelable(cancelable());
            } else {
                dialog.setCancelable(customCancelable);
            }
            if (customCanceledOnTouchOutside == null) {
                dialog.setCanceledOnTouchOutside(canceledOnTouchOutside());
            } else {
                dialog.setCanceledOnTouchOutside(customCanceledOnTouchOutside);
            }
        }
        initWidget();
        initData();
        setListeners();
    }

    protected void customStyle(Window window) {
    }

    protected abstract int getLayoutId();

    protected void initWidget() {
    }

    protected void initData() {
    }

    protected void setListeners() {
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

    /** 设置点击空白处是否可以消失 */
    public void setCustomCanceledOnTouchOutside(Boolean customCanceledOnTouchOutside) {
        this.customCanceledOnTouchOutside = customCanceledOnTouchOutside;
    }

    /** 设置按返回键是否可以消失 */
    public void setCustomCancelable(Boolean customCancelable) {
        this.customCancelable = customCancelable;
    }

    public void setOnDestroyListener(OnDestroyListener listener) {
        mOnDestroyListener = listener;
    }

    public interface OnDestroyListener {
        void onDestroy();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        OnDestroyListener onDestroyListener = mOnDestroyListener;
        if (onDestroyListener != null) {
            onDestroyListener.onDestroy();
        }
    }

    public <T extends View> T findViewById(@IdRes int id) {
        return mRootView.findViewById(id);
    }

}

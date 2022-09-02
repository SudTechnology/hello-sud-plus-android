package tech.sud.mgp.hello.common.base;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import tech.sud.mgp.hello.R;

public abstract class BaseBottomSheetDialogFragment extends BottomSheetDialogFragment {

    protected View mRootView;
    private OnDestroyListener mOnDestroyListener;
    private Boolean customCanceledOnTouchOutside;
    private Boolean customCancelable;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.CustomBottomSheetDialogFragmentTheme);
    }

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
        if (beforeSetContentView()) {
            dismiss();
            return;
        }
        initDialotStyle();
        initWidget();
        initData();
        setListeners();
    }

    private void initDialotStyle() {
        Dialog dialog = getDialog();
        if (dialog != null) {
            Window window = dialog.getWindow();
            if (window != null) {
                View bottomView = window.findViewById(com.google.android.material.R.id.design_bottom_sheet);
                if (bottomView != null) {
                    BottomSheetBehavior<View> behavior = BottomSheetBehavior.from(bottomView);
                    setBehavior(behavior);
                    ViewGroup.LayoutParams layoutParams = bottomView.getLayoutParams();
                    layoutParams.height = getHeight();
                    bottomView.setLayoutParams(layoutParams);
                }
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
    }

    protected void setBehavior(BottomSheetBehavior<View> behavior) {
    }

    /**
     * 在初始化之前会调用
     *
     * @return 返回true则直接销毁页面，不进行后续初始化
     */
    protected boolean beforeSetContentView() {
        return false;
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

    protected int getHeight() {
        return ViewGroup.LayoutParams.WRAP_CONTENT;
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

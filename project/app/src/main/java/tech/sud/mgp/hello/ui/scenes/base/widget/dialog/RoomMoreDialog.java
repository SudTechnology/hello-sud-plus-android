package tech.sud.mgp.hello.ui.scenes.base.widget.dialog;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import androidx.annotation.Nullable;

import com.blankj.utilcode.util.BarUtils;
import com.gyf.immersionbar.ImmersionBar;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.base.BaseDialogFragment;
import tech.sud.mgp.hello.common.utils.DensityUtils;
import tech.sud.mgp.hello.common.utils.ViewUtils;

/**
 * 房间内，更多-弹窗
 */
public class RoomMoreDialog extends BaseDialogFragment {

    private View containerHang;
    private View containerExit;
    private View.OnClickListener hangOnClickListener;
    private View.OnClickListener exitOnClickListener;
    private boolean isFullScreen;

    public static RoomMoreDialog getInstance(boolean isFullScreen) {
        RoomMoreDialog dialog = new RoomMoreDialog();
        Bundle bundle = new Bundle();
        bundle.putBoolean("isFullScreen", isFullScreen);
        dialog.setArguments(bundle);
        return dialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if (arguments != null) {
            isFullScreen = arguments.getBoolean("isFullScreen");
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_room_more;
    }

    @Override
    protected int getWidth() {
        return ViewGroup.LayoutParams.MATCH_PARENT;
    }

    @Override
    protected int getHeight() {
        return DensityUtils.dp2px(146) + BarUtils.getStatusBarHeight();
    }

    @Override
    protected int getGravity() {
        return Gravity.TOP;
    }

    @Override
    protected void customStyle(Window window) {
        super.customStyle(window);
        window.setDimAmount(0);
        window.setWindowAnimations(R.style.TopToBottomAnim);
        if (isFullScreen) {
            ImmersionBar.with(this).init();
        } else {
            ImmersionBar.with(this).init();
        }
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        containerHang = findViewById(R.id.container_hang);
        containerExit = findViewById(R.id.container_exit);
        ViewUtils.addMarginTop(containerHang, ImmersionBar.getStatusBarHeight(this));
    }

    @Override
    protected void setListeners() {
        super.setListeners();
        containerHang.setOnClickListener(hangOnClickListener);
        containerExit.setOnClickListener(exitOnClickListener);
    }

    /** 设置挂起按钮点击事件 */
    public void setHangOnClickListener(View.OnClickListener listener) {
        hangOnClickListener = listener;
    }

    /** 设置退出按钮点击事件 */
    public void setExitOnClickListener(View.OnClickListener listener) {
        exitOnClickListener = listener;
    }

}

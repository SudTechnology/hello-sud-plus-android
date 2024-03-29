package tech.sud.mgp.hello.ui.scenes.base.widget.view.floating;

import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.gyf.immersionbar.ImmersionBar;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.utils.DensityUtils;
import tech.sud.mgp.hello.ui.scenes.base.model.RoomInfoModel;

public class RoomFloatingView extends ConstraintLayout implements IRoomFloating {

    private WindowManager mWindowManager;
    private final WindowManager.LayoutParams mLayoutParams = new WindowManager.LayoutParams();
    private final Point screenSize = DensityUtils.getScreenSize();
    private TextView tvName;
    private View viewShutdown;

    private boolean mIsShow = false;
    private long clickTime = 0;
    private float downX = 0;
    private float downY = 0;
    private float downViewX = 0;
    private float downViewY = 0;
    private int minX = 0;
    private int minY = 0;
    private int maxX = 0;
    private int maxY = 0;

    public RoomFloatingView(@NonNull Context context) {
        this(context, null);
    }

    public RoomFloatingView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RoomFloatingView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
        initWindow();
    }

    private void initView() {
        inflate(getContext(), R.layout.view_room_floating, this);
        tvName = findViewById(R.id.tv_name);
        viewShutdown = findViewById(R.id.iv_shutdown);
    }

    private void initWindow() {
        //悬浮窗管理相关
        mWindowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mLayoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            mLayoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        }
        mLayoutParams.format = PixelFormat.RGBA_8888;
        mLayoutParams.gravity = Gravity.START | Gravity.TOP;
        mLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        mLayoutParams.width = LinearLayout.LayoutParams.WRAP_CONTENT;
        mLayoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
        measure(MeasureSpec.makeMeasureSpec(screenSize.x, MeasureSpec.AT_MOST), MeasureSpec.makeMeasureSpec(screenSize.y, MeasureSpec.AT_MOST));
        updateMaxSize();
    }

    private void updateMaxSize() {
        maxX = screenSize.x - getMeasuredWidth();
        maxY = screenSize.y - getMeasuredHeight() - ImmersionBar.getNavigationBarHeight(getContext());
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        updateMaxSize();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                updateMaxSize();
                clickTime = System.currentTimeMillis();
                downX = event.getRawX();
                downY = event.getRawY();
                if (mLayoutParams.x < minX) mLayoutParams.x = minX;
                if (mLayoutParams.x > maxX) mLayoutParams.x = maxX;
                if (mLayoutParams.y < minY) mLayoutParams.y = minY;
                if (mLayoutParams.y > maxY) mLayoutParams.y = maxY;
                // 更新悬浮窗控件布局
                mWindowManager.updateViewLayout(this, mLayoutParams);
                downViewX = mLayoutParams.x;
                downViewY = mLayoutParams.y;
                break;
            case MotionEvent.ACTION_MOVE:
                float movedX = event.getRawX() - downX;
                float movedY = event.getRawY() - downY;
                int intentX = (int) (downViewX + movedX);
                int intentY = (int) (downViewY + movedY);

                if (intentX < minX) {
                    mLayoutParams.x = minX;
                } else if (intentX > maxX) {
                    mLayoutParams.x = maxX;
                } else {
                    mLayoutParams.x = intentX;
                }

                if (intentY < minY) {
                    mLayoutParams.y = minY;
                } else if (intentY > maxY) {
                    mLayoutParams.y = maxY;
                } else {
                    mLayoutParams.y = intentY;
                }

                // 更新悬浮窗控件布局
                mWindowManager.updateViewLayout(this, mLayoutParams);
                break;
            case MotionEvent.ACTION_UP:
                int scaledTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
                //超过两秒就不算点击事件，或者是移动事件，就不算点击事件了
                if (System.currentTimeMillis() - clickTime > 1000
                        || Math.abs(downX - event.getRawX()) > scaledTouchSlop
                        || Math.abs(downY - event.getRawY()) > scaledTouchSlop) {
                    return false;
                }
                break;
        }
        return super.onTouchEvent(event);
    }

    /** 显示 */
    @Override
    public void show() {
        if (!mIsShow) {
            mLayoutParams.x = maxX - DensityUtils.dp2px(6);
            mLayoutParams.y = maxY - DensityUtils.dp2px(135);
            mWindowManager.addView(this, mLayoutParams);
            mIsShow = true;
        }
    }

    /** 隐藏 */
    @Override
    public void dismiss() {
        if (mIsShow) {
            mWindowManager.removeView(this);
            mIsShow = false;
        }
    }

    /** 设置房间数据 */
    @Override
    public void setRoomInfoModel(RoomInfoModel model) {
        tvName.setText(model.roomName);
        measure(MeasureSpec.makeMeasureSpec(screenSize.x, MeasureSpec.AT_MOST), MeasureSpec.makeMeasureSpec(screenSize.y, MeasureSpec.AT_MOST));
        updateMaxSize();
    }

    /** 设置关闭按钮监听器 */
    @Override
    public void setShutdownListener(OnClickListener listener) {
        viewShutdown.setOnClickListener(listener);
    }

}

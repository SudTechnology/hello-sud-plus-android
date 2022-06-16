package tech.sud.mgp.hello.ui.scenes.base.widget.view.floating;

import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.TextureView;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.gyf.immersionbar.ImmersionBar;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.utils.DensityUtils;
import tech.sud.mgp.hello.ui.scenes.base.model.RoomInfoModel;

/**
 * 弹幕房间的悬浮窗
 */
public class DanmakuRoomFloatingView extends ConstraintLayout implements IRoomFloating {

    private WindowManager mWindowManager;
    private final WindowManager.LayoutParams mLayoutParams = new WindowManager.LayoutParams();
    private final Point screenSize = DensityUtils.getScreenSize();
    private TextureView videoView;
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

    public DanmakuRoomFloatingView(@NonNull Context context) {
        this(context, null);
    }

    public DanmakuRoomFloatingView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DanmakuRoomFloatingView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
        initWindow();
    }

    private void initView() {
        inflate(getContext(), R.layout.view_danmaku_room_floating, this);
        videoView = findViewById(R.id.video_view);
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
                float intentX = downViewX + movedX;
                float intentY = downViewY + movedY;

                if (intentX > minX && intentX < maxX) mLayoutParams.x = (int) intentX;
                if (intentY > minY && intentY < maxY) mLayoutParams.y = (int) intentY;
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
            mLayoutParams.x = maxX - DensityUtils.dp2px(16);
            mLayoutParams.y = maxY - DensityUtils.dp2px(169);
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
        measure(MeasureSpec.makeMeasureSpec(screenSize.x, MeasureSpec.AT_MOST), MeasureSpec.makeMeasureSpec(screenSize.y, MeasureSpec.AT_MOST));
        updateMaxSize();
    }

    /** 设置关闭按钮监听器 */
    @Override
    public void setShutdownListener(OnClickListener listener) {
        viewShutdown.setOnClickListener(listener);
    }

    public View getVideoView() {
        return videoView;
    }

}

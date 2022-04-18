package tech.sud.mgp.hello.ui.scenes.base.widget.view;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Build;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
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

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.utils.DensityUtils;
import tech.sud.mgp.hello.ui.scenes.base.model.RoomInfoModel;

public class RoomFloatingView extends ConstraintLayout {

    private WindowManager mWindowManager;
    private WindowManager.LayoutParams mLayoutParams = new WindowManager.LayoutParams();
    private DisplayMetrics display = DensityUtils.getDisplayMetrics(getContext());
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
        init();
        initView();
    }

    private void initView() {
        inflate(getContext(), R.layout.view_room_floating, this);
        tvName = findViewById(R.id.tv_name);
        viewShutdown = findViewById(R.id.iv_shutdown);
    }

    private void init() {
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
        maxX = display.widthPixels - mLayoutParams.width;
        maxY = display.heightPixels - mLayoutParams.height;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        maxX = display.widthPixels - mLayoutParams.width;
        maxY = display.heightPixels - mLayoutParams.height;
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
    public void show() {
        if (!mIsShow) {
            mLayoutParams.x = maxX - DensityUtils.dp2px(140);
            mLayoutParams.y = maxY - DensityUtils.dp2px(145);
            mWindowManager.addView(this, mLayoutParams);
            mIsShow = true;
        }
    }

    /** 隐藏 */
    public void dismiss() {
        if (mIsShow) {
            mWindowManager.removeView(this);
            mIsShow = false;
        }
    }

    /** 设置房间数据 */
    public void setRoomInfoModel(RoomInfoModel model) {
        tvName.setText(model.roomName);
    }

    /** 设置关闭按钮监听器 */
    public void setShutdownListener(OnClickListener listener) {
        viewShutdown.setOnClickListener(listener);
    }

}

package tech.sud.mgp.common.widget.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.ColorInt;
import androidx.annotation.IntRange;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.ListIterator;

import tech.sud.mgp.common.R;
import tech.sud.mgp.common.utils.DensityUtils;

public class SoundLevelView extends View {

    private boolean isDiffuse = false; // 是否正在扩散中
    private final ArrayList<Circle> mCircles = new ArrayList<>(); // 圆圈的描述
    private float mCircleStartWidth; // 圆圈的起始直径
    private float mDiffuseSpeed; // 扩散速度，也就是圆圈扩散时的直径一个增加值
    private int mStartAlpha = 255; // 圆圈的起始透明度
    private int mEndAlpha; // 圆圈放到最大时的一个透明度,区间为：0-255
    private float mCircleInterval; // 每个圆圈之间的间隔
    private long mAnimDuration; // 动画持续时间
    private final Paint mPaint = new Paint();
    private long mCurUserId;

    public SoundLevelView(Context context) {
        this(context, null);
    }

    public SoundLevelView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SoundLevelView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs, defStyleAttr);
    }

    private void initView(Context context, AttributeSet attrs, int defStyleAttr) {
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SoundLevelView, defStyleAttr, 0);
        mPaint.setColor(typedArray.getColor(R.styleable.SoundLevelView_slv_circle_color, Color.parseColor("#de00a3")));
        mCircleStartWidth = typedArray.getDimension(R.styleable.SoundLevelView_slv_start_width, 0f);
        mEndAlpha = typedArray.getInt(R.styleable.SoundLevelView_slv_end_alpha, 0);
        mDiffuseSpeed = typedArray.getDimension(
                R.styleable.SoundLevelView_slv_speed,
                DensityUtils.dp2px(context, 1F)
        );
        mCircleInterval = typedArray.getDimension(
                R.styleable.SoundLevelView_slv_circle_interval,
                DensityUtils.dp2px(context, 10F)
        );
        mAnimDuration = typedArray.getInt(R.styleable.SoundLevelView_slv_anim_duration, 1000);
        mPaint.setStrokeWidth(typedArray.getDimension(
                R.styleable.SoundLevelView_slv_stroke_width,
                DensityUtils.dp2px(context, 2F)
        ));
        typedArray.recycle();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (!isDiffuse) return;
        int cx = getWidth() / 2;
        int cy = getHeight() / 2;
        int maxWidth = getWidth();
        ListIterator<Circle> iterator = mCircles.listIterator();
        while (iterator.hasNext()) {
            Circle next = iterator.next();
            mPaint.setAlpha(next.alpha);
            canvas.drawCircle(cx, cy, next.width / 2, mPaint);
            next.width += mDiffuseSpeed;
            float widthPercent = (next.width - mCircleStartWidth) / (maxWidth - mCircleStartWidth);
            next.alpha = (int) (mStartAlpha - mStartAlpha * widthPercent);
            if (next.width > maxWidth) {
                iterator.remove();
            }
        }
        //判断是否需要添加新的圆圈
        if (mCircles.size() == 0) {
            addCircle(0);
        } else {
            Circle firstCircle = mCircles.get(0);
            if (firstCircle.width - mCircleStartWidth > mCircleInterval) {
                addCircle(0);
            }
        }
        postInvalidateDelayed(30);
    }

    private void addCircle(int index) {
        Circle circle = new Circle();
        circle.width = mCircleStartWidth;
        circle.alpha = 255;
        mCircles.add(index, circle);
    }

    public void start() {
        if (isDiffuse) {
            removeCallbacks(mStopTask);
            postDelayed(mStopTask, mAnimDuration);
            return;
        }
        isDiffuse = true;
        addCircle(0);
        invalidate();
        postDelayed(mStopTask, mAnimDuration);
    }

    public void stop() {
        if (!isDiffuse) return;
        isDiffuse = false;
        mCircles.clear();
    }

    public void setCurUserId(long userId) {
        if (mCurUserId != userId) {
            stop();
        }
        mCurUserId = userId;
    }

    public void setCircleStartWidth(float width) {
        mCircleStartWidth = width;
    }

    public void setCircleColor(@ColorInt int color) {
        mPaint.setColor(color);
    }

    public void setEndAlpha(@IntRange(from = 0, to = 255) int alpha) {
        mEndAlpha = alpha;
    }

    public void setDiffuseSpeed(float speed) {
        mDiffuseSpeed = speed;
    }

    public void stCircleInterval(float interval) {
        mCircleInterval = interval;
    }

    public void setAnimDuration(long duration) {
        mAnimDuration = duration;
    }

    private final Runnable mStopTask = new Runnable() {
        @Override
        public void run() {
            stop();
        }
    };

    private static class Circle {
        public float width;
        public int alpha;
    }

}

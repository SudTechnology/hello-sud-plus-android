package tech.sud.mgp.hello.common.widget.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;

import androidx.constraintlayout.widget.ConstraintLayout;

import tech.sud.mgp.hello.R;


/**
 * 圆角布局
 */
public class CornersConstraintLayout extends ConstraintLayout {

    private final boolean leftTopCorner;
    private final boolean rightTopCorner;
    private final boolean leftBottomCorner;
    private final boolean rightBottomCorner;
    private final float[] radiusArray = {0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f};
    private final Path path = new Path();
    private final RectF rectF = new RectF();

    public CornersConstraintLayout(Context context) {
        this(context, null);
    }

    public CornersConstraintLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CornersConstraintLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CornersConstraintLayout, defStyleAttr, 0);
        leftTopCorner = typedArray.getBoolean(R.styleable.CornersConstraintLayout_ccl_leftTopCorner, true);
        rightTopCorner = typedArray.getBoolean(R.styleable.CornersConstraintLayout_ccl_rightTopCorner, true);
        leftBottomCorner = typedArray.getBoolean(R.styleable.CornersConstraintLayout_ccl_leftBottomCorner, true);
        rightBottomCorner = typedArray.getBoolean(R.styleable.CornersConstraintLayout_ccl_rightBottomCorner, true);
        int radius = (int) typedArray.getDimension(R.styleable.CornersConstraintLayout_ccl_cornerRadius, 0);
        
        if (leftTopCorner) {
            radiusArray[0] = radius;
            radiusArray[1] = radius;
        }

        if (rightTopCorner) {
            radiusArray[2] = radius;
            radiusArray[3] = radius;
        }

        if (rightBottomCorner) {
            radiusArray[6] = radius;
            radiusArray[7] = radius;
        }

        if (leftBottomCorner) {
            radiusArray[4] = radius;
            radiusArray[5] = radius;
        }
        typedArray.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        rectF.right = getMeasuredWidth();
        rectF.bottom = getMeasuredHeight();
        path.reset();
        path.addRoundRect(rectF, radiusArray, Path.Direction.CW);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (leftTopCorner || leftBottomCorner || rightTopCorner || rightBottomCorner) {
            canvas.clipPath(path);
        }
        super.onDraw(canvas);
    }


}

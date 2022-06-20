package tech.sud.mgp.hello.common.widget.view;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.blankj.utilcode.util.ImageUtils;

/**
 * 背景所用Drawable，效果是只拉伸中间部分，并在各分辨下保持左右两边部分不变形
 * 1，对图片进行高度的等比例缩放
 * 2，绘制时，取中间像素点进行拉伸
 */
public class XStretchDrawable extends Drawable {

    private Matrix mMatrix = new Matrix();
    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mCenterPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private BitmapShader mCenterBitmapShader;
    private Bitmap bitmap;

    public XStretchDrawable(Bitmap bitmap) {
        this.bitmap = bitmap;
        init();
    }

    /** 图片的拉伸比例 */
    private float mBitmapScale = 1f;

    /** 横向截取图片的位置比 */
    private float mClipRatio = 0.5f;

    private void init() {
        //截取图片中间纵向的一个像素点
        if (!isEmptyBitmap(bitmap)) {
            float centerX = bitmap.getWidth() * mClipRatio;
            if (centerX < 0) {
                centerX = 1f;
            }
            Bitmap centerBitmap = ImageUtils.clip(bitmap, (int) centerX, 0, 1, bitmap.getHeight());
            if (!isEmptyBitmap(centerBitmap)) {
                BitmapShader bitmapShader = new BitmapShader(centerBitmap, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
                mCenterPaint.setShader(bitmapShader);
                mCenterBitmapShader = bitmapShader;
            }
        }
    }

    private boolean isEmptyBitmap(Bitmap src) {
        return src == null || src.getWidth() == 0 || src.getHeight() == 0;
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        int width = getBounds().width();
        int height = getBounds().height();
        if (width == 0 || height == 0 || isEmptyBitmap(bitmap)) {
            return;
        }

        //计算实际显示时，图片中心点x的位置
        float centerX = getIntrinsicWidth() * mClipRatio * mBitmapScale;

        //region 画图片左边部分
        canvas.save();
        canvas.clipRect(0f, 0f, centerX, height);
        canvas.drawBitmap(bitmap, mMatrix, mPaint);
        canvas.restore();
        //endregion 画图片左边部分

        //region 画中间拉伸部分
        canvas.save();
        float centerRightX = width - centerX;
        canvas.clipRect(centerX, 0f, centerRightX, height);
        canvas.drawRect(centerX, 0f, centerRightX, height, mCenterPaint);
        canvas.restore();
        //endregion 画中间拉伸部分

        //region 画最右边部分
        canvas.save();
        canvas.clipRect(centerRightX, 0f, width, height);
        float matrixTranslateX = centerRightX - centerX;
        mMatrix.postTranslate(matrixTranslateX, 0f);
        canvas.drawBitmap(bitmap, mMatrix, mPaint);
        mMatrix.postTranslate(-matrixTranslateX, 0f); //post是属于一个叠加的方法，所以这里将其恢复
        canvas.restore();
        //endregion 画最右边部分
    }

    @Override
    public void setAlpha(int alpha) {
        mPaint.setAlpha(alpha);
        invalidateSelf();
    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {
        mPaint.setColorFilter(colorFilter);
        invalidateSelf();
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    @Override
    public int getIntrinsicWidth() {
        if (bitmap != null) {
            return bitmap.getWidth();
        }
        return 0;
    }

    @Override
    public int getIntrinsicHeight() {
        if (bitmap != null) {
            return bitmap.getHeight();
        }
        return 0;
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
        if (bounds == null) return;
        mBitmapScale = bounds.height() * 1.0f / getIntrinsicHeight();
        mMatrix.setScale(mBitmapScale, mBitmapScale);
        if (mCenterBitmapShader != null) {
            mCenterBitmapShader.setLocalMatrix(mMatrix);
        }
    }

}

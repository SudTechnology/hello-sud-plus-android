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
 * 背景所用Drawable，效果是只拉伸中间部分，并在各分辨下保持上下两边部分不变形
 * 1，对图片进行宽度的等比例缩放
 * 2，绘制时，取中间像素点进行拉伸
 */
public class YStretchDrawable extends Drawable {

    private Matrix mMatrix = new Matrix();
    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mCenterPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private BitmapShader mCenterBitmapShader;
    private Bitmap bitmap;

    public YStretchDrawable(Bitmap bitmap) {
        this.bitmap = bitmap;
        init();
    }

    /** 图片的拉伸比例 */
    private float mBitmapScale = 1f;

    /** 截取图片的位置比 */
    private float mClipRatio = 0.5f;

    private void init() {
        //截取图片中间纵向的一个像素点
        if (!isEmptyBitmap(bitmap)) {
            float centerY = bitmap.getHeight() * mClipRatio;
            if (centerY < 0) {
                centerY = 1f;
            }
            Bitmap centerBitmap = ImageUtils.clip(bitmap, 0, (int) centerY, bitmap.getWidth(), 1);
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

        // 如果缩放之后，bitmap高度大于绘制区域的高度，则按照正常设置背景的方式缓制
        if (getIntrinsicHeight() * mBitmapScale > height) {
            mMatrix.setScale(mBitmapScale, height * 1.0f / getIntrinsicHeight());
            canvas.drawBitmap(bitmap, mMatrix, mPaint);
            return;
        }

        // 计算实际显示时，图片中心点Y的位置
        float centerY = getIntrinsicHeight() * mClipRatio * mBitmapScale;

        // region 画图片上边部分
        canvas.save();
        canvas.clipRect(0f, 0f, width, centerY);
        canvas.drawBitmap(bitmap, mMatrix, mPaint);
        canvas.restore();
        // endregion 画图片上边部分

        // region 画中间拉伸部分
        canvas.save();
        float centerBottomY = height - centerY;
        canvas.clipRect(0f, centerY, width, centerBottomY);
        canvas.drawRect(0f, centerY, width, centerBottomY, mCenterPaint);
        canvas.restore();
        // endregion 画中间拉伸部分

        // region 画底部部分
        canvas.save();
        canvas.clipRect(0, centerBottomY, width, height);
        float matrixTranslateY = centerBottomY - centerY;
        mMatrix.postTranslate(0f, matrixTranslateY);
        canvas.drawBitmap(bitmap, mMatrix, mPaint);
        mMatrix.postTranslate(0f, -matrixTranslateY); //post是属于一个叠加的方法，所以这里将其恢复
        canvas.restore();
        // endregion 画底部部分

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
        mBitmapScale = bounds.width() * 1.0f / getIntrinsicWidth();
        mMatrix.setScale(mBitmapScale, mBitmapScale);
        if (mCenterBitmapShader != null) {
            mCenterBitmapShader.setLocalMatrix(mMatrix);
        }
    }

}

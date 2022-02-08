package tech.sud.mgp.common.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import tech.sud.mgp.common.R;

public class ImageLoader {

    public static void loadImage(ImageView view, String url) {
        if (isDestroy(view)) return;
        Glide.with(view).load(url).into(view);
    }

    public static void loadAvatar(ImageView view, String url) {
        if (isDestroy(view)) return;
        Glide.with(view)
                .load(url)
                .placeholder(R.drawable.common_ic_default_avatar)
                .error(R.drawable.common_ic_default_avatar)
                .into(view);
    }

    public static void loadDrawable(ImageView view, @DrawableRes int drawableResId) {
        if (isDestroy(view)) return;
        Glide.with(view).load(drawableResId).into(view);
    }

    public static void loadSizeGift(View view, @DrawableRes int drawableResId, int size, RequestListener<Drawable> callback) {
        if (isDestroy(view)) return;
        Glide.with(view).asDrawable().load(drawableResId)
                .listener(callback)
                .override(size, size)
                .submit();
    }

    /**
     * 获取固定大小的drawable
     */
    public static Drawable zoomDrawable(Drawable drawable, int w, int h) {
        try {
            int width = drawable.getIntrinsicWidth();
            int height = drawable.getIntrinsicHeight();
            Bitmap oldbmp = drawableToBitmap(drawable);
            Matrix matrix = new Matrix();
            float scaleWidth = (float) w / width;
            float scaleHeight = (float) h / height;
            matrix.postScale(scaleWidth, scaleHeight);
            Bitmap newbmp = Bitmap.createBitmap(
                    oldbmp, 0, 0, width, height,
                    matrix, true
            );
            return new BitmapDrawable(null, newbmp);
        } catch (Exception e) {

        }
        return null;
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }
        Bitmap bitmap;
        int width = Math.max(drawable.getIntrinsicWidth(), 2);
        int height = Math.max(drawable.getIntrinsicHeight(), 2);
        try {
            bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
        } catch (Exception e) {
            e.printStackTrace();
            bitmap = null;
        }
        return bitmap;
    }

    private static boolean isDestroy(View view) {
        Context context = view.getContext();
        if (context instanceof Activity) {
            return ((Activity) context).isDestroyed();
        }
        return false;
    }

}

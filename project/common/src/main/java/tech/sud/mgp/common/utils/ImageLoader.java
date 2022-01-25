package tech.sud.mgp.common.utils;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.DrawableRes;

import com.bumptech.glide.Glide;

public class ImageLoader {

    public static void loadImage(ImageView view, String url) {
        if (isDestroy(view)) return;
        Glide.with(view).load(url).into(view);
    }

    public static void loadAvatar(ImageView view, String url) {
        if (isDestroy(view)) return;
        Glide.with(view).load(url).into(view);
    }

    public static void loadDrawable(ImageView view, @DrawableRes int drawableResId) {
        if (isDestroy(view)) return;
        Glide.with(view).load(drawableResId).into(view);
    }

    private static boolean isDestroy(View view) {
        Context context = view.getContext();
        if (context instanceof Activity) {
            return ((Activity) context).isDestroyed();
        }
        return false;
    }

}

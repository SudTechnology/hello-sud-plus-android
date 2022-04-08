package tech.sud.mgp.hello.common.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.DrawableRes;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestListener;

import tech.sud.mgp.hello.R;

public class ImageLoader {

    // 加载图片
    public static void loadImage(ImageView view, String url) {
        if (isDestroy(view)) return;
        Glide.with(view).load(url).into(view);
    }

    // 加载头像
    public static void loadAvatar(ImageView view, String url) {
        if (isDestroy(view)) return;
        Glide.with(view)
                .load(url)
                .placeholder(R.drawable.ic_default_avatar)
                .error(R.drawable.ic_default_avatar)
                .into(view);
    }

    // 加载本地drawable资源
    public static void loadDrawable(ImageView view, @DrawableRes int drawableResId) {
        if (isDestroy(view)) return;
        Glide.with(view).load(drawableResId).into(view);
    }

    // 加载礼物图片
    public static void loadSizeGift(View view, @DrawableRes int drawableResId, int size, RequestListener<Drawable> callback) {
        if (isDestroy(view)) return;
        Glide.with(view).asDrawable().load(drawableResId)
                .listener(callback)
                .override(size, size)
                .submit();
    }

    // 加载游戏图片
    public static void loadGameCover(ImageView view, String url) {
        if (isDestroy(view)) return;
        Glide.with(view)
                .load(url)
                .placeholder(R.drawable.icon_game_default)
                .error(R.drawable.icon_game_default)
                .into(view);
    }

    // 加载场景图片
    public static void loadSceneCover(ImageView view, String url) {
        if (isDestroy(view)) return;
        Glide.with(view)
                .load(url)
                .placeholder(R.drawable.icon_scene_default)
                .error(R.drawable.icon_scene_default)
                .into(view);
    }

    // 判断是否已销毁
    private static boolean isDestroy(View view) {
        Context context = view.getContext();
        if (context instanceof Activity) {
            return ((Activity) context).isDestroyed();
        }
        return false;
    }

}

package tech.sud.mgp.hello.common.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.widget.view.CustomColorDrawable;


public class ImageLoader {

    // 加载图片
    public static void loadImage(ImageView view, String url) {
        if (isDestroy(view)) return;
        Glide.with(view).load(url).into(view);
    }

    // 加载nft图片
    public static void loadNftImage(ImageView view, String url, CustomColorDrawable backgroundDrawable) {
        loadNftImage(view, url, backgroundDrawable, null);
    }

    // 加载nft图片
    public static void loadNftImage(ImageView view, String url, CustomColorDrawable backgroundDrawable, RequestListener<Drawable> listener) {
        if (isDestroy(view)) return;
        Drawable background = view.getBackground();
        if (background instanceof CustomColorDrawable) {
            CustomColorDrawable customColorDrawable = (CustomColorDrawable) background;
            customColorDrawable.stop();
        }

        if (TextUtils.isEmpty(url)) {
            ImageLoader.loadDrawable(view, R.drawable.ic_nft_nonsupport);
            return;
        }

        view.setBackground(backgroundDrawable);
        ColorDrawable placeholderDrawable = new ColorDrawable(Color.parseColor("#00000000"));

        Glide.with(view).load(url).placeholder(placeholderDrawable)
                .error(placeholderDrawable)
                .addListener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        if (listener != null) {
                            listener.onLoadFailed(e, model, target, isFirstResource);
                        }
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        if (backgroundDrawable != null) {
                            backgroundDrawable.stop();
                        }
                        if (listener != null) {
                            listener.onResourceReady(resource, model, target, dataSource, isFirstResource);
                        }
                        return false;
                    }
                }).into(view);
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

    // 加载礼物图片
    public static void loadSizeGift(View view, String url, int size, RequestListener<Drawable> callback) {
        if (isDestroy(view)) return;
        Glide.with(view).asDrawable().load(url)
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

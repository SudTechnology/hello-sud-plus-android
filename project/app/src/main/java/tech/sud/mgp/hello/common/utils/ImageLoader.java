package tech.sud.mgp.hello.common.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.CustomViewTarget;
import com.bumptech.glide.request.transition.Transition;

import java.io.File;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.widget.view.XStretchDrawable;

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

    /** 加载中间像素点拉伸，两边保持等比的图像作为背景 */
    public static void loadXStretchBackground(View view, String path) {
        if (isDestroy(view)) return;
        int maxSize;
        if (view.getMeasuredWidth() <= 0) {
            maxSize = DensityUtils.dp2px(375);
        } else {
            maxSize = view.getMeasuredWidth();
        }
        Glide.with(view.getContext())
                .asFile()
                .load(path)
                .into(new CustomViewTarget<View, File>(view) {
                    @Override
                    public void onLoadFailed(@Nullable Drawable errorDrawable) {
                        view.setBackground(errorDrawable);
                    }

                    @Override
                    public void onResourceReady(@NonNull File resource, @Nullable Transition<? super File> transition) {
                        Bitmap bitmap = ImageUtils.getBitmap(resource, maxSize, maxSize);
                        if (bitmap != null) {
                            view.setBackground(new XStretchDrawable(bitmap));
                        }
                    }

                    @Override
                    protected void onResourceCleared(@Nullable Drawable placeholder) {
                        
                    }
                });
    }

}

package tech.sud.mgp.hello.common.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.vectordrawable.graphics.drawable.Animatable2Compat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.integration.webp.decoder.WebpDrawable;
import com.bumptech.glide.integration.webp.decoder.WebpDrawableTransformation;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.bitmap.CenterInside;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import tech.sud.mgp.hello.ui.scenes.common.gift.listener.PlayResultListener;
import tech.sud.mgp.hello.ui.scenes.common.gift.model.PlayResult;

public class WebPUtils {

    /**
     * 加载本地资源webp
     *
     * @param resId     本地资源id
     * @param loopCount -1为循环播放，大于0为次数
     */
    public static void loadWebP(ImageView iv, int resId, int loopCount, PlayResultListener listener) {
        if (isDestroy(iv)) return;
        Glide.with(iv).clear(iv);
        // webp动图
        Transformation<Bitmap> transformation = new CenterInside();
        Glide.with(iv)
                .load(resId)
                .optionalTransform(transformation)
                .optionalTransform(WebpDrawable.class, new WebpDrawableTransformation(transformation))
                .addListener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        if (listener != null) {
                            listener.onResult(PlayResult.PLAYERROR);
                        }
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        if (resource instanceof WebpDrawable) {
                            WebpDrawable webpDrawable = (WebpDrawable) resource;
                            if (loopCount > 1) {
                                webpDrawable.setLoopCount(loopCount);
                            } else if (loopCount == 0) { // 需要设置为循环1次才会有onAnimationEnd回调
                                webpDrawable.setLoopCount(1);
                            } else {
                                webpDrawable.setLoopCount(loopCount);
                            }
                            webpDrawable.registerAnimationCallback(new Animatable2Compat.AnimationCallback() {
                                @Override
                                public void onAnimationStart(Drawable drawable) {
                                    super.onAnimationStart(drawable);
                                    if (listener != null) {
                                        listener.onResult(PlayResult.START);
                                    }
                                }

                                @Override
                                public void onAnimationEnd(Drawable drawable) {
                                    super.onAnimationEnd(drawable);
                                    webpDrawable.unregisterAnimationCallback(this);
                                    if (listener != null) {
                                        listener.onResult(PlayResult.PLAYEND);
                                    }
                                }
                            });
                        }
                        return false;
                    }
                })
                .into(iv);
    }

    /**
     * 加载webp网络资源
     *
     * @param url       播放地址
     * @param loopCount -1为循环播放，大于0为次数
     */
    public static void loadWebP(ImageView iv, String url, int loopCount) {
        if (isDestroy(iv)) return;
        loadWebP(iv, loopCount, Glide.with(iv).load(url));
    }

    private static void loadWebP(ImageView iv, int loopCount, RequestBuilder<Drawable> builder) {
        Glide.with(iv).clear(iv);
        Transformation<Bitmap> transform = new CenterInside();
        WebpDrawableTransformation webpDrawableTransformation = new WebpDrawableTransformation(transform);
        builder.addListener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        if (resource instanceof WebpDrawable) {
                            WebpDrawable webpDrawable = (WebpDrawable) resource;
                            try {
                                if (!webpDrawable.isRunning()) {
                                    webpDrawable.setLoopCount(loopCount);
                                    webpDrawable.start();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        return false;
                    }
                }).optionalTransform(transform)
                .optionalTransform(WebpDrawable.class, webpDrawableTransformation)
                .skipMemoryCache(true)
                .into(iv);
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

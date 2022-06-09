package tech.sud.mgp.hello.common.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;
import androidx.annotation.RawRes;
import androidx.vectordrawable.graphics.drawable.Animatable2Compat;

import com.bumptech.glide.Glide;
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
     * @param imageView imageView
     * @param resId     资源id
     * @param loopCount -1为循环播放，大于0为次数
     * @param listener  播放监听
     */
    public static void loadWebp(ImageView imageView, @RawRes @DrawableRes int resId, int loopCount, PlayResultListener listener) {
        Context context = imageView.getContext();
        if (context instanceof Activity) {
            if (((Activity) context).isDestroyed()) {
                return;
            }
        }
        //webp动图
        Transformation<Bitmap> transformation = new CenterInside();
        Glide.with(imageView)
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
                        WebpDrawable webpDrawable = (WebpDrawable) resource;
                        // 需要设置为有限循环次数才会有onAnimationEnd回调
                        webpDrawable.setLoopCount(loopCount);
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
                        return false;
                    }
                })
                .into(imageView);
    }

}

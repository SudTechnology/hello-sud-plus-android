package tech.sud.mgp.audio.gift.manager.stategy;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import androidx.annotation.Nullable;
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

import tech.sud.mgp.audio.gift.listener.PlayResultListener;
import tech.sud.mgp.audio.gift.model.PlayResult;

public class GiftWebpStrategy extends PlayStrategy<GiftWebpModel> {
    @Override
    public void play(GiftWebpModel giftWebpModel) {
        loadWebp(giftWebpModel.imageView, giftWebpModel.getResId(), giftWebpModel.getPlayResultListener());
    }

    private void loadWebp(ImageView imageView, int resId, PlayResultListener listener) {
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
                        //需要设置为循环1次才会有onAnimationEnd回调
                        webpDrawable.setLoopCount(1);
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

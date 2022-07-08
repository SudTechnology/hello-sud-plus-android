package tech.sud.mgp.hello.common.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;
import androidx.annotation.RawRes;

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

public class WebPUtils {

    /**
     * 加载webp网络资源
     *
     * @param resId     本地资源id
     * @param loopCount -1为循环播放，大于0为次数
     */
    public static void loadWebP(ImageView iv, @RawRes @DrawableRes int resId, int loopCount) {
        if (isDestroy(iv)) return;
        loadWebP(iv, loopCount, Glide.with(iv).load(resId));
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

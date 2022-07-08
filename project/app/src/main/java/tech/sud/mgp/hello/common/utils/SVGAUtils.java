package tech.sud.mgp.hello.common.utils;

import android.app.Activity;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;

import com.opensource.svgaplayer.SVGACallback;
import com.opensource.svgaplayer.SVGADrawable;
import com.opensource.svgaplayer.SVGAImageView;
import com.opensource.svgaplayer.SVGAParser;
import com.opensource.svgaplayer.SVGAVideoEntity;

import java.net.MalformedURLException;
import java.net.URL;

import tech.sud.mgp.hello.ui.scenes.common.gift.listener.PlayResultListener;
import tech.sud.mgp.hello.ui.scenes.common.gift.model.PlayResult;

/**
 * svga工具
 */
public class SVGAUtils {

    /**
     * 通过url加载svga
     *
     * @param lifecycle 生命周期，可传空，最好传
     * @return 可在返回对象当中操作是否要取消加载
     */
    public static LoadSVGAFuture loadByUrl(Lifecycle lifecycle, String url, SVGAImageView imageView, PlayResultListener listener) throws MalformedURLException {
        LoadSVGAFuture future = new LoadSVGAFuture();
        if (lifecycle != null && lifecycle.getCurrentState() == Lifecycle.State.DESTROYED) {
            return future;
        }
        if (listener != null) {
            listener.onResult(PlayResult.START);
        }
        SVGAParser parse = new SVGAParser(imageView.getContext());
        parse.decodeFromURL(new URL(url), new SVGAParser.ParseCompletion() {
            @Override
            public void onComplete(@NonNull SVGAVideoEntity videoItem) {
                if (lifecycle != null) {
                    if (lifecycle.getCurrentState() == Lifecycle.State.DESTROYED) {
                        future.isCanceled = true;
                        return;
                    }
                } else {
                    Context context = imageView.getContext();
                    if (context == null || (context instanceof Activity && ((Activity) context).isDestroyed())) {
                        future.isCanceled = true;
                        return;
                    }
                }
                if (future.isCanceled) {
                    return;
                }
                SVGADrawable drawable = new SVGADrawable(videoItem);
                imageView.setImageDrawable(drawable);
                imageView.startAnimation();
                future.isCompleted = true;
                imageView.setCallback(new SVGACallback() {
                    @Override
                    public void onPause() {
                    }

                    @Override
                    public void onFinished() {
                        imageView.stopAnimation();
                        imageView.setImageDrawable(null);
                        if (listener != null) {
                            listener.onResult(PlayResult.PLAYEND);
                        }
                    }

                    @Override
                    public void onRepeat() {
                    }

                    @Override
                    public void onStep(int i, double v) {
                    }
                });
            }

            @Override
            public void onError() {
                if (listener != null) {
                    listener.onResult(PlayResult.PLAYERROR);
                }
            }
        }, null);
        return future;
    }

    public static class LoadSVGAFuture {
        boolean isCompleted = false;
        boolean isCanceled = false;

        public void cancel() {
            isCanceled = true;
        }
    }

}

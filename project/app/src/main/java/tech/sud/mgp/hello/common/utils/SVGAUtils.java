package tech.sud.mgp.hello.common.utils;

import android.app.Activity;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;

import com.opensource.svgaplayer.SVGADrawable;
import com.opensource.svgaplayer.SVGAImageView;
import com.opensource.svgaplayer.SVGAParser;
import com.opensource.svgaplayer.SVGAVideoEntity;

import java.net.MalformedURLException;
import java.net.URL;

import tech.sud.mgp.hello.ui.common.utils.CompletedListener;

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
    public static LoadSVGAFuture loadByUrl(Lifecycle lifecycle, String url, SVGAImageView imageView, CompletedListener listener) throws MalformedURLException {
        LoadSVGAFuture future = new LoadSVGAFuture();
        if (lifecycle != null && lifecycle.getCurrentState() == Lifecycle.State.DESTROYED) {
            return future;
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
                if (listener != null) {
                    listener.onCompleted();
                }
                SVGADrawable drawable = new SVGADrawable(videoItem);
                imageView.setImageDrawable(drawable);
                imageView.startAnimation();
                future.isCompleted = true;
            }

            @Override
            public void onError() {
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

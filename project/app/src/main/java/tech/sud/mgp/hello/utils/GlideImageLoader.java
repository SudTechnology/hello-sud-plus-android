package tech.sud.mgp.hello.utils;

import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class GlideImageLoader {

    public static void loadImage(ImageView view,String url){
        Glide.with(view).load(url).into(view);
    }

}

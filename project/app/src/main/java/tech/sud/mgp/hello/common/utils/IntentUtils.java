package tech.sud.mgp.hello.common.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class IntentUtils {

    /**
     * 调用外部浏览器打开Url
     *
     * @param context
     * @param url
     */
    public static void openUrl(Context context, String url) {
        if (url == null || url.length() == 0) return;
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        context.startActivity(intent);
    }

}

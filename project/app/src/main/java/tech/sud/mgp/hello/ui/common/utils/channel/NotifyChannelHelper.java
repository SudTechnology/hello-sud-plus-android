package tech.sud.mgp.hello.ui.common.utils.channel;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationManagerCompat;

/**
 * 通知栏通道助手
 */
public class NotifyChannelHelper {

    public static String SCENEROOM_NOTIFICATION_CHANNEL_ID = "sceneRoom";
    public static String SCENEROOM_NOTIFICATION_CHANNEL_NAME = "sceneRoom";

    /**
     * android8.0及以后的通知栏都需要初始化通道
     */
    public void initChannel(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return;
        NotificationManagerCompat from = NotificationManagerCompat.from(context);
        NotificationChannel sceneRoomChannel = getSceneRoomChannel();
        from.createNotificationChannel(sceneRoomChannel);
    }

    /**
     * 获取场景房间内的通知渠道配置
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public NotificationChannel getSceneRoomChannel() {
        return new NotificationChannel(SCENEROOM_NOTIFICATION_CHANNEL_ID, SCENEROOM_NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_LOW);
    }

}

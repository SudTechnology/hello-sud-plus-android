package tech.sud.mgp.hello.ui.scenes.base.utils;

import android.app.Activity;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.ui.common.activity.MyTranslucentActivity;
import tech.sud.mgp.hello.ui.common.constant.RequestKey;
import tech.sud.mgp.hello.ui.common.utils.channel.NotifyChannelHelper;
import tech.sud.mgp.hello.ui.common.utils.channel.NotifyId;

/**
 * 场景房间内的通知栏助手，用于显示或隐藏通知栏
 */
public class SceneRoomNotificationHelper {

    private int notifyId = NotifyId.SCENE_ROOM_NOTIFY_ID.getValue();
    private Context context;
    private String roomName;
    private Class<? extends Activity> startClass;

    public SceneRoomNotificationHelper(Context context) {
        this.context = context;
    }

    /**
     * 显示通知栏
     */
    public void show() {
        try {
            Notification notification = createNotification();
            notification.flags = notification.flags | Notification.FLAG_NO_CLEAR;
            NotificationManagerCompat from = NotificationManagerCompat.from(context);
            from.notify(notifyId, notification);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 隐藏通知栏
     */
    public void hide() {
        try {
            NotificationManagerCompat new_nm = NotificationManagerCompat.from(context);
            new_nm.cancel(notifyId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 创建一个通知栏对象
     *
     * @return
     */
    public Notification createNotification() {
        Intent intent;
        if (startClass == null) {
            intent = new Intent(context, MyTranslucentActivity.class);
        } else {
            intent = new Intent(context, startClass);
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Bundle bundle = new Bundle();
        bundle.putBoolean(RequestKey.KEY_IS_PENDING_INTENT, true);
        intent.putExtras(bundle);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        String ticker = context.getString(R.string.app_name);
        String desc = roomName;
        return new NotificationCompat.Builder(context, NotifyChannelHelper.SCENEROOM_NOTIFICATION_CHANNEL_ID)
                .setContentTitle(ticker)
                .setContentText(desc)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setContentIntent(pendingIntent)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher))
                .setTicker(ticker)
                .build();
    }

    // 设置房间名称
    public void setRoomName(String roomName) {
        this.roomName = roomName;
        show();
    }

    // 设置点击通知栏需要启动的页面
    public void setStartClass(Class<? extends Activity> startClass) {
        this.startClass = startClass;
        show();
    }
}

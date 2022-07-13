package tech.sud.mgp.hello.ui.scenes.base.utils;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.app.APPConfig;
import tech.sud.mgp.hello.ui.main.constant.SceneType;
import tech.sud.mgp.hello.ui.scenes.base.model.RoleType;
import tech.sud.mgp.hello.ui.scenes.base.model.RoomInfoModel;
import tech.sud.mgp.hello.ui.scenes.custom.CustomActivity;

public class EnterRoomUtils {

    private static boolean isRunning = false;

    /**
     * 进入房间
     *
     * @param context 上下文
     * @param roomId  房间id
     */
    public static void enterRoom(Context context, long roomId, long gameId) {
        RoomInfoModel model = new RoomInfoModel();
        model.roomId = roomId;
        model.roomNumber = roomId + "";
        model.roomName = context.getString(R.string.room_name);
        model.gameId = gameId;
        model.roleType = RoleType.OWNER;
        model.rtcToken = APPConfig.ZEGO_TOKEN;
        Intent intent = getSceneIntent(context, SceneType.CUSTOM_SCENE);
        intent.putExtra("RoomInfoModel", model);
        context.startActivity(intent);
    }

    @NonNull
    private static Intent getSceneIntent(Context context, int sceneType) {
        return new Intent(context, CustomActivity.class);
    }

}

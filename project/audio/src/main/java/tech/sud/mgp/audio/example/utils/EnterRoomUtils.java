package tech.sud.mgp.audio.example.utils;

import android.content.Context;
import android.content.Intent;

import tech.sud.mgp.audio.example.activity.AudioRoomActivity;
import tech.sud.mgp.audio.example.model.RoomInfoModel;

public class EnterRoomUtils {

    public static void enterRoom(Context context, long roomId, String roomName, long gameId) {
        RoomInfoModel model = new RoomInfoModel();
        model.roomId = roomId;
        model.roomName = roomName;
        model.gameId = gameId;
        enterRoom(context, model);
    }

    public static void enterRoom(Context context, RoomInfoModel model) {
        Intent intent = new Intent(context, AudioRoomActivity.class);
        intent.putExtra("RoomInfoModel", model);
        context.startActivity(intent);
    }

}

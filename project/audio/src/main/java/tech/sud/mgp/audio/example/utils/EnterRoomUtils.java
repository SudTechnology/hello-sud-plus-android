package tech.sud.mgp.audio.example.utils;

import android.content.Context;
import android.content.Intent;

import androidx.lifecycle.LifecycleOwner;

import com.blankj.utilcode.util.ToastUtils;

import tech.sud.mgp.audio.BuildConfig;
import tech.sud.mgp.audio.example.activity.AudioRoomActivity;
import tech.sud.mgp.audio.example.http.repository.AudioRepository;
import tech.sud.mgp.audio.example.http.response.EnterRoomResp;
import tech.sud.mgp.audio.example.model.RoomInfoModel;
import tech.sud.mgp.common.http.param.BaseResponse;
import tech.sud.mgp.common.http.param.RetCode;
import tech.sud.mgp.common.http.rx.RxCallback;

public class EnterRoomUtils {

    private static boolean isRunning = false;

    public static void enterRoom(Context context, long roomId) {
        if (isRunning) {
            return;
        }
        isRunning = true;
        LifecycleOwner owner = null;
        if (context instanceof LifecycleOwner) {
            owner = (LifecycleOwner) context;
        }
        AudioRepository.enterRoom(owner, roomId, new RxCallback<EnterRoomResp>() {
            @Override
            public void onNext(BaseResponse<EnterRoomResp> t) {
                super.onNext(t);
                EnterRoomResp resp = t.getData();
                if (t.getRetCode() == RetCode.SUCCESS) {
                    if (resp != null) {
                        startAudioRoomActivity(context, resp.roomId, resp.roomName, resp.gameId);
                    }
                } else {
                    ToastUtils.showLong("fail:" + t.getRetCode());
                }
                isRunning = false;
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                isRunning = false;
            }
        });
        if (BuildConfig.DEBUG) {
            startAudioRoomActivity(context, 11, "asdfasdf", 0);
        }
    }

    private static void startAudioRoomActivity(Context context, long roomId, String roomName, long gameId) {
        RoomInfoModel model = new RoomInfoModel();
        model.roomId = roomId;
        model.roomName = roomName;
        model.gameId = gameId;
        Intent intent = new Intent(context, AudioRoomActivity.class);
        intent.putExtra("RoomInfoModel", model);
        context.startActivity(intent);
    }

}

package tech.sud.mgp.hello.ui.scenes.audio.utils;

import android.content.Context;
import android.content.Intent;

import androidx.lifecycle.LifecycleOwner;

import com.blankj.utilcode.util.ToastUtils;

import tech.sud.mgp.hello.ui.scenes.audio.activity.AudioRoomActivity;
import tech.sud.mgp.hello.service.room.repository.AudioRepository;
import tech.sud.mgp.hello.service.room.response.EnterRoomResp;
import tech.sud.mgp.hello.ui.scenes.audio.model.RoomInfoModel;
import tech.sud.mgp.hello.common.http.param.BaseResponse;
import tech.sud.mgp.hello.common.http.param.RetCode;
import tech.sud.mgp.hello.common.http.rx.RxCallback;
import tech.sud.mgp.hello.common.utils.ResponseUtils;

public class EnterRoomUtils {

    private static boolean isRunning = false;

    /**
     * 进入房间
     *
     * @param context 上下文
     * @param roomId  房间id
     */
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
                        startAudioRoomActivity(context, resp.roomId, resp.roomName, resp.gameId, resp.roleType);
                    }
                } else {
                    ToastUtils.showLong(ResponseUtils.conver(t));
                }
                isRunning = false;
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                isRunning = false;
            }
        });
    }

    private static void startAudioRoomActivity(Context context, long roomId, String roomName, long gameId, int roleType) {
        RoomInfoModel model = new RoomInfoModel();
        model.roomId = roomId;
        model.roomName = roomName;
        model.gameId = gameId;
        model.roleType = roleType;
        Intent intent = new Intent(context, AudioRoomActivity.class);
        intent.putExtra("RoomInfoModel", model);
        context.startActivity(intent);
    }

}

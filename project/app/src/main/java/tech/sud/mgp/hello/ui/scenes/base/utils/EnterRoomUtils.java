package tech.sud.mgp.hello.ui.scenes.base.utils;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;

import tech.sud.mgp.hello.common.http.param.BaseResponse;
import tech.sud.mgp.hello.common.http.param.RetCode;
import tech.sud.mgp.hello.common.http.rx.RxCallback;
import tech.sud.mgp.hello.service.room.repository.RoomRepository;
import tech.sud.mgp.hello.service.room.response.EnterRoomResp;
import tech.sud.mgp.hello.ui.main.constant.SceneType;
import tech.sud.mgp.hello.ui.scenes.asr.ASRActivity;
import tech.sud.mgp.hello.ui.scenes.audio.activity.AudioRoomActivity;
import tech.sud.mgp.hello.ui.scenes.base.model.EnterRoomParams;
import tech.sud.mgp.hello.ui.scenes.base.model.RoomInfoModel;
import tech.sud.mgp.hello.ui.scenes.crossroom.activity.CrossRoomActivity;
import tech.sud.mgp.hello.ui.scenes.custom.CustomActivity;
import tech.sud.mgp.hello.ui.scenes.orderentertainment.OrderEntertainmentActivity;
import tech.sud.mgp.hello.ui.scenes.ticket.activity.TicketActivity;

public class EnterRoomUtils {

    private static boolean isRunning = false;

    /**
     * 进入房间
     *
     * @param context 上下文
     * @param roomId  房间id
     */
    public static void enterRoom(Context context, long roomId) {
        EnterRoomParams params = new EnterRoomParams();
        params.roomId = roomId;
        enterRoom(context, params);
    }

    /**
     * 进入房间
     *
     * @param context 上下文
     */
    public static void enterRoom(Context context, EnterRoomParams params) {
        if (isRunning) {
            return;
        }
        isRunning = true;
        LifecycleOwner owner = null;
        if (context instanceof LifecycleOwner) {
            owner = (LifecycleOwner) context;
        }
        RoomRepository.enterRoom(owner, params.roomId, new RxCallback<EnterRoomResp>() {
            @Override
            public void onNext(BaseResponse<EnterRoomResp> t) {
                super.onNext(t);
                EnterRoomResp resp = t.getData();
                if (t.getRetCode() == RetCode.SUCCESS) {
                    if (resp != null) {
                        startSceneRoomActivity(context, resp);
                    }
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

    /**
     * 打开场景页面
     *
     * @param context 上下文
     */
    private static void startSceneRoomActivity(Context context, EnterRoomResp enterRoomResp) {
        RoomInfoModel model = new RoomInfoModel();
        model.sceneType = enterRoomResp.sceneType;
        model.roomId = enterRoomResp.roomId;
        model.roomNumber = enterRoomResp.roomNumber;
        model.roomName = enterRoomResp.roomName;
        model.gameId = enterRoomResp.gameId;
        model.roleType = enterRoomResp.roleType;
        model.rtcToken = enterRoomResp.rtcToken;
        model.rtiToken = enterRoomResp.rtiToken;
        model.imToken = enterRoomResp.imToken;
        model.gameLevel = enterRoomResp.gameLevel;
        model.roomPkModel = enterRoomResp.pkResultVO;
        Intent intent = getSceneIntent(context, enterRoomResp.sceneType);
        intent.putExtra("RoomInfoModel", model);
        context.startActivity(intent);
    }

    @NonNull
    private static Intent getSceneIntent(Context context, int sceneType) {
        // TODO: 2022/4/2 完善对应场景之后再放开
        switch (sceneType) {
            case SceneType.ASR:
                return new Intent(context, ASRActivity.class);
            case SceneType.TICKET:
                return new Intent(context, TicketActivity.class);
            case SceneType.ORDER_ENTERTAINMENT:
                return new Intent(context, OrderEntertainmentActivity.class);
            case SceneType.CUSTOM_SCENE:
                return new Intent(context, CustomActivity.class);
            case SceneType.CROSS_ROOM:
                return new Intent(context, CrossRoomActivity.class);
//            case SceneType.TALENT:
//                return new Intent(context, TalentRoomActivity.class);
//            case SceneType.ONE_ONE:
//                return new Intent(context, OneOneActivity.class);
//            case SceneType.QUIZ:
//                return new Intent(context, QuizActivity.class);
//            case SceneType.SHOW:
//                return new Intent(context, ShowActivity.class);
            case SceneType.AUDIO:
            default:
                return new Intent(context, AudioRoomActivity.class);
        }
    }

}

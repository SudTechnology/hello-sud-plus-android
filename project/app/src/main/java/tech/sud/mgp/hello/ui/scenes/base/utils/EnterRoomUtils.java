package tech.sud.mgp.hello.ui.scenes.base.utils;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;

import com.blankj.utilcode.util.ToastUtils;

import tech.sud.mgp.hello.common.http.param.BaseResponse;
import tech.sud.mgp.hello.common.http.param.RetCode;
import tech.sud.mgp.hello.common.http.rx.RxCallback;
import tech.sud.mgp.hello.common.utils.ResponseUtils;
import tech.sud.mgp.hello.service.room.repository.AudioRepository;
import tech.sud.mgp.hello.service.room.response.EnterRoomResp;
import tech.sud.mgp.hello.ui.main.constant.SceneType;
import tech.sud.mgp.hello.ui.scenes.asr.ASRActivity;
import tech.sud.mgp.hello.ui.scenes.audio.activity.AudioRoomActivity;
import tech.sud.mgp.hello.ui.scenes.base.model.RoomInfoModel;
import tech.sud.mgp.hello.ui.scenes.crossroom.CrossRoomActivity;
import tech.sud.mgp.hello.ui.scenes.oneone.OneOneActivity;
import tech.sud.mgp.hello.ui.scenes.orderentertainment.OrderEntertainmentActivity;
import tech.sud.mgp.hello.ui.scenes.quiz.QuizActivity;
import tech.sud.mgp.hello.ui.scenes.show.ShowActivity;
import tech.sud.mgp.hello.ui.scenes.talent.TalentRoomActivity;
import tech.sud.mgp.hello.ui.scenes.ticket.TicketActivity;

public class EnterRoomUtils {

    private static boolean isRunning = false;

    /**
     * 进入房间
     *
     * @param context 上下文
     * @param roomId  房间id
     */
    public static void enterRoom(Context context, long roomId) {
        enterRoom(context, roomId, SceneType.UNDEFINED);
    }

    /**
     * 进入房间
     *
     * @param context   上下文
     * @param roomId    房间id
     * @param sceneType 场景类型 {@link SceneType}
     */
    public static void enterRoom(Context context, long roomId, int sceneType) {
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
                        startSceneRoomActivity(context, resp.roomId, resp.roomName, resp.gameId, resp.roleType, sceneType);
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

    /**
     * 打开场景页面
     *
     * @param context
     * @param roomId
     * @param roomName
     * @param gameId
     * @param roleType
     * @param sceneType
     */
    private static void startSceneRoomActivity(Context context, long roomId, String roomName,
                                               long gameId, int roleType, int sceneType) {
        RoomInfoModel model = new RoomInfoModel();
        model.roomId = roomId;
        model.roomName = roomName;
        model.gameId = gameId;
        model.roleType = roleType;
        Intent intent = getSceneIntent(context, sceneType);
        intent.putExtra("RoomInfoModel", model);
        context.startActivity(intent);
    }

    @NonNull
    private static Intent getSceneIntent(Context context, int sceneType) {
        switch (sceneType) {
            case SceneType.ASR:
                return new Intent(context, ASRActivity.class);
            case SceneType.CROSS_ROOM:
                return new Intent(context, CrossRoomActivity.class);
            case SceneType.ONE_ONE:
                return new Intent(context, OneOneActivity.class);
            case SceneType.ORDER_ENTERTAINMENT:
                return new Intent(context, OrderEntertainmentActivity.class);
            case SceneType.QUIZ:
                return new Intent(context, QuizActivity.class);
            case SceneType.SHOW:
                return new Intent(context, ShowActivity.class);
            case SceneType.TALENT:
                return new Intent(context, TalentRoomActivity.class);
            case SceneType.TICKET:
                return new Intent(context, TicketActivity.class);
            case SceneType.AUDIO:
            default:
                return new Intent(context, AudioRoomActivity.class);
        }
    }

}
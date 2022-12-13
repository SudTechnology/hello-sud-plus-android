package tech.sud.mgp.hello.ui.scenes.base.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.jeremyliao.liveeventbus.LiveEventBus;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.event.LiveEventBusKey;
import tech.sud.mgp.hello.common.event.model.EnterRoomEvent;
import tech.sud.mgp.hello.common.http.param.BaseResponse;
import tech.sud.mgp.hello.common.http.param.RetCode;
import tech.sud.mgp.hello.common.http.rx.RxCallback;
import tech.sud.mgp.hello.common.model.AppData;
import tech.sud.mgp.hello.service.main.config.BaseRtcConfig;
import tech.sud.mgp.hello.service.main.config.ZegoConfig;
import tech.sud.mgp.hello.service.room.repository.RoomRepository;
import tech.sud.mgp.hello.service.room.resp.EnterRoomResp;
import tech.sud.mgp.hello.ui.common.utils.CompletedListener;
import tech.sud.mgp.hello.ui.common.utils.LifecycleUtils;
import tech.sud.mgp.hello.ui.main.base.constant.SceneType;
import tech.sud.mgp.hello.ui.scenes.asr.ASRActivity;
import tech.sud.mgp.hello.ui.scenes.audio.activity.AudioRoomActivity;
import tech.sud.mgp.hello.ui.scenes.base.model.EnterRoomParams;
import tech.sud.mgp.hello.ui.scenes.base.model.RoomInfoModel;
import tech.sud.mgp.hello.ui.scenes.crossapp.activity.CrossAppAuthActivity;
import tech.sud.mgp.hello.ui.scenes.crossapp.activity.CrossAppMatchActivity;
import tech.sud.mgp.hello.ui.scenes.crossroom.activity.CrossRoomActivity;
import tech.sud.mgp.hello.ui.scenes.custom.CustomActivity;
import tech.sud.mgp.hello.ui.scenes.danmaku.activity.DanmakuActivity;
import tech.sud.mgp.hello.ui.scenes.disco.activity.DiscoActivity;
import tech.sud.mgp.hello.ui.scenes.league.activity.LeagueActivity;
import tech.sud.mgp.hello.ui.scenes.orderentertainment.OrderEntertainmentActivity;
import tech.sud.mgp.hello.ui.scenes.quiz.activity.QuizActivity;
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

        long roomId = params.roomId;
        EnterRoomEvent enterRoomEvent = new EnterRoomEvent();
        enterRoomEvent.roomId = roomId;
        LiveEventBus.<EnterRoomEvent>get(LiveEventBusKey.KEY_ENTER_ROOM).post(enterRoomEvent);

        LifecycleOwner owner = null;
        if (context instanceof LifecycleOwner) {
            owner = (LifecycleOwner) context;
        }
        RoomRepository.enterRoom(owner, roomId, new RxCallback<EnterRoomResp>() {
            @Override
            public void onNext(BaseResponse<EnterRoomResp> t) {
                super.onNext(t);
                EnterRoomResp resp = t.getData();
                if (t.getRetCode() == RetCode.SUCCESS) {
                    if (resp != null) {
                        if (canEnterRoom(resp)) {
                            safeStartSceneRoomActivity(context, params, resp);
                        }
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

    private static boolean canEnterRoom(EnterRoomResp resp) {
        if (resp.sceneType == SceneType.DANMAKU) {
            // TODO: 2022/6/16 弹幕游戏目前只支持即构RTC
            BaseRtcConfig rtcConfig = AppData.getInstance().getSelectRtcConfig();
            if (!(rtcConfig instanceof ZegoConfig)) {
                ToastUtils.showLong(R.string.coming_soon);
                return false;
            }
        }
        return true;
    }

    /** 安全地打开场景房间 */
    private static void safeStartSceneRoomActivity(Context context, EnterRoomParams enterRoomParams, EnterRoomResp resp) {
        if (context == null) {
            Activity topActivity = ActivityUtils.getTopActivity();
            if (topActivity instanceof LifecycleOwner) {
                LifecycleUtils.safeLifecycle((LifecycleOwner) topActivity, new CompletedListener() {
                    @Override
                    public void onCompleted() {
                        startSceneRoomActivity(topActivity, enterRoomParams, resp);
                    }
                });
            }
        } else {
            startSceneRoomActivity(context, enterRoomParams, resp);
        }
    }

    /**
     * 打开场景页面
     *
     * @param context 上下文
     */
    private static void startSceneRoomActivity(Context context, EnterRoomParams enterRoomParams, EnterRoomResp enterRoomResp) {
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
        model.streamId = enterRoomResp.streamId;
        if (enterRoomParams.crossAppModel == null) {
            model.crossAppModel = enterRoomResp.extraMatchVO;
        } else {
            model.crossAppModel = enterRoomParams.crossAppModel;
        }
        model.authRoomInfo = enterRoomResp.extraRoomVO;
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
            case SceneType.QUIZ:
                return new Intent(context, QuizActivity.class);
            case SceneType.DANMAKU:
                return new Intent(context, DanmakuActivity.class);
            case SceneType.DISCO:
                return new Intent(context, DiscoActivity.class);
            case SceneType.CROSS_APP_AUTH:
                return new Intent(context, CrossAppAuthActivity.class);
            case SceneType.LEAGUE:
                return new Intent(context, LeagueActivity.class);
            case SceneType.CROSS_APP_MATCH:
                return new Intent(context, CrossAppMatchActivity.class);
//            case SceneType.TALENT:
//                return new Intent(context, TalentRoomActivity.class);
//            case SceneType.ONE_ONE:
//                return new Intent(context, OneOneActivity.class);
//            case SceneType.SHOW:
//                return new Intent(context, ShowActivity.class);
            case SceneType.AUDIO:
            default:
                return new Intent(context, AudioRoomActivity.class);
        }
    }

}

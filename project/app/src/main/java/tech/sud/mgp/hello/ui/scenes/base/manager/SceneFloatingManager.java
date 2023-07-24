package tech.sud.mgp.hello.ui.scenes.base.manager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;

import tech.sud.mgp.hello.ui.common.constant.RequestKey;
import tech.sud.mgp.hello.ui.main.base.constant.SceneType;
import tech.sud.mgp.hello.ui.scenes.base.model.RoomInfoModel;
import tech.sud.mgp.hello.ui.scenes.base.widget.view.floating.DanmakuRoomFloatingView;
import tech.sud.mgp.hello.ui.scenes.base.widget.view.floating.IRoomFloating;
import tech.sud.mgp.hello.ui.scenes.base.widget.view.floating.RoomFloatingView;

/**
 * 房间内的悬浮窗业务处理
 */
public class SceneFloatingManager extends BaseServiceManager {

    private IRoomFloating floatingView;
    private RoomInfoModel roomInfoModel;
    private Class<? extends Activity> startClass;

    private final SceneRoomServiceManager parentManager;

    public SceneFloatingManager(SceneRoomServiceManager sceneRoomServiceManager) {
        super();
        this.parentManager = sceneRoomServiceManager;
    }

    /** 显示悬浮窗 */
    public void showFloating(Context context, RoomInfoModel model,
                             Class<? extends Activity> startClass,
                             View.OnClickListener shutdownOnClickListener) {
        roomInfoModel = model;
        this.startClass = startClass;
        if (model.sceneType == SceneType.DANMAKU || model.sceneType == SceneType.VERTICAL_DANMAKU) {
            showDanmakuFloating(context, model, startClass, shutdownOnClickListener);
        } else {
            showDefaultFloating(context, model, startClass, shutdownOnClickListener);
        }
    }

    /** 弹幕场景下的弹窗 */
    private void showDanmakuFloating(Context context, RoomInfoModel model, Class<? extends Activity> startClass,
                                     View.OnClickListener shutdownOnClickListener) {
        if (floatingView != null) return;
        DanmakuRoomFloatingView view = new DanmakuRoomFloatingView(context);
        view.setRoomInfoModel(model);
        view.setShutdownListener(shutdownOnClickListener);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickFloatingView(context, startClass);
            }
        });
        view.show();
        floatingView = view;
        String streamId = getStreamId();
        if (!TextUtils.isEmpty(streamId)) {
            parentManager.sceneEngineManager.stopVideo(streamId);
            parentManager.sceneEngineManager.startVideo(streamId, null, view.getVideoView());
        }
    }

    private void clickFloatingView(Context context, Class<? extends Activity> startClass) {
        dismissFloating();
        Intent intent = new Intent(context, startClass);
        intent.putExtra(RequestKey.KEY_IS_PENDING_INTENT, true);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    private void showDefaultFloating(Context context, RoomInfoModel model, Class<? extends Activity> startClass,
                                     View.OnClickListener shutdownOnClickListener) {
        if (floatingView != null) return;
        RoomFloatingView view = new RoomFloatingView(context);
        view.setRoomInfoModel(model);
        view.setShutdownListener(shutdownOnClickListener);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickFloatingView(context, startClass);
            }
        });
        view.show();
        floatingView = view;
    }

    /** 隐藏悬浮窗 */
    public void dismissFloating() {
        if (floatingView != null) {
            if (floatingView instanceof DanmakuRoomFloatingView) {
                DanmakuRoomFloatingView danmakuRoomFloatingView = (DanmakuRoomFloatingView) floatingView;
                String streamId = getStreamId();
                if (!TextUtils.isEmpty(streamId)) {
                    parentManager.sceneEngineManager.stopVideo(streamId, danmakuRoomFloatingView.getVideoView());
                }
            }
            floatingView.dismiss();
            floatingView = null;
        }
    }

    private String getStreamId() {
        if (roomInfoModel != null) {
            return roomInfoModel.streamId;
        }
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        dismissFloating();
    }

}
package tech.sud.mgp.hello.ui.scenes.base.manager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;

import tech.sud.mgp.hello.ui.common.constant.RequestKey;
import tech.sud.mgp.hello.ui.scenes.base.model.RoomInfoModel;
import tech.sud.mgp.hello.ui.scenes.base.widget.view.RoomFloatingView;

/**
 * 房间内的悬浮窗业务处理
 */
public class SceneFloatingManager extends BaseServiceManager {

    private RoomFloatingView floatingView;
    private RoomInfoModel roomInfoModel;
    private Class<? extends Activity> startClass;

    /** 设置数据 */
    public void setData(RoomInfoModel model, Class<? extends Activity> startClass) {
        this.roomInfoModel = model;
        this.startClass = startClass;
    }

    /** 显示悬浮窗 */
    public void showFloating(Context context, RoomInfoModel model,
                             Class<? extends Activity> startClass,
                             View.OnClickListener shutdownOnClickListener) {
        if (floatingView != null) return;
        floatingView = new RoomFloatingView(context);
        floatingView.setRoomInfoModel(model);
        floatingView.setShutdownListener(shutdownOnClickListener);
        floatingView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, startClass);
                intent.putExtra(RequestKey.KEY_IS_PENDING_INTENT, true);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
                dismissFloating();
            }
        });
        floatingView.show();
    }

    /** 隐藏悬浮窗 */
    public void dismissFloating() {
        if (floatingView != null) {
            floatingView.dismiss();
            floatingView = null;
        }
    }

}
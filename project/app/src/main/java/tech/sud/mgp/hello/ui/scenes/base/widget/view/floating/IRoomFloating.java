package tech.sud.mgp.hello.ui.scenes.base.widget.view.floating;

import android.view.View;

import tech.sud.mgp.hello.ui.scenes.base.model.RoomInfoModel;

public interface IRoomFloating {

    /** 显示 */
    void show();

    /** 隐藏 */
    void dismiss();

    /** 设置房间数据 */
    void setRoomInfoModel(RoomInfoModel model);

    /** 设置关闭按钮监听器 */
    void setShutdownListener(View.OnClickListener listener);

}

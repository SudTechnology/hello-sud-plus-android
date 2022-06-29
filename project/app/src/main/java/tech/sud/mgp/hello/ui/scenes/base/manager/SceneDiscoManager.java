package tech.sud.mgp.hello.ui.scenes.base.manager;

import android.app.Application;

import com.blankj.utilcode.util.Utils;

import tech.sud.mgp.SudMGPWrapper.state.SudMGPAPPState;
import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.ui.scenes.base.service.SceneRoomServiceCallback;
import tech.sud.mgp.hello.ui.scenes.disco.viewmodel.DiscoActionHelper;

/**
 * 蹦迪场景 内的业务逻辑
 */
public class SceneDiscoManager extends BaseServiceManager {

    private SceneRoomServiceManager parentManager;
    private final DiscoActionHelper helper = new DiscoActionHelper(); // 操作蹦迪动作助手

    public SceneDiscoManager(SceneRoomServiceManager sceneRoomServiceManager) {
        super();
        this.parentManager = sceneRoomServiceManager;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        parentManager.sceneChatManager.addSendMsgListener(sendMsgListener);
    }

    /** 发送公屏监听 */
    private final SceneChatManager.SendMsgListener sendMsgListener = new SceneChatManager.SendMsgListener() {
        @Override
        public void onSendMsgCompleted(String msg) {
            checkChatMsgHit(msg);
        }
    };

    /** 检查是否命中 */
    private void checkChatMsgHit(String msg) {
        Application context = Utils.getApp();
        if (context.getString(R.string.move).equals(msg)) { // 移动
            triggerMove();
        } else if (context.getString(R.string.god).equals(msg)) { // 上天
            triggerGod();
        } else if (context.getString(R.string.change_role).equals(msg)) { // 换角色
            triggerChangeRole();
        } else if (context.getString(R.string.go_to_work).equals(msg)) { // 上班
            triggerGoToWork();
        } else if (context.getString(R.string.get_off_work).equals(msg)) { // 下班
            triggerGetOffWork();
        } else if (context.getString(R.string.focus).equals(msg)) { // 聚焦
            triggerFocus();
        }
    }

    /** 触发【移动】 */
    private void triggerMove() {
        callbackAction(helper.roleMove(null, null));
    }

    /** 触发【上天】 */
    private void triggerGod() {
        callbackAction(helper.roleFly(3));
    }

    /** 触发【换角色】 */
    private void triggerChangeRole() {
        callbackAction(helper.changeRole(null));
    }

    /** 触发【上班】 */
    private void triggerGoToWork() {
        int selfMicIndex = parentManager.sceneMicManager.findSelfMicIndex();
        if (selfMicIndex >= 0) {
            callbackAction(helper.joinAnchor(null));
        }
    }

    /** 触发【下班】 */
    private void triggerGetOffWork() {
        callbackAction(helper.leaveAnchor(null));
    }

    /** 触发【聚焦】 */
    private void triggerFocus() {
        callbackAction(helper.roleFocus(4, false));
    }

    private void callbackAction(String dataJson) {
        SceneRoomServiceCallback callback = parentManager.getCallback();
        if (callback != null) {
            callback.notifyStateChange(SudMGPAPPState.APP_COMMON_GAME_DISCO_ACTION, dataJson, null);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        parentManager.sceneChatManager.removeSendMsgListener(sendMsgListener);
    }

}

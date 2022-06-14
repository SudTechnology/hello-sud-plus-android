package tech.sud.mgp.hello.ui.scenes.base.manager;

import com.blankj.utilcode.util.Utils;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.ui.scenes.base.manager.SceneCommandManager.QuizBetCommandListener;
import tech.sud.mgp.hello.ui.scenes.base.model.UserInfo;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.quiz.QuizBetModel;

/**
 * 竞猜场景
 */
public class SceneQuizManager extends BaseServiceManager {

    private final SceneRoomServiceManager parentManager;

    public SceneQuizManager(SceneRoomServiceManager sceneRoomServiceManager) {
        super();
        this.parentManager = sceneRoomServiceManager;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        parentManager.sceneEngineManager.setCommandListener(quizBetCommandListener);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        parentManager.sceneEngineManager.removeCommandListener(quizBetCommandListener);
    }

    private final QuizBetCommandListener quizBetCommandListener = new QuizBetCommandListener() {
        @Override
        public void onRecvCommand(QuizBetModel model, String userID) {
            addQuizBetChatMsg(model);
        }
    };

    /** 添加公屏消息 */
    public void addQuizBetChatMsg(QuizBetModel model) {
        for (UserInfo userInfo : model.recUser) {
            if (userInfo.userID == null) {
                continue;
            }
            String msg;
            if (userInfo.userID.equals(model.sendUser.userID)) {
                // 自己给自己下注了
                msg = Utils.getApp().getString(R.string.quiz_guess_i_win_msg, model.sendUser.name);
            } else {
                // 给别人下注
                msg = Utils.getApp().getString(R.string.quiz_bet_msg, model.sendUser.name, userInfo.name);
            }
            parentManager.sceneChatManager.addMsg(msg);
        }
    }

}

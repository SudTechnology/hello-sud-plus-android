package tech.sud.mgp.game.middle.utils;

import android.text.TextUtils;

import com.blankj.utilcode.util.GsonUtils;

import tech.sud.mgp.game.middle.model.GameChatModel;
import tech.sud.mgp.game.middle.model.GameChatMsgModel;
import tech.sud.mgp.game.middle.model.GameMessageModel;

/**
 * 游戏通用状态工具类
 */
public class GameCommonStateUtils {

    /**
     * 解析游戏侧发送过来的公屏消息状态
     *
     * @param dataJson
     * @return
     */
    public static GameMessageModel parseMsgState(String dataJson) {
        try {
            GameChatModel gameChatModel = GsonUtils.fromJson(dataJson, GameChatModel.class);
            String msg = parseGameChatModel(gameChatModel);
            if (!TextUtils.isEmpty(msg)) {
                GameMessageModel gameMessageModel = new GameMessageModel();
                gameMessageModel.type = gameChatModel.type;
                gameMessageModel.msg = msg;
                return gameMessageModel;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 解析公屏消息
     *
     * @param model
     * @return
     */
    private static String parseGameChatModel(GameChatModel model) {
        if (model == null || model.msg == null || model.msg.size() == 0) {
            return null;
        }
        StringBuffer sb = new StringBuffer();
        for (GameChatMsgModel msgModel : model.msg) {
            switch (msgModel.phrase) {
                case 1:
                    if (msgModel.text != null && msgModel.text.zh_CN != null) {
                        sb.append(msgModel.text.zh_CN);
                    }
                    break;
                case 2:
                    if (msgModel.user != null && msgModel.user.name != null) {
                        sb.append(msgModel.user.name);
                    }
                    break;
            }
        }
        return sb.toString();
    }

}

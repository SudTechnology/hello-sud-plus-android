package tech.sud.mgp.hello.ui.game.middle.utils;

import android.text.TextUtils;

import com.blankj.utilcode.util.GsonUtils;

import tech.sud.mgp.hello.ui.game.middle.model.GameASRModel;
import tech.sud.mgp.hello.ui.game.middle.model.GameKeyWordModel;
import tech.sud.mgp.hello.ui.game.middle.model.GameMessageModel;
import tech.sud.mgp.hello.ui.game.middle.model.GameChatModel;
import tech.sud.mgp.hello.ui.game.middle.model.GameChatMsgModel;

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

    /**
     * 解析游戏侧发送过来的关键字状态
     *
     * @param dataJson
     * @return
     */
    public static String parseKeywordState(String dataJson) {
        try {
            GameKeyWordModel gameKeyWordModel = GsonUtils.fromJson(dataJson, GameKeyWordModel.class);
            return gameKeyWordModel.word;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 解析游戏侧发送过来的ASR状态
     *
     * @param dataJson
     * @return
     */
    public static boolean parseASRState(String dataJson) {
        try {
            GameASRModel asrState = GsonUtils.fromJson(dataJson, GameASRModel.class);
            return asrState.isOpen;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

}
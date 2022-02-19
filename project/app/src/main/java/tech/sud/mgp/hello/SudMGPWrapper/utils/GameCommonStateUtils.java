package tech.sud.mgp.hello.SudMGPWrapper.utils;

import com.blankj.utilcode.util.GsonUtils;

import tech.sud.mgp.hello.SudMGPWrapper.state.SudMGPMGState;

/**
 * 游戏通用状态工具类
 */
public class GameCommonStateUtils {

    /**
     * 解析游戏侧发送过来的公屏消息状态
     *
     * @param dataJson
     * @return 返回解析出来的消息文本
     */
    public static String parseMsgState(String dataJson) {
        try {
            SudMGPMGState.MGCommonPublicMessage message = GsonUtils.fromJson(dataJson, SudMGPMGState.MGCommonPublicMessage.class);
            return parseMGCommonPublicMessage(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 解析公屏消息
     *
     * @param publicMessage
     * @return
     */
    public static String parseMGCommonPublicMessage(SudMGPMGState.MGCommonPublicMessage publicMessage) {
        if (publicMessage == null || publicMessage.msg == null || publicMessage.msg.size() == 0) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for (SudMGPMGState.MGCommonPublicMessage.MGCommonPublicMessageMsg msgModel : publicMessage.msg) {
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
    public static SudMGPMGState.MGCommonKeyWordToHit parseKeywordState(String dataJson) {
        try {
            return GsonUtils.fromJson(dataJson, SudMGPMGState.MGCommonKeyWordToHit.class);
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
            SudMGPMGState.MGCommonGameASR asrState = GsonUtils.fromJson(dataJson, SudMGPMGState.MGCommonGameASR.class);
            return asrState.isOpen;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

}

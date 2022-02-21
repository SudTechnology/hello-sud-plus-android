/*
 * Copyright © Sud.Tech
 * https://sud.tech
 */

package tech.sud.mgp.hello.SudMGPWrapper.utils;

import tech.sud.mgp.hello.SudMGPWrapper.state.SudMGPMGState;

/**
 * 游戏通用状态工具类
 */
public class GameCommonStateUtils {

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

}

/*
 * Copyright © Sud.Tech
 * https://sud.tech
 */

package tech.sud.mgp.SudMGPWrapper.utils;

import android.text.TextUtils;

import tech.sud.mgp.SudMGPWrapper.state.SudMGPMGState;

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
    public static String parseMGCommonPublicMessage(SudMGPMGState.MGCommonPublicMessage publicMessage, String languageCode) {
        if (publicMessage == null || publicMessage.msg == null || publicMessage.msg.size() == 0) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for (SudMGPMGState.MGCommonPublicMessage.MGCommonPublicMessageMsg msgModel : publicMessage.msg) {
            switch (msgModel.phrase) {
                case 1:
                    String text = parseI18nText(languageCode, msgModel.text);
                    if (text != null) {
                        sb.append(text);
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
     * 根据当前的语言码，选择对应语言的文字
     *
     * @param languageCode 语言代码
     * @param model        游戏发过来的文本对象
     * @return 返回选择的字符串
     */
    public static String parseI18nText(String languageCode, SudMGPMGState.MGCommonPublicMessage.MGCommonPublicMessageMsgText model) {
        if (model == null) return null;
        if (languageCode == null) return model.defaultStr;

        // 精准匹配
        String text = i18nPrecise(languageCode, model);

        if (TextUtils.isEmpty(text)) {
            // 如果未匹配到，则尝试模糊匹配
            if (isMatchLanguage(languageCode, "zh")) {
                return model.zh_CN;
            }
            return model.en_US;
        } else {
            return text;
        }
    }

    /** 精准匹配 */
    private static String i18nPrecise(String languageCode, SudMGPMGState.MGCommonPublicMessage.MGCommonPublicMessageMsgText model) {
        switch (languageCode) {
            case "zh-CN":
                return model.zh_CN;
            case "zh-HK":
                return model.zh_HK;
            case "zh-MO":
                return model.zh_MO;
            case "zh-SG":
                return model.zh_SG;
            case "zh-TW":
                return model.zh_TW;
            case "en-US":
                return model.en_US;
            case "en-GB":
                return model.en_GB;
            case "ms-BN":
                return model.ms_BN;
            case "ms-MY":
                return model.ms_MY;
            case "vi-VN":
                return model.vi_VN;
            case "id-ID":
                return model.id_ID;
            case "es-ES":
                return model.es_ES;
            case "ja-JP":
                return model.ja_JP;
            case "ko-KR":
                return model.ko_KR;
            case "th-TH":
                return model.th_TH;
            case "ar-SA":
                return model.ar_SA;
            case "ur-PK":
                return model.ur_PK;
            case "tr-TR":
                return model.tr_TR;
        }
        return null;
    }

    /**
     * 判断是否是该语言
     */
    private static boolean isMatchLanguage(String languageCode, String matchLanguageCode) {
        if (languageCode != null && matchLanguageCode != null) {
            return matchLanguageCode.equals(languageCode) || languageCode.startsWith(matchLanguageCode + "-");
        }
        return false;
    }

}

package tech.sud.mgp.hello.service.main.resp;

import java.util.List;

public class AiInfoModel {
    public String aiUid; // AI 用户uid
    public String aiId; // AI id
    public String nickname; // 名称
    public String birthday; // 生日
    public String bloodType; // 血型
    public String mbti; // mbti
    public List<String> personalities; // 性格特点
    public List<String> languageStyles; // 语言风格
    public List<String> languageDetailStyles; // 语言详细风格
    public int voiceStatus; // 音色状态,  1: training, 2: success, 3: failed
    public String demoAudioData; // 试听音频base64
    public String demoAudioText; // 试听音频文本
    public int status; // 状态 0：关闭， 1：开启
}

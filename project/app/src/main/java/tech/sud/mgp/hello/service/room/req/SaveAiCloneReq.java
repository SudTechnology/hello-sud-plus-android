package tech.sud.mgp.hello.service.room.req;

import java.util.List;

public class SaveAiCloneReq {
    public String nickname; // 名称
    public String birthday; // 生日 (yyyy-MM-dd)
    public String bloodType; // 血型
    public String mbti; // mbti
    public List<String> personalities; // 性格特点
    public List<String> languageStyles; // 语言风格
    public List<String> languageDetailStyles; // 语言详细风格
    public String audioData; // 音频base64数据
    public String audioFormat; // 音频格式:  wav,  mp3
}

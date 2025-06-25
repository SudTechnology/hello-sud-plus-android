package tech.sud.mgp.hello.service.main.resp;

import java.util.List;

public class GetAiCloneResp {
    public AiInfoModel aiInfo; // AI 信息
    public List<String> bloodTypeOptions; // 血型选项
    public List<String> mbtiOptions; // mbti选项
    public List<String> personalityOptions; // 性格特点选项
    public List<String> languageStyleOptions; // 语言风格选项
    public List<String> languageDetailStyleOptions; // 语言详细风格选项
    public String audioText; // 训练音频文本
}

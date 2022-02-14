package tech.sud.mgp.hello.ui.game.middle.model;

import com.google.gson.annotations.SerializedName;

public class GameChatTextModel {
    @SerializedName(value = "default")
    public String defaultStr;
    @SerializedName(value = "zh-CN")
    public String zh_CN;
    @SerializedName(value = "en_US")
    public String en_US;
}

package tech.sud.mgp.hello.ui.scenes.base.model;

/**
 * 语聊房麦位数据模型
 */
public class AudioRoomMicModel {

    public int micIndex;

    public long userId; // 0代表没有人
    public String nickName;
    public String avatar;
    public int roleType; // 1:房主 0：普通用户
    public String streamId; // 流id

    public Boolean isCaptain; // 是否是队长，空值不显示
    public int readyStatus; // 0没有状态，1已准备，2未准备
    public boolean isPlayingGame; // 是否正在游戏中
    public boolean isIn;//是否加入游戏
    public Boolean giftEnable = false; // 是否展示礼物icon true 展示 false 不展示

    public void clearUser() {
        userId = 0;
        nickName = null;
        avatar = null;
        roleType = 0;
        isCaptain = false;
        readyStatus = 0;
        streamId = null;
        giftEnable = false;
        isIn = false;
    }

}

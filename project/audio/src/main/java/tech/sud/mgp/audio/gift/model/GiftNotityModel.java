package tech.sud.mgp.audio.gift.model;

import tech.sud.mgp.common.model.HSUserInfo;

public class GiftNotityModel {

    private int cmd;
    private int giftId;
    private int giftCount;
    private HSUserInfo sendUser;
    private HSUserInfo toUser;

    public int getCmd() {
        return cmd;
    }

    public void setCmd(int cmd) {
        this.cmd = cmd;
    }

    public int getGiftId() {
        return giftId;
    }

    public void setGiftId(int giftId) {
        this.giftId = giftId;
    }

    public int getGiftCount() {
        return giftCount;
    }

    public void setGiftCount(int giftCount) {
        this.giftCount = giftCount;
    }

    public HSUserInfo getSendUser() {
        return sendUser;
    }

    public void setSendUser(HSUserInfo sendUser) {
        this.sendUser = sendUser;
    }

    public HSUserInfo getToUser() {
        return toUser;
    }

    public void setToUser(HSUserInfo toUser) {
        this.toUser = toUser;
    }
}

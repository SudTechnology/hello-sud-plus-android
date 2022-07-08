package tech.sud.mgp.hello.ui.scenes.orderentertainment.model;

import tech.sud.mgp.hello.ui.scenes.base.model.AudioRoomMicModel;

public class OrderMicModel {
    public AudioRoomMicModel userInfo;
    public int indexMic;
    public boolean checked;
    //是否是假数据，麦上没有人的时候要显示假数据
    public boolean isFake = false;
}

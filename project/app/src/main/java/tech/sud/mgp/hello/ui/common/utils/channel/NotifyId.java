package tech.sud.mgp.hello.ui.common.utils.channel;

/**
 * 通知id定义，可避免冲突
 */
public enum NotifyId {

    SCENE_ROOM_NOTIFY_ID(0x11), // 场景房间通知栏
    ;

    private int value;

    NotifyId(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}

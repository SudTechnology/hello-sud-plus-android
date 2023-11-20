package tech.sud.mgp.hello.ui.scenes.audio3d.model;

import java.io.Serializable;

public class EmojiModel implements Serializable {
    public String name;
    public int resId;
    public int actionId;
    public int type; // 操作类型 0:表情, 1:爆灯

    public EmojiModel() {
    }

    public EmojiModel(String name, int resId, int actionId) {
        this.name = name;
        this.resId = resId;
        this.actionId = actionId;
    }
}

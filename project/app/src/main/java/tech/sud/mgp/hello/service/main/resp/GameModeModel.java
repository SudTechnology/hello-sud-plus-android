package tech.sud.mgp.hello.service.main.resp;

import java.io.Serializable;

public class GameModeModel implements Serializable {
    public int mode; // 游戏模式
    public int[] count; // 数组0为最小人数，数组1为最大支持人数
}

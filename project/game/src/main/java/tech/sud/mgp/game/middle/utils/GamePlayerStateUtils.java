package tech.sud.mgp.game.middle.utils;

import com.blankj.utilcode.util.GsonUtils;

import tech.sud.mgp.game.middle.state.mg.player.PlayerCaptainState;

public class GamePlayerStateUtils {

    /**
     * 解析队长状态
     *
     * @param dataJson
     * @return
     */
    public static PlayerCaptainState parseCaptainState(String dataJson) {
        try {
            return GsonUtils.fromJson(dataJson, PlayerCaptainState.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}

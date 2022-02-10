package tech.sud.mgp.game.middle.utils;

import com.blankj.utilcode.util.GsonUtils;

import tech.sud.mgp.game.middle.state.mg.player.PlayerCaptainState;
import tech.sud.mgp.game.middle.state.mg.player.PlayerReadyState;

/**
 * 游戏玩家状态工具类
 */
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

    /**
     * 解析准备状态
     *
     * @param dataJson
     * @return
     */
    public static PlayerReadyState parseReadyState(String dataJson) {
        try {
            return GsonUtils.fromJson(dataJson, PlayerReadyState.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}

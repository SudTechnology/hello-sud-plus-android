package tech.sud.mgp.hello.service.main.resp;

import java.io.Serializable;
import java.util.List;

/**
 * 游戏描述
 */
public class GameModel implements Serializable {

    // 游戏加载类型定义
    public static final int LOAD_TYPE_SDK = 0;
    public static final int LOAD_TYPE_H5 = 1;
    public static final int LOAD_TYPE_RTMP = 2;

    public long gameId; // 游戏ID
    public String gameName; // 游戏名称
    public String gamePic; // 游戏图片
    public String homeGamePic; // 首页游戏图片
    public List<Integer> suitScene; // 适用场景
    public List<GameModeModel> gameModeList; // 游戏模式

    // region 查询竞猜游戏列表时会返回
    public int ticketCoin; // 入场门票(金币)
    public int winCoin; // 奖励(金币)
    public int gameCountDownCycle; // 游戏倒计时间隔（秒）
    // endregion 查询竞猜游戏列表时会返回

    public String leagueSceneName; // 联赛场景名称
    public String leagueScenePic; // 联赛场景图片

    public int loadType; // 游戏加载类型,0:sdk,1:h5,2:rtmp

    public int getGameMaxNumber() {
        if (gameModeList != null && gameModeList.size() > 0) {
            GameModeModel gameModeModel = gameModeList.get(0);
            if (gameModeModel.mode != 1) {
                for (GameModeModel model : gameModeList) {
                    if (model.mode == 1) {
                        gameModeModel = model;
                    }
                }
            }
            if (gameModeModel.count.length >= 2) {
                return gameModeModel.count[1];
            }
        }
        return 0;
    }
}

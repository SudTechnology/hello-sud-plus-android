package tech.sud.mgp.hello.service.main.resp;

import java.io.Serializable;
import java.util.List;

/**
 * 游戏描述
 */
public class GameModel implements Serializable {
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

}

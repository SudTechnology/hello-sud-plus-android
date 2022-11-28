package tech.sud.mgp.hello.service.main.resp;

import java.io.Serializable;
import java.util.List;

/**
 * 跨域游戏列表 返回参数
 */
public class CrossAppGameListResp implements Serializable {
    public List<GameModel> hotGameList; // 热门游戏列表
    public List<GameModel> allGameList; // 所有游戏列表
}

package tech.sud.mgp.hello.service.main.resp;

import java.util.List;

/**
 * 查询竞猜游戏列表 返回参数
 */
public class QuizGameListResp {
    public int pkCountDownCycle; // 跨房pk倒计时间隔（秒）
    public List<GameModel> quizGameInfoList; // 竞猜游戏列表
}

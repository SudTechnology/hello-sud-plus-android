package tech.sud.mgp.hello.service.main.req;

import java.util.List;

/**
 * 竞猜场景下注 请求参数
 */
public class QuizBetReq {

    public static final int QUIZ_TYPE_PK = 1;
    public static final int QUIZ_TYPE_GAME = 2;

    public int quizType; // 竞猜类型(1：跨房PK 2：游戏)
    public Long coin; // 投注金币数（跨房pk竞猜使用）
    public List<Long> supportedUserIdList; // 被支持用户id
}

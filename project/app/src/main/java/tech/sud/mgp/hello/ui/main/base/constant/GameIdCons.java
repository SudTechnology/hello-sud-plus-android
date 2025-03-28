package tech.sud.mgp.hello.ui.main.base.constant;

/**
 * 游戏id常量
 */
public class GameIdCons {
    public static final long NONE = 0; // 没有游戏
    public static final long I_GUESS_YOU_SAID = 1468434504892882946L; // 你说我猜
    public static final long DIGITAL_BOMB = 1468091457989509190L; // 数字炸弹
    public static final long YOU_DRAW_AND_I_GUESS = 1461228410184400899L; // 你画我猜
    public static final long CUSTOM_ROCKET = 1583284410804244481L; // 定制火箭
    public static final long BASEBALL = 1594978084509368321L; // 棒球
    public static final long CRAZY_RACECAR = 1649319572314173442L; // 疯狂赛车
    public static final long KING_EATERS = 1641330097642704898L; // 大胃王
    public static final long MONOPOLY = 1704460412809043970L; // 大富翁
    public static final long SOUL_STONE = 1890346721291059202L; // 振魂石

    public static boolean isInteractionGame(long gameId) {
        if (gameId == CUSTOM_ROCKET) return true;
        if (gameId == BASEBALL) return true;
        if (gameId == CRAZY_RACECAR) return true;
        if (gameId == KING_EATERS) return true;
        return false;
    }

}

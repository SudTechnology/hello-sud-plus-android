package tech.sud.mgp.hello.common.http.param;


/**
 * 接口地址
 */
public interface RequestUrl {

    /**
     * 登录
     */
    String LOGIN = "login/v1";

    /**
     * 刷新token
     * */
    String REFRESHTOKEN= "refresh-token/v1";

    /**
     * 主页接口
     */
    String GAME_LIST = "game/list/v1";

    /**
     * 房间列表
     */
    String ROOM_LIST = "room/list/v1";

    /**
     * 进入房间
     */
    String ENTER_ROOM = "room/enter-room/v1";

    /**
     * 查询麦位列表接口
     */
    String ROOM_MIC_LIST = "room/mic/list/v1";

    /**
     * 批量查询用户信息
     */
    String USER_INFO_LIST = "batch/user-info/v1";

    /**
     * 房间上下麦接口
     */
    String ROOM_MIC_SWITCH = "room/switch-mic/v1";

    /**
     * 房间列表
     */
    String ROOM_MATCH = "room/match-room/v1";

    /**
     * 查询基础配置
     */
    String GET_BASE_CONFIG = "base/config/v1";

    /**
     * 游戏登录
     */
    String GAME_LOGIN = "base/login/v1";

    /**
     * 游戏切换
     */
    String SWITCH_GAME = "room/switch-game/v1";

    /**
     * 退出房间
     */
    String EXIT_ROOM = "room/exit-room/v1";

    /**
     * 创建房间
     */
    String CREAT_ROOM = "/room/create-room/v1";

}

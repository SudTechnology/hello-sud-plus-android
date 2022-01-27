package tech.sud.mgp.common.http.param;


/**
 * 接口地址
 */
public interface RequestUrl {

    /**
     * xxx
     */
    String LOGIN = "login/v1";

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
    String ENTER_ROOM = "room/enter/v1";

    /**
     * 查询麦位列表接口
     */
    String ROOM_MIC_LIST = "room/mic/list/v1";

    /**
     * 批量查询用户信息
     */
    String USER_INFO_LIST = "batch/user_info/v1";

    /**
     * 房间上下麦接口
     */
    String ROOM_MIC_SWITCH = "room/mic/switch/v1";

    /**
     * 房间列表
     */
    String ROOM_MATCH = "room/match/v1";

}

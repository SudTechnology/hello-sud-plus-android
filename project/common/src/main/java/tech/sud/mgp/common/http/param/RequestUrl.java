package tech.sud.mgp.common.http.param;


/**
 * 接口地址
 */
public interface RequestUrl {

    String BASE_URL = "https://www.divtoss.com/";

    /**
     * xxx
     */
    String LOGIN = "api/login";

    /**
     * 主页接口
     */
    String GAMELIST = "game/list/v1";

    /**
     * 房间列表
     */
    String ROOMLIST = "room/list/v1";

    /**
     * 进入房间
     */
    String ENTERROOM = "room/enter/v1";
}

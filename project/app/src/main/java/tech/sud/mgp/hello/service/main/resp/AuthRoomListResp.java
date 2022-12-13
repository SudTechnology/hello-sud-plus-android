package tech.sud.mgp.hello.service.main.resp;

import java.util.List;

/**
 * 授权房间列表 返回参数
 */
public class AuthRoomListResp {
    public int total; // 总记录数
    public List<AuthRoomModel> roomInfos;
}

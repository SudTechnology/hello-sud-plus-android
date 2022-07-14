package tech.sud.mgp.hello.ui.main.discover;

import java.io.Serializable;

/**
 * 发现页房间数据
 */
public class DiscoverRoomModel implements Serializable {
    public String room_id; // 对方的房间id
    public long mg_id;
    public String status;
    public int player_total; // 玩家总数
    public int ob_total; // 观众视角总数
    public long last_changed_time;
    public String authSecret; // 授权码
}

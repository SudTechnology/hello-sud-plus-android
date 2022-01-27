package tech.sud.mgp.audio.example.model;

import java.io.Serializable;

public class RoomInfoModel implements Serializable {
    public Long roomId;
    public String roomName;
    public long gameId;
    public int roleType; // // --1:房主 0：普通用户
}

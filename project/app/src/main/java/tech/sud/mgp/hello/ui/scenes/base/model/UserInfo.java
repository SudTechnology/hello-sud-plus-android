package tech.sud.mgp.hello.ui.scenes.base.model;

import java.util.Objects;

public class UserInfo {
    public String userID; // 用户id
    public String name; // 用户昵称
    public String icon; // 	用户头像
    public int sex; // 用户性别[1男 2女]
    public String roomID; // 房间Id
    public boolean isAi; // 是否是机器人
    public int level; // 机器人等级 1:简单 2:适中 3:困难
    
    public long getRoomId() {
        try {
            return Long.parseLong(roomID);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public long getLongUserId() {
        try {
            return Long.parseLong(userID);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserInfo userInfo = (UserInfo) o;
        return Objects.equals(userID, userInfo.userID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userID);
    }

}

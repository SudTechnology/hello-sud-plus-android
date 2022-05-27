package tech.sud.mgp.hello.ui.scenes.base.model;

public class UserInfo {
    public String userID; // 用户id
    public String name; // 用户昵称
    public String icon; // 	用户头像
    public int sex; // 用户性别[1男 2女]
    public String roomID; // 房间Id

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

}

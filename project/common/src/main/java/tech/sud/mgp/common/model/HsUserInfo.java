package tech.sud.mgp.common.model;

public class HsUserInfo {

    private static final HsUserInfo sInstance = new HsUserInfo();

    private HsUserInfo() {
    }

    public static HsUserInfo getInstance() {
        return sInstance;
    }

    public long userId;
    public String nickName;
    public String gender; //male为男性，female为女性
}
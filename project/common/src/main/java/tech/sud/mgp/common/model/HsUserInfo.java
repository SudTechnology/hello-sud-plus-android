package tech.sud.mgp.common.model;

public class HSUserInfo {

    private static final HSUserInfo sInstance = new HSUserInfo();

    private HSUserInfo() {
    }

    public static HSUserInfo getInstance() {
        return sInstance;
    }

    public long userId;
    public String nickName;
    public String gender; //male为男性，female为女性
}
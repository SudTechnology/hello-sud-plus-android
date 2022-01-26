package tech.sud.mgp.audio.middle;

public class MediaUser {
    public String userID;
    public String userName;

    public MediaUser(String userID, String userName) {
        this.userID = userID;
        this.userName = userName;
    }

    public MediaUser(String userID) {
        this.userID = userID;
        this.userName = userID;
    }
}

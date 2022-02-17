package tech.sud.mgp.hello.rtc.audio.core;

public class AudioUser {
    public String userID;
    public String userName;

    public AudioUser(String userID, String userName) {
        this.userID = userID;
        this.userName = userName;
    }

    public AudioUser(String userID) {
        this.userID = userID;
        this.userName = userID;
    }

    public AudioUser() {
    }
}

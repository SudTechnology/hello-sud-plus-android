package tech.sud.mgp.hello.common.http.use.resp;

import java.util.List;

public class GameModel {
    String gameName;
    long gameId;
    String gamePic;
    List<Integer> suitScene;

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public long getGameId() {
        return gameId;
    }

    public void setGameId(long gameId) {
        this.gameId = gameId;
    }

    public String getGamePic() {
        return gamePic;
    }

    public void setGamePic(String gamePic) {
        this.gamePic = gamePic;
    }

    public List<Integer> getSuitScene() {
        return suitScene;
    }

    public void setSuitScene(List<Integer> suitScene) {
        this.suitScene = suitScene;
    }
}

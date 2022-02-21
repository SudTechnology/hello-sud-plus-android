package tech.sud.mgp.hello.service.main.resp;

import java.util.List;

public class GameModel {
    public String gameName;
    public long gameId;
    public String gamePic;
    public List<Integer> suitScene;
    public List<GameModeModel> gameModeList;

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

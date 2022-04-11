package tech.sud.mgp.hello.service.main.resp;

import java.util.List;

public class GameModel {
    public long gameId; // 游戏ID
    public String gameName; // 游戏名称
    public String gamePic; // 游戏图片
    public String homeGamePic; // 首页游戏图片
    public List<Integer> suitScene; // 适用场景
    public List<GameModeModel> gameModeList; // 游戏模式

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

    public String getHomeGamePic() {
        return homeGamePic;
    }

    public void setHomeGamePic(String homeGamePic) {
        this.homeGamePic = homeGamePic;
    }

    public List<GameModeModel> getGameModeList() {
        return gameModeList;
    }

    public void setGameModeList(List<GameModeModel> gameModeList) {
        this.gameModeList = gameModeList;
    }
}

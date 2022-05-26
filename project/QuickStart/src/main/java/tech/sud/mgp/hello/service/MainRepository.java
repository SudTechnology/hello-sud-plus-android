package tech.sud.mgp.hello.service;

import java.util.ArrayList;
import java.util.List;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.ui.main.GameModel;

public class MainRepository {

    /** 获取游戏列表 */
    public static List<GameModel> getGameList() {
        ArrayList<GameModel> list = new ArrayList<>();
        list.add(buildGameModel(1461227817776713700L, "碰碰我最强", R.drawable.ppwzq));
        list.add(buildGameModel(1461228379255603200L, "飞镖达人", R.drawable.fbdr));
        list.add(buildGameModel(1461228410184401000L, "你画我猜", R.drawable.nhwc));
        list.add(buildGameModel(1461297734886621200L, "五子棋", R.drawable.wzq));
        list.add(buildGameModel(1461297789198663700L, "黑白棋", R.drawable.hbq));
        list.add(buildGameModel(1468090257126719500L, "短道速滑", R.drawable.ddsh));
        list.add(buildGameModel(1468091457989509000L, "数字炸弹", R.drawable.szzd));
        list.add(buildGameModel(1468180338417074200L, "飞行棋", R.drawable.fxq));
        list.add(buildGameModel(1468434401847222300L, "扫雷", R.drawable.sl));
        list.add(buildGameModel(1468434504892883000L, "你说我猜", R.drawable.nswc));
        list.add(buildGameModel(1468434637562912800L, "数字转轮", R.drawable.szzl));
        list.add(buildGameModel(1468434723902660600L, "石头剪刀布", R.drawable.stjdb));
        list.add(buildGameModel(1472142478505271300L, "TeenPatti", R.drawable.teen_patti));
        list.add(buildGameModel(1472142559912517600L, "UMO", R.drawable.umo));
        list.add(buildGameModel(1472142640866779100L, "排雷兵", R.drawable.plb));
        list.add(buildGameModel(1472142695162044400L, "台湾麻将", R.drawable.twmj));
        list.add(buildGameModel(1472142747708285000L, "狼人杀", R.drawable.lrs));
        list.add(buildGameModel(1490944230389182500L, "友尽闯关", R.drawable.picopark));
        list.add(buildGameModel(1490944604005200000L, "大话骰", R.drawable.dice));
        list.add(buildGameModel(1494212349664686000L, "德州扑克", R.drawable.dz));
        return list;
    }

    public static GameModel buildGameModel(long gameId, String gameName, int gamePic) {
        GameModel model = new GameModel();
        model.gameId = gameId;
        model.gameName = gameName;
        model.gamePic = gamePic;
        return model;
    }

}

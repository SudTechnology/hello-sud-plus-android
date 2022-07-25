package tech.sud.mgp.hello.ui.scenes.base.model;

import java.util.ArrayList;
import java.util.List;

/**
 * 联赛 数据
 */
public class LeagueModel {
    public int schedule; // 已经进行了几局比赛，0开始
    public List<String> winner = new ArrayList<>(); // 进行比赛之后的胜出者

    public LeagueModel copy() {
        LeagueModel model = new LeagueModel();
        model.schedule = schedule;
        model.winner.addAll(winner);
        return model;
    }

}
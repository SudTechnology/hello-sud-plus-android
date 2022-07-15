package tech.sud.mgp.hello.ui.scenes.league.activity;

import tech.sud.mgp.hello.ui.scenes.audio.activity.AbsAudioRoomActivity;
import tech.sud.mgp.hello.ui.scenes.league.viewmodel.LeagueGameViewModel;

/**
 * 联赛场景 页面
 */
public class LeagueActivity extends AbsAudioRoomActivity<LeagueGameViewModel> {

    @Override
    protected LeagueGameViewModel initGameViewModel() {
        return new LeagueGameViewModel();
    }

}
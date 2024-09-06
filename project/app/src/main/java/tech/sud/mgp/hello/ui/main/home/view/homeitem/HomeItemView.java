package tech.sud.mgp.hello.ui.main.home.view.homeitem;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import tech.sud.mgp.hello.service.main.resp.GameModel;
import tech.sud.mgp.hello.service.main.resp.QuizGameListResp;
import tech.sud.mgp.hello.service.main.resp.SceneModel;
import tech.sud.mgp.hello.ui.main.base.constant.SceneType;

/**
 * 首页不同场景下的view
 */
public class HomeItemView extends FrameLayout {

    private IHomeChildItemView child;

    public HomeItemView(@NonNull Context context) {
        super(context);
    }

    public HomeItemView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public HomeItemView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /** 设置数据 */
    public void setData(SceneModel sceneModel, List<GameModel> datas, QuizGameListResp quizGameListResp) {
        View view;
        switch (sceneModel.sceneId) {
            case SceneType.DANMAKU:
                view = new HomeDanmakuItemView(getContext());
                break;
            case SceneType.VERTICAL_DANMAKU:
            case SceneType.DANMAKU_LIST_CLASS:
                view = new HomeVerticalDanmakuItemView(getContext());
                break;
            case SceneType.QUIZ:
                view = new HomeQuizItemView(getContext());
                break;
            case SceneType.DISCO:
                view = new HomeDiscoItemView(getContext());
                break;
            case SceneType.LEAGUE:
                view = new HomeLeagueItemView(getContext());
                break;
            case SceneType.CROSS_APP_MATCH:
                view = new HomeCrossAppItemView(getContext());
                break;
            case SceneType.AUDIO_3D:
                view = new HomeAudio3DItemView(getContext());
                break;
            case SceneType.AUDIO_INTERACT:
            case SceneType.REAL_TIME_SPORTS:
            case SceneType.CLASSIC_CHESS_GAME:
            case SceneType.CLASSIC_CARD_CLASS:
            case SceneType.CLASSIC_BOARD_GAMES:
            case SceneType.COMPETE_IN_A_TEAM:
            case SceneType.LEISURE_AND_ENTERTAINMENT:
            case SceneType.INTERACTIVE_GIFTS:
            case SceneType.BETTING_GAMES:
            case SceneType.LINGXIAN_GAME_ZONE:
            case SceneType.CUBE:
            case SceneType.REYOU:
            case SceneType.AI:
                view = new HomeMatchItemView(getContext());
                break;
            default:
                view = new HomeNormalItemView(getContext());
                break;
        }
        addView(view);
        child = (IHomeChildItemView) view;
        child.setData(sceneModel, datas, quizGameListResp);
    }

    /** 设置"更多活动"的点击监听 */
    public void setMoreActivityOnClickListener(OnClickListener listener) {
        child.setMoreActivityOnClickListener(listener);
    }

    /** 设置点击了游戏的监听 */
    public void setGameItemListener(GameItemListener listener) {
        child.setGameItemListener(listener);
    }

    /** 设置点击创建房间监听 */
    public void setCreatRoomClickListener(CreatRoomClickListener listener) {
        child.setCreatRoomClickListener(listener);
    }

}

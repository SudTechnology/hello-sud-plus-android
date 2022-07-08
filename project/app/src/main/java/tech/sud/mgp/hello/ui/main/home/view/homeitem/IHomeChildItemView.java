package tech.sud.mgp.hello.ui.main.home.view.homeitem;

import android.view.View;

import java.util.List;

import tech.sud.mgp.hello.service.main.resp.GameModel;
import tech.sud.mgp.hello.service.main.resp.QuizGameListResp;
import tech.sud.mgp.hello.service.main.resp.SceneModel;

/**
 * 首页itemView接口
 */
public interface IHomeChildItemView {
    /** 设置"更多活动"的点击监听 */
    default void setMoreActivityOnClickListener(View.OnClickListener listener) {
    }

    /** 设置点击了游戏的监听 */
    void setGameItemListener(GameItemListener gameItemListener);

    /** 设置点击创建房间监听 */
    void setCreatRoomClickListener(CreatRoomClickListener creatRoomClickListener);

    /** 设置数据 */
    void setData(SceneModel sceneModel, List<GameModel> datas, QuizGameListResp quizGameListResp);
}
